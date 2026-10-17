from __future__ import annotations

from typing import Iterable


class SimulatedInput:
    def __init__(self) -> None:
        self._held: set[int] = set()
        self._pressed: set[int] = set()
        self._released: set[int] = set()

    def hold_key(self, *codes: int) -> None:
        for c in codes:
            if c not in self._held:
                self._pressed.add(c)
            self._held.add(c)

    def press_key(self, *codes: int) -> None:
        for c in codes:
            self._pressed.add(c)
            self._held.add(c)

    def release_key(self, *codes: int) -> None:
        for c in codes:
            if c in self._held:
                self._released.add(c)
            self._held.discard(c)

    def clear(self) -> None:
        self._held.clear()
        self._pressed.clear()
        self._released.clear()

    def is_key_held(self, *codes: int) -> bool:
        return any(c in self._held for c in codes)

    def is_key_pressed(self, *codes: int) -> bool:
        return any(c in self._pressed for c in codes)

    def end_frame(self) -> None:
        self._pressed.clear()
        self._released.clear()


# Common key codes (match Java AWT where practical)
VK_LEFT, VK_RIGHT, VK_UP, VK_DOWN = 37, 39, 38, 40
VK_SPACE, VK_ENTER, VK_ESCAPE = 32, 10, 27
VK_A, VK_D, VK_W, VK_S = ord("A"), ord("D"), ord("W"), ord("S")

KEY_NAMES = {
    "LEFT": VK_LEFT,
    "RIGHT": VK_RIGHT,
    "UP": VK_UP,
    "DOWN": VK_DOWN,
    "SPACE": VK_SPACE,
    "ENTER": VK_ENTER,
    "ESCAPE": VK_ESCAPE,
    "ESC": VK_ESCAPE,
    "A": VK_A,
    "D": VK_D,
    "W": VK_W,
    "S": VK_S,
}


def resolve_key(token: str) -> int:
    t = token.upper()
    if t.startswith("VK_"):
        t = t[3:]
    if t in KEY_NAMES:
        return KEY_NAMES[t]
    if len(t) == 1:
        return ord(t)
    raise ValueError(f"Unknown key: {token}")
