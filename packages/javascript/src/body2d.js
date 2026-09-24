export class Body2D {
  constructor(x, y, width, height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.velocityX = 0;
    this.velocityY = 0;
    this.onGround = false;
    this.gravity = 0.5;
    this.jumpStrength = -10;
  }

  setVelocityX(vx) {
    this.velocityX = vx;
  }

  jump() {
    if (this.onGround) {
      this.velocityY = this.jumpStrength;
      this.onGround = false;
    }
  }

  step(solids) {
    this.velocityY += this.gravity;
    this.x += this.velocityX;
    this._resolveX(solids);
    this.y += this.velocityY;
    this.onGround = false;
    this._resolveY(solids);
  }

  _resolveX(solids) {
    for (const s of solids) {
      if (this._aabb(s)) {
        if (this.velocityX > 0) this.x = s.x - this.width;
        else if (this.velocityX < 0) this.x = s.x + s.width;
        this.velocityX = 0;
      }
    }
  }

  _resolveY(solids) {
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

  _aabb(s) {
    return (
      this.x < s.x + s.width &&
      this.x + this.width > s.x &&
      this.y < s.y + s.height &&
      this.y + this.height > s.y
    );
  }

  get isOnGround() {
    return this.onGround;
  }
}
