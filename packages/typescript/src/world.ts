export function toPixelInt(r: number, g: number, b: number, a = 255): number {
  return ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
}

export type RenderTarget = {
  width: number;
  height: number;
  fillRect(x: number, y: number, w: number, h: number, argb: number): void;
};

export class Actor {
  x: number;
  y: number;
  width: number;
  height: number;
  color: number;
  name: string | null = null;
  world: World | null = null;

  constructor(x: number, y: number, width: number, height: number, color: number) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.color = color;
  }

  named(name: string): this {
    this.name = name;
    return this;
  }

  tryMove(dx: number, dy: number): boolean {
    if (!this.world) {
      this.x += dx;
      this.y += dy;
      return true;
    }
    const nx = this.x + dx;
    const ny = this.y + dy;
    if (this.world.blocks(nx, ny, this.width, this.height)) return false;
    this.x = nx;
    this.y = ny;
    return true;
  }

  update(_input?: unknown): void {}
}

export class Trigger {
  x: number;
  y: number;
  width: number;
  height: number;
  onEnter: (actor: Actor) => void;
  id: string | null;
  private _inside: Actor[] = [];

  constructor(
    x: number,
    y: number,
    width: number,
    height: number,
    onEnter?: (actor: Actor) => void,
    id: string | null = null
  ) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.onEnter = onEnter ?? (() => {});
    this.id = id;
  }

  setOnEnter(fn: (actor: Actor) => void): void {
    const prev = this.onEnter;
    this.onEnter = (a) => {
      prev(a);
      fn(a);
    };
  }

  evaluate(actors: Actor[]): void {
    const now: Actor[] = [];
    for (const actor of actors) {
      if (this._overlaps(actor)) {
        now.push(actor);
        if (!this._inside.includes(actor)) this.onEnter(actor);
      }
    }
    this._inside = now;
  }

  private _overlaps(actor: Actor): boolean {
    return (
      this.x < actor.x + actor.width &&
      this.x + this.width > actor.x &&
      this.y < actor.y + actor.height &&
      this.y + this.height > actor.y
    );
  }
}

export class World {
  static readonly EMPTY = 0;
  static readonly SOLID = 1;

  cols: number;
  rows: number;
  tileSize: number;
  tiles: number[];
  actors: Actor[] = [];
  triggers: Trigger[] = [];
  clearColor: number;
  solidColor: number;

  constructor(cols: number, rows: number, tileSize = 16) {
    this.cols = cols;
    this.rows = rows;
    this.tileSize = tileSize;
    this.tiles = new Array(cols * rows).fill(World.EMPTY);
    this.clearColor = toPixelInt(20, 20, 30);
    this.solidColor = toPixelInt(60, 60, 80);
  }

  static grid(cols: number, rows: number, tileSize = 16): World {
    return new World(cols, rows, tileSize);
  }

  withClearColor(c: number): this {
    this.clearColor = c;
    return this;
  }

  withSolidColor(c: number): this {
    this.solidColor = c;
    return this;
  }

  fillBorder(tile = World.SOLID): void {
    for (let x = 0; x < this.cols; x++) {
      this.setTile(x, 0, tile);
      this.setTile(x, this.rows - 1, tile);
    }
    for (let y = 0; y < this.rows; y++) {
      this.setTile(0, y, tile);
      this.setTile(this.cols - 1, y, tile);
    }
  }

  setTile(tx: number, ty: number, tile: number): void {
    if (!this._inBounds(tx, ty)) throw new Error(`Tile out of bounds (${tx},${ty})`);
    this.tiles[tx + ty * this.cols] = tile;
  }

  getTile(tx: number, ty: number): number {
    if (!this._inBounds(tx, ty)) return World.SOLID;
    return this.tiles[tx + ty * this.cols];
  }

  isSolid(tx: number, ty: number): boolean {
    return this.getTile(tx, ty) === World.SOLID;
  }

  blocks(x: number, y: number, w: number, h: number): boolean {
    const minTx = Math.floor(x / this.tileSize);
    const maxTx = Math.floor((x + w - 1) / this.tileSize);
    const minTy = Math.floor(y / this.tileSize);
    const maxTy = Math.floor((y + h - 1) / this.tileSize);
    for (let tx = minTx; tx <= maxTx; tx++) {
      for (let ty = minTy; ty <= maxTy; ty++) {
        if (this.isSolid(tx, ty)) return true;
      }
    }
    return false;
  }

  add(actor: Actor): void {
    this.actors.push(actor);
    actor.world = this;
  }

  removeActor(name: string): Actor | null {
    const found = this.findActor(name);
    if (found) {
      this.actors = this.actors.filter((a) => a !== found);
      found.world = null;
    }
    return found;
  }

  addTrigger(trigger: Trigger): void {
    this.triggers.push(trigger);
  }

  findActor(name: string): Actor | null {
    return this.actors.find((a) => a.name === name) ?? null;
  }

  findTrigger(id: string): Trigger | null {
    return this.triggers.find((t) => t.id === id) ?? null;
  }

  update(input?: unknown): void {
    for (const a of this.actors) a.update(input);
    for (const t of this.triggers) t.evaluate(this.actors);
  }

  render(ctx: RenderTarget): void {
    ctx.fillRect(0, 0, ctx.width, ctx.height, this.clearColor);
    for (let ty = 0; ty < this.rows; ty++) {
      for (let tx = 0; tx < this.cols; tx++) {
        if (this.isSolid(tx, ty)) {
          ctx.fillRect(tx * this.tileSize, ty * this.tileSize, this.tileSize, this.tileSize, this.solidColor);
        }
      }
    }
    for (const a of this.actors) {
      ctx.fillRect(a.x, a.y, a.width, a.height, a.color);
    }
  }

  private _inBounds(tx: number, ty: number): boolean {
    return tx >= 0 && ty >= 0 && tx < this.cols && ty < this.rows;
  }
}
