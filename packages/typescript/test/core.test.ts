import test from "node:test";
import assert from "node:assert/strict";
import { Actor, Body2D, FrameAssert, Game, GameHarness, GameSpec, World, toPixelInt } from "../dist/index.js";

test("gamespec world", () => {
  const spec = GameSpec.parse(`
title=T
world.cols=8
world.rows=6
world.tile=16
world.border=true
spawn.hero=2,2
trigger.coin=4,2
state.initial=play
`);
  const world = spec.createWorld();
  assert.ok(world);
  assert.ok(world!.isSolid(0, 0));
  assert.ok(world!.findActor("hero"));
  assert.ok(world!.findTrigger("coin"));
  assert.equal(spec.getInitialState(), "play");
});

test("body2d lands", () => {
  const plat = { x: 0, y: 100, width: 200, height: 20 };
  const body = new Body2D(10, 0, 16, 16);
  for (let i = 0; i < 50; i++) body.step([plat]);
  assert.ok(body.isOnGround);
  assert.ok(Math.abs(body.y - 84) < 0.01);
});

test("harness pixel", () => {
  const bg = toPixelInt(10, 20, 30);
  class G extends Game {
    override render(): void {
      this.context.fillRect(0, 0, this.width, this.height, bg);
    }
  }
  const h = new GameHarness(() => new G(32, 32));
  h.step(1);
  FrameAssert.assertPixel(h.renderer(), 0, 0, bg);
});

test("world tryMove blocked", () => {
  const world = World.grid(5, 5, 16);
  world.setTile(2, 1, World.SOLID);
  const a = new Actor(16, 16, 16, 16, 0xffffffff);
  world.add(a);
  assert.equal(a.tryMove(16, 0), false);
  assert.equal(a.x, 16);
});
