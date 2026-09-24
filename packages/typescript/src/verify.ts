import { readFile } from "node:fs/promises";

export class Context {
  width: number;
  height: number;
  pixels: Uint32Array;

  constructor(width: number, height: number) {
    this.width = width;
    this.height = height;
    this.pixels = new Uint32Array(width * height);
  }

  fillRect(x: number, y: number, w: number, h: number, argb: number): void {
    const color = argb >>> 0;
    for (let py = Math.max(0, y); py < Math.min(this.height, y + h); py++) {
      const row = py * this.width;
      for (let px = Math.max(0, x); px < Math.min(this.width, x + w); px++) {
        this.pixels[row + px] = color;
      }
    }
  }

  getPixel(x: number, y: number): number {
    if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
      throw new Error(`Pixel (${x},${y}) out of bounds`);
    }
    return this.pixels[y * this.width + x];
  }
}

export class SimulatedInput {
  private _held = new Set<number>();
  private _pressed = new Set<number>();

  holdKey(...codes: number[]): void {
    for (const c of codes) {
      if (!this._held.has(c)) this._pressed.add(c);
      this._held.add(c);
    }
  }

  pressKey(...codes: number[]): void {
    for (const c of codes) {
      this._pressed.add(c);
      this._held.add(c);
    }
  }

  releaseKey(...codes: number[]): void {
    for (const c of codes) this._held.delete(c);
  }

  clear(): void {
    this._held.clear();
    this._pressed.clear();
  }

  isKeyHeld(...codes: number[]): boolean {
    return codes.some((c) => this._held.has(c));
  }

  isKeyPressed(...codes: number[]): boolean {
    return codes.some((c) => this._pressed.has(c));
  }

  endFrame(): void {
    this._pressed.clear();
  }
}

export const Keys = {
  LEFT: 37,
  RIGHT: 39,
  UP: 38,
  DOWN: 40,
  SPACE: 32,
  ENTER: 13,
  ESCAPE: 27,
  A: 65,
  D: 68,
  W: 87,
  S: 83,
} as const;

export type KeyName = keyof typeof Keys;

export function resolveKey(token: string): number {
  let t = token.toUpperCase();
  if (t.startsWith("VK_")) t = t.slice(3);
  if (t in Keys) return Keys[t as KeyName];
  if (t.length === 1) return t.charCodeAt(0);
  throw new Error(`Unknown key: ${token}`);
}

export class AudioProbe {
  private static _current: AudioProbe | null = null;
  events: Array<[string, string]> = [];

  install(): this {
    AudioProbe._current = this;
    return this;
  }

  close(): void {
    if (AudioProbe._current === this) AudioProbe._current = null;
  }

  static recordPlay(name: string): void {
    if (AudioProbe._current) AudioProbe._current.events.push(["PLAY", name]);
  }

  assertPlayed(name: string): void {
    if (!this.events.some(([k, n]) => k === "PLAY" && n === name)) {
      throw new Error(`Expected play '${name}' but events=${JSON.stringify(this.events)}`);
    }
  }
}

export class FrameAssert {
  static assertPixel(ctx: Context, x: number, y: number, expected: number): void {
    const actual = ctx.getPixel(x, y);
    if (actual !== (expected >>> 0)) {
      throw new Error(
        `Pixel (${x},${y}): expected 0x${(expected >>> 0).toString(16)} but was 0x${actual.toString(16)}`
      );
    }
  }

  static hash(ctx: Context): number {
    let h = 0x811c9dc5;
    for (const p of ctx.pixels) {
      h ^= p;
      h = Math.imul(h, 0x01000193) >>> 0;
    }
    return h >>> 0;
  }
}

export class Game {
  width: number;
  height: number;
  context: Context;
  input: SimulatedInput;

  constructor(width: number, height: number) {
    this.width = width;
    this.height = height;
    this.context = new Context(width, height);
    this.input = new SimulatedInput();
  }

  init(): void {}
  update(): void {}
  render(): void {}
}

export class GameHarness {
  game: Game;
  private _initialized = false;

  constructor(factory: () => Game) {
    this.game = factory();
  }

  step(frames = 1): this {
    if (!this._initialized) {
      this.game.init();
      this._initialized = true;
    }
    for (let i = 0; i < frames; i++) {
      this.game.update();
      this.game.render();
      this.game.input.endFrame();
    }
    return this;
  }

  renderer(): Context {
    return this.game.context;
  }
}

export type PlaytestStep = { frame: number; action: string; key: number };

export class PlaytestScript {
  steps: PlaytestStep[];

  constructor(steps: PlaytestStep[]) {
    this.steps = [...steps].sort((a, b) => a.frame - b.frame);
  }

  static parse(text: string): PlaytestScript {
    const steps: PlaytestStep[] = [];
    for (const line of text.split(/\r?\n/)) {
      const t = line.trim();
      if (!t || t.startsWith("#")) continue;
      const parts = t.split(/\s+/);
      const frame = parseInt(parts[0], 10);
      const action = parts[1].toUpperCase();
      let key = 0;
      if (action !== "IDLE" && action !== "CLEAR") key = resolveKey(parts[2]);
      steps.push({ frame, action, key });
    }
    return new PlaytestScript(steps);
  }

  static async loadFile(path: string): Promise<PlaytestScript> {
    return PlaytestScript.parse(await readFile(path, "utf8"));
  }

  play(harness: GameHarness, input: SimulatedInput = harness.game.input): number {
    if (!this.steps.length) return 0;
    let frame = 0;
    let index = 0;
    const last = this.steps[this.steps.length - 1].frame;
    while (frame <= last) {
      while (index < this.steps.length && this.steps[index].frame === frame) {
        const s = this.steps[index++];
        if (s.action === "HOLD") input.holdKey(s.key);
        else if (s.action === "PRESS") input.pressKey(s.key);
        else if (s.action === "RELEASE") input.releaseKey(s.key);
        else if (s.action === "CLEAR") input.clear();
      }
      harness.step(1);
      frame++;
    }
    return frame;
  }
}
