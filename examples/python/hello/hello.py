"""Minimal Python hello example."""

from __future__ import annotations

import sys
from pathlib import Path

from transmute import FrameAssert, Game, GameHarness, GameSpec
from transmute.world import to_pixel_int


class HelloGame(Game):
    def __init__(self, spec: GameSpec) -> None:
        super().__init__(spec.width, spec.height)
        self.spec = spec
        self.clear = spec.clear_color

    def render(self) -> None:
        self.context.fill_rect(0, 0, self.width, self.height, self.clear)
        self.context.fill_rect(20, 20, 40, 40, to_pixel_int(220, 180, 60))


def main(argv: list[str] | None = None) -> None:
    argv = argv if argv is not None else sys.argv[1:]
    root = Path(__file__).resolve().parent
    spec = GameSpec.load(root / "gamespec.properties")
    headless = "--headless" in argv

    if headless:
        with GameHarness(lambda: HelloGame(spec)) as h:
            h.step(1)
            FrameAssert.assert_pixel(h.renderer(), 0, 0, spec.clear_color)
            print(f"hello headless ok hash=0x{FrameAssert.hash(h.renderer()):x}")
        return

    print("Windowed mode not implemented in Python v1; use --headless")
    with GameHarness(lambda: HelloGame(spec)) as h:
        h.step(1)
        print("ok")


if __name__ == "__main__":
    main()
