export class Context {
  constructor(width, height) {
    this.width = width;
    this.height = height;
    this.pixels = new Uint32Array(width * height);
  }

  fillRect(x, y, w, h, argb) {
    const color = argb >>> 0;
    for (let py = Math.max(0, y); py < Math.min(this.height, y + h); py++) {
      const row = py * this.width;
      for (let px = Math.max(0, x); px < Math.min(this.width, x + w); px++) {
        this.pixels[row + px] = color;
      }
    }
  }

  getPixel(x, y) {
    if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
      throw new Error(`Pixel (${x},${y}) out of bounds`);
    }
    return this.pixels[y * this.width + x];
  }
}

export class SimulatedInput {
  constructor() {
    this._held = new Set();
    this._pressed = new Set();
  }

  holdKey(...codes) {
    for (const c of codes) {
      if (!this._held.has(c)) this._pressed.add(c);
      this._held.add(c);
    }
  }

  pressKey(...codes) {
    for (const c of codes) {
      this._pressed.add(c);
      this._held.add(c);
    }
  }

  releaseKey(...codes) {
    for (const c of codes) this._held.delete(c);
  }

  clear() {
    this._held.clear();
    this._pressed.clear();
  }

  isKeyHeld(...codes) {
    return codes.some((c) => this._held.has(c));
  }

  isKeyPressed(...codes) {
    return codes.some((c) => this._pressed.has(c));
  }

  endFrame() {
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
};

export function resolveKey(token) {
  let t = token.toUpperCase();
  if (t.startsWith("VK_")) t = t.slice(3);
  if (Keys[t] != null) return Keys[t];
  if (t.length === 1) return t.charCodeAt(0);
  throw new Error(`Unknown key: ${token}`);
}

export class AudioProbe {
  static _current = null;

  constructor() {
    this.events = [];
  }

  install() {
    AudioProbe._current = this;
    return this;
  }

  close() {
    if (AudioProbe._current === this) AudioProbe._current = null;
  }

  static recordPlay(name) {
    if (AudioProbe._current) AudioProbe._current.events.push(["PLAY", name]);
  }

  assertPlayed(name) {
    if (!this.events.some(([k, n]) => k === "PLAY" && n === name)) {
      throw new Error(`Expected play '${name}' but events=${JSON.stringify(this.events)}`);
    }
  }
}

export class FrameAssert {
  static assertPixel(ctx, x, y, expected) {
    const actual = ctx.getPixel(x, y);
    if (actual !== (expected >>> 0)) {
      throw new Error(
        `Pixel (${x},${y}): expected 0x${(expected >>> 0).toString(16)} but was 0x${actual.toString(16)}`
      );
    }
  }

  static hash(ctx) {
    let h = 0x811c9dc5;
    for (const p of ctx.pixels) {
      h ^= p;
      h = Math.imul(h, 0x01000193) >>> 0;
    }
    return h >>> 0;
  }
}

export class Game {
  constructor(width, height) {
    this.width = width;
    this.height = height;
    this.context = new Context(width, height);
    this.input = new SimulatedInput();
  }

  init() {}
  update() {}
  render() {}
}

export class GameHarness {
  constructor(factory) {
    this.game = factory();
    this._initialized = false;
  }

  step(frames = 1) {
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

  renderer() {
    return this.game.context;
  }
}

export class PlaytestScript {
  constructor(steps) {
    this.steps = [...steps].sort((a, b) => a.frame - b.frame);
  }

  static parse(text) {
    const steps = [];
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

  static async loadFile(path) {
    const { readFile } = await import("node:fs/promises");
    return PlaytestScript.parse(await readFile(path, "utf8"));
  }

  play(harness, input = harness.game.input) {
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
