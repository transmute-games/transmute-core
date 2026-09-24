from __future__ import annotations


class Context:
    """Minimal ARGB pixel buffer for headless verify."""

    def __init__(self, width: int, height: int) -> None:
        self.width = width
        self.height = height
        self.pixels = [0] * (width * height)

    def fill_rect(self, x: int, y: int, w: int, h: int, argb: int) -> None:
        for py in range(max(0, y), min(self.height, y + h)):
            row = py * self.width
            for px in range(max(0, x), min(self.width, x + w)):
                self.pixels[row + px] = argb & 0xFFFFFFFF

    def get_pixel(self, x: int, y: int) -> int:
        if x < 0 or y < 0 or x >= self.width or y >= self.height:
            raise IndexError(f"Pixel ({x},{y}) out of bounds")
        return self.pixels[y * self.width + x]
