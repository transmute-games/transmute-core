export function toPixelInt(r, g, b, a = 255) {
  return ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
}

export class Actor {
  constructor(x, y, width, height, color) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.color = color;
    this.name = null;
    this.world = null;
  }

  named(name) {
    this.name = name;
    return this;
  }

  tryMove(dx, dy) {
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

  update(_input) {}
}

export class Trigger {
  constructor(x, y, width, height, onEnter, id = null) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.onEnter = onEnter || (() => {});
    this.id = id;
    this._inside = [];
  }

  setOnEnter(fn) {
    const prev = this.onEnter;
    this.onEnter = (a) => {
      prev(a);
      fn(a);
    };
  }

  evaluate(actors) {
    const now = [];
    for (const actor of actors) {
      if (this._overlaps(actor)) {
        now.push(actor);
        if (!this._inside.includes(actor)) this.onEnter(actor);
      }
    }
    this._inside = now;
  }

  _overlaps(actor) {
    return (
      this.x < actor.x + actor.width &&
      this.x + this.width > actor.x &&
      this.y < actor.y + actor.height &&
      this.y + this.height > actor.y
    );
  }
}

export class World {
  static EMPTY = 0;
  static SOLID = 1;

  constructor(cols, rows, tileSize = 16) {
    this.cols = cols;
    this.rows = rows;
    this.tileSize = tileSize;
    this.tiles = new Array(cols * rows).fill(World.EMPTY);
    this.actors = [];
    this.triggers = [];
    this.clearColor = toPixelInt(20, 20, 30);
    this.solidColor = toPixelInt(60, 60, 80);
  }

  static grid(cols, rows, tileSize = 16) {
    return new World(cols, rows, tileSize);
  }

  withClearColor(c) {
    this.clearColor = c;
    return this;
  }

  withSolidColor(c) {
    this.solidColor = c;
    return this;
  }

  fillBorder(tile = World.SOLID) {
    for (let x = 0; x < this.cols; x++) {
      this.setTile(x, 0, tile);
      this.setTile(x, this.rows - 1, tile);
    }
    for (let y = 0; y < this.rows; y++) {
      this.setTile(0, y, tile);
      this.setTile(this.cols - 1, y, tile);
    }
  }

  setTile(tx, ty, tile) {
    if (!this._inBounds(tx, ty)) throw new Error(`Tile out of bounds (${tx},${ty})`);
    this.tiles[tx + ty * this.cols] = tile;
  }

  getTile(tx, ty) {
    if (!this._inBounds(tx, ty)) return World.SOLID;
    return this.tiles[tx + ty * this.cols];
  }

  isSolid(tx, ty) {
    return this.getTile(tx, ty) === World.SOLID;
  }

  blocks(x, y, w, h) {
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

  add(actor) {
    this.actors.push(actor);
    actor.world = this;
  }

  removeActor(name) {
    const found = this.findActor(name);
    if (found) {
      this.actors = this.actors.filter((a) => a !== found);
      found.world = null;
    }
    return found;
  }

  addTrigger(trigger) {
    this.triggers.push(trigger);
  }

  findActor(name) {
    return this.actors.find((a) => a.name === name) || null;
  }

  findTrigger(id) {
    return this.triggers.find((t) => t.id === id) || null;
  }

  update(input) {
    for (const a of this.actors) a.update(input);
    for (const t of this.triggers) t.evaluate(this.actors);
  }

  render(ctx) {
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

  _inBounds(tx, ty) {
    return tx >= 0 && ty >= 0 && tx < this.cols && ty < this.rows;
  }
}
