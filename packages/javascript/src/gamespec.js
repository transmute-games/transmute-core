import { Actor, Trigger, World, toPixelInt } from "./world.js";
import { AudioProbe } from "./verify.js";

export class GameSpec {
  constructor(raw) {
    this.raw = raw;
    this.title = raw.title || "Game";
    this.version = raw.version || "1.0.0";
    this.width = parseInt(raw.width || "320", 10);
    this.height = parseInt(raw.height || "180", 10);
    this.scale = parseInt(raw.scale || "3", 10);
    this.headless = (raw.headless || "false") === "true";
    const r = parseInt(raw["clear.r"] || "32", 10);
    const g = parseInt(raw["clear.g"] || "32", 10);
    const b = parseInt(raw["clear.b"] || "64", 10);
    const a = parseInt(raw["clear.a"] || "255", 10);
    this.clearColor = toPixelInt(r, g, b, a);
  }

  static parse(text) {
    const raw = {};
    for (const line of text.split(/\r?\n/)) {
      const t = line.trim();
      if (!t || t.startsWith("#") || !t.includes("=")) continue;
      const i = t.indexOf("=");
      raw[t.slice(0, i).trim()] = t.slice(i + 1).trim();
    }
    return new GameSpec(raw);
  }

  static async loadFile(path) {
    const { readFile } = await import("node:fs/promises");
    return GameSpec.parse(await readFile(path, "utf8"));
  }

  getInitialState() {
    return (this.raw["state.initial"] || "play").trim();
  }

  createWorld() {
    if (!this.raw["world.cols"] || !this.raw["world.rows"]) return null;
    const cols = parseInt(this.raw["world.cols"], 10);
    const rows = parseInt(this.raw["world.rows"], 10);
    const tile = parseInt(this.raw["world.tile"] || "16", 10);
    const world = World.grid(cols, rows, tile).withClearColor(this.clearColor);
    if ((this.raw["world.border"] || "false") === "true") world.fillBorder(World.SOLID);
    const solids = (this.raw["world.solid"] || "").trim();
    if (solids) {
      for (const pair of solids.split(";")) {
        const p = pair.trim();
        if (!p) continue;
        const [x, y] = p.split(",").map((s) => parseInt(s.trim(), 10));
        world.setTile(x, y, World.SOLID);
      }
    }
    this._applySpawns(world, tile);
    this._applyTriggers(world, tile);
    return world;
  }

  _baseNames(prefix) {
    const names = [];
    const seen = new Set();
    for (const key of Object.keys(this.raw)) {
      if (!key.startsWith(prefix)) continue;
      const rest = key.slice(prefix.length);
      const name = rest.split(".")[0];
      if (name && !seen.has(name)) {
        seen.add(name);
        names.push(name);
      }
    }
    return names;
  }

  _applySpawns(world, tile) {
    for (const name of this._baseNames("spawn.")) {
      const pos = this.raw[`spawn.${name}`];
      if (!pos) continue;
      const [tx, ty] = pos.split(",").map((s) => parseInt(s.trim(), 10));
      const tw = parseInt(this.raw[`spawn.${name}.w`] || "1", 10);
      const th = parseInt(this.raw[`spawn.${name}.h`] || "1", 10);
      const color = this._parseColor(this.raw[`spawn.${name}.color`], toPixelInt(100, 150, 255));
      world.add(new Actor(tx * tile, ty * tile, tw * tile, th * tile, color).named(name));
    }
  }

  _applyTriggers(world, tile) {
    for (const name of this._baseNames("trigger.")) {
      const pos = this.raw[`trigger.${name}`];
      if (!pos) continue;
      const [tx, ty] = pos.split(",").map((s) => parseInt(s.trim(), 10));
      const tw = parseInt(this.raw[`trigger.${name}.w`] || "1", 10);
      const th = parseInt(this.raw[`trigger.${name}.h`] || "1", 10);
      const audio = this.raw[`trigger.${name}.audio`];
      world.addTrigger(
        new Trigger(
          tx * tile,
          ty * tile,
          tw * tile,
          th * tile,
          () => {
            if (audio) AudioProbe.recordPlay(audio);
          },
          name
        )
      );
    }
  }

  _parseColor(rgb, fallback) {
    if (!rgb) return fallback;
    const parts = rgb.split(",").map((s) => parseInt(s.trim(), 10));
    if (parts.length < 3) return fallback;
    return toPixelInt(parts[0], parts[1], parts[2], parts[3] ?? 255);
  }
}
