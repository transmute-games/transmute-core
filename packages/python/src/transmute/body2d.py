from __future__ import annotations

from dataclasses import dataclass
from typing import Protocol


class Solid(Protocol):
    @property
    def x(self) -> float: ...

    @property
    def y(self) -> float: ...

    @property
    def width(self) -> float: ...

    @property
    def height(self) -> float: ...


@dataclass
class Body2D:
    x: float
    y: float
    width: float
    height: float
    velocity_x: float = 0.0
    velocity_y: float = 0.0
    on_ground: bool = False
    gravity: float = 0.5
    jump_strength: float = -10.0

    def set_velocity_x(self, vx: float) -> None:
        self.velocity_x = vx

    def jump(self) -> None:
        if self.on_ground:
            self.velocity_y = self.jump_strength
            self.on_ground = False

    def step(self, solids: list) -> None:
        self.velocity_y += self.gravity
        self.x += self.velocity_x
        self._resolve_x(solids)
        self.y += self.velocity_y
        self.on_ground = False
        self._resolve_y(solids)

    def _resolve_x(self, solids: list) -> None:
        for s in solids:
            if self._aabb(s):
                if self.velocity_x > 0:
                    self.x = s.x - self.width
                elif self.velocity_x < 0:
                    self.x = s.x + s.width
                self.velocity_x = 0

    def _resolve_y(self, solids: list) -> None:
        for s in solids:
            if self._aabb(s):
                if self.velocity_y > 0:
                    self.y = s.y - self.height
                    self.velocity_y = 0
                    self.on_ground = True
                elif self.velocity_y < 0:
                    self.y = s.y + s.height
                    self.velocity_y = 0

    def _aabb(self, s) -> bool:
        return (
            self.x < s.x + s.width
            and self.x + self.width > s.x
            and self.y < s.y + s.height
            and self.y + self.height > s.y
        )

    @property
    def is_on_ground(self) -> bool:
        return self.on_ground
