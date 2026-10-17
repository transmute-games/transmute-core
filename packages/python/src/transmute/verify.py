from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
from typing import Callable

from .context import Context
from .input import SimulatedInput, resolve_key


class AudioProbe:
    _current: AudioProbe | None = None

    def __init__(self) -> None:
        self.events: list[tuple[str, str]] = []

    def __enter__(self) -> AudioProbe:
        AudioProbe._current = self
        return self

    def __exit__(self, *args) -> None:
        if AudioProbe._current is self:
            AudioProbe._current = None

    @classmethod
    def record_play(cls, name: str) -> None:
        if cls._current is not None:
            cls._current.events.append(("PLAY", name))

    def assert_played(self, name: str) -> None:
        if not any(k == "PLAY" and n == name for k, n in self.events):
            raise AssertionError(f"Expected play '{name}' but events={self.events}")


class FrameAssert:
    @staticmethod
    def assert_pixel(ctx: Context, x: int, y: int, expected: int) -> None:
        actual = ctx.get_pixel(x, y)
        if actual != (expected & 0xFFFFFFFF):
            raise AssertionError(
                f"Pixel ({x},{y}): expected 0x{expected:08X} but was 0x{actual:08X}"
            )

    @staticmethod
    def hash(ctx: Context) -> int:
        h = 0x811C9DC5
        for p in ctx.pixels:
            h ^= p & 0xFFFFFFFF
            h = (h * 0x01000193) & 0xFFFFFFFF
        return h


class Game:
    """Minimal game base for the Python port."""

    def __init__(self, width: int, height: int) -> None:
        self.width = width
        self.height = height
        self.context = Context(width, height)
        self.input = SimulatedInput()

    def init(self) -> None:
        pass

    def update(self) -> None:
        pass

    def render(self) -> None:
        pass


class GameHarness:
    def __init__(self, factory: Callable[[], Game]) -> None:
        self.game = factory()
        self._initialized = False

    def __enter__(self) -> GameHarness:
        return self

    def __exit__(self, *args) -> None:
        pass

    def step(self, frames: int = 1) -> GameHarness:
        if not self._initialized:
            self.game.init()
            self._initialized = True
        for _ in range(frames):
            self.game.update()
            self.game.render()
            self.game.input.end_frame()
        return self

    def renderer(self) -> Context:
        return self.game.context


@dataclass
class Step:
    frame: int
    action: str
    key: int = 0


class PlaytestScript:
    def __init__(self, steps: list[Step]) -> None:
        self.steps = sorted(steps, key=lambda s: s.frame)

    @classmethod
    def load(cls, path: str | Path) -> PlaytestScript:
        steps: list[Step] = []
        for line in Path(path).read_text(encoding="utf-8").splitlines():
            line = line.strip()
            if not line or line.startswith("#"):
                continue
            parts = line.split()
            frame = int(parts[0])
            action = parts[1].upper()
            key = 0
            if action not in ("IDLE", "CLEAR"):
                key = resolve_key(parts[2])
            steps.append(Step(frame, action, key))
        return cls(steps)

    def play(self, harness: GameHarness, input_handler: SimulatedInput | None = None) -> int:
        inp = input_handler or harness.game.input
        if not self.steps:
            return 0
        frame = 0
        index = 0
        last = self.steps[-1].frame
        while frame <= last:
            while index < len(self.steps) and self.steps[index].frame == frame:
                self._apply(inp, self.steps[index])
                index += 1
            harness.step(1)
            frame += 1
        return frame

    @staticmethod
    def _apply(inp: SimulatedInput, step: Step) -> None:
        if step.action == "HOLD":
            inp.hold_key(step.key)
        elif step.action == "PRESS":
            inp.press_key(step.key)
        elif step.action == "RELEASE":
            inp.release_key(step.key)
        elif step.action == "CLEAR":
            inp.clear()
