from __future__ import annotations

from dataclasses import dataclass, field
from pathlib import Path
from typing import Callable


def to_pixel_int(r: int, g: int, b: int, a: int = 255) -> int:
    return ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255)


@dataclass
class Actor:
    x: int
    y: int
    width: int
    height: int
    color: int
    name: str | None = None
    world: World | None = None

    def named(self, name: str) -> Actor:
        self.name = name
        return self

    def try_move(self, dx: int, dy: int) -> bool:
        if self.world is None:
            self.x += dx
            self.y += dy
            return True
        nx, ny = self.x + dx, self.y + dy
        if self.world.blocks(nx, ny, self.width, self.height):
            return False
        self.x, self.y = nx, ny
        return True

    def update(self, input_handler=None) -> None:
        pass


@dataclass
class Trigger:
    x: int
    y: int
    width: int
    height: int
    on_enter: Callable[[Actor], None] = field(default_factory=lambda: (lambda a: None))
    id: str | None = None
    _inside: list[Actor] = field(default_factory=list)

    def set_on_enter(self, fn: Callable[[Actor], None]) -> None:
        prev = self.on_enter

        def chained(actor: Actor) -> None:
            prev(actor)
            fn(actor)

        self.on_enter = chained

    def evaluate(self, actors: list[Actor]) -> None:
        now: list[Actor] = []
        for actor in actors:
            if self._overlaps(actor):
                now.append(actor)
                if actor not in self._inside:
                    self.on_enter(actor)
        self._inside = now

    def _overlaps(self, actor: Actor) -> bool:
        return (
            self.x < actor.x + actor.width
            and self.x + self.width > actor.x
            and self.y < actor.y + actor.height
            and self.y + self.height > actor.y
        )


class World:
    EMPTY = 0
    SOLID = 1

    def __init__(self, cols: int, rows: int, tile_size: int = 16) -> None:
        if cols <= 0 or rows <= 0 or tile_size <= 0:
            raise ValueError("cols, rows, tile_size must be positive")
        self.cols = cols
        self.rows = rows
        self.tile_size = tile_size
        self.tiles = [self.EMPTY] * (cols * rows)
        self.actors: list[Actor] = []
        self.triggers: list[Trigger] = []
        self.clear_color = to_pixel_int(20, 20, 30)
        self.solid_color = to_pixel_int(60, 60, 80)

    @classmethod
    def grid(cls, cols: int, rows: int, tile_size: int = 16) -> World:
        return cls(cols, rows, tile_size)

    def with_clear_color(self, argb: int) -> World:
        self.clear_color = argb
        return self

    def with_solid_color(self, argb: int) -> World:
        self.solid_color = argb
        return self

    def fill_border(self, tile: int = SOLID) -> None:
        for x in range(self.cols):
            self.set_tile(x, 0, tile)
            self.set_tile(x, self.rows - 1, tile)
        for y in range(self.rows):
            self.set_tile(0, y, tile)
            self.set_tile(self.cols - 1, y, tile)

    def set_tile(self, tx: int, ty: int, tile: int) -> None:
        if not self._in_bounds(tx, ty):
            raise ValueError(f"Tile out of bounds: ({tx},{ty})")
        self.tiles[tx + ty * self.cols] = tile

    def get_tile(self, tx: int, ty: int) -> int:
        if not self._in_bounds(tx, ty):
            return self.SOLID
        return self.tiles[tx + ty * self.cols]

    def is_solid(self, tx: int, ty: int) -> bool:
        return self.get_tile(tx, ty) == self.SOLID

    def blocks(self, x: int, y: int, w: int, h: int) -> bool:
        min_tx = x // self.tile_size
        max_tx = (x + w - 1) // self.tile_size
        min_ty = y // self.tile_size
        max_ty = (y + h - 1) // self.tile_size
        for tx in range(min_tx, max_tx + 1):
            for ty in range(min_ty, max_ty + 1):
                if self.is_solid(tx, ty):
                    return True
        return False

    def add(self, actor: Actor) -> None:
        self.actors.append(actor)
        actor.world = self

    def remove_actor(self, name: str) -> Actor | None:
        found = self.find_actor(name)
        if found:
            self.actors.remove(found)
            found.world = None
        return found

    def add_trigger(self, trigger: Trigger) -> None:
        self.triggers.append(trigger)

    def find_actor(self, name: str) -> Actor | None:
        for a in self.actors:
            if a.name == name:
                return a
        return None

    def find_trigger(self, id_: str) -> Trigger | None:
        for t in self.triggers:
            if t.id == id_:
                return t
        return None

    def update(self, input_handler=None) -> None:
        for actor in self.actors:
            actor.update(input_handler)
        for trigger in self.triggers:
            trigger.evaluate(self.actors)

    def render(self, ctx) -> None:
        ctx.fill_rect(0, 0, ctx.width, ctx.height, self.clear_color)
        for ty in range(self.rows):
            for tx in range(self.cols):
                if self.is_solid(tx, ty):
                    ctx.fill_rect(
                        tx * self.tile_size,
                        ty * self.tile_size,
                        self.tile_size,
                        self.tile_size,
                        self.solid_color,
                    )
        for actor in self.actors:
            ctx.fill_rect(actor.x, actor.y, actor.width, actor.height, actor.color)

    def pixel_width(self) -> int:
        return self.cols * self.tile_size

    def pixel_height(self) -> int:
        return self.rows * self.tile_size

    def _in_bounds(self, tx: int, ty: int) -> bool:
        return 0 <= tx < self.cols and 0 <= ty < self.rows
