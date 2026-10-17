export interface Solid {
  x: number;
  y: number;
  width: number;
  height: number;
}

export class Body2D {
  x: number;
  y: number;
  width: number;
  height: number;
  velocityX = 0;
  velocityY = 0;
  onGround = false;
  gravity = 0.5;
  jumpStrength = -10;

  constructor(x: number, y: number, width: number, height: number) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  setVelocityX(vx: number): void {
    this.velocityX = vx;
  }

  jump(): void {
    if (this.onGround) {
      this.velocityY = this.jumpStrength;
      this.onGround = false;
    }
  }

  step(solids: Solid[]): void {
    this.velocityY += this.gravity;
    this.x += this.velocityX;
    this._resolveX(solids);
    this.y += this.velocityY;
    this.onGround = false;
    this._resolveY(solids);
  }

  private _resolveX(solids: Solid[]): void {
    for (const s of solids) {
      if (this._aabb(s)) {
        if (this.velocityX > 0) this.x = s.x - this.width;
        else if (this.velocityX < 0) this.x = s.x + s.width;
        this.velocityX = 0;
      }
    }
  }

  private _resolveY(solids: Solid[]): void {
    for (const s of solids) {
      if (this._aabb(s)) {
        if (this.velocityY > 0) {
          this.y = s.y - this.height;
          this.velocityY = 0;
          this.onGround = true;
        } else if (this.velocityY < 0) {
          this.y = s.y + s.height;
          this.velocityY = 0;
        }
      }
    }
  }

  private _aabb(s: Solid): boolean {
    return (
      this.x < s.x + s.width &&
      this.x + this.width > s.x &&
      this.y < s.y + s.height &&
      this.y + this.height > s.y
    );
  }

  get isOnGround(): boolean {
    return this.onGround;
  }
}
