from __future__ import annotations

from pathlib import Path

from .world import Actor, Trigger, World, to_pixel_int


class GameSpec:
    def __init__(self, raw: dict[str, str]) -> None:
        self.raw = raw
        self.title = raw.get("title", "Game")
        self.version = raw.get("version", "1.0.0")
        self.width = int(raw.get("width", "320"))
        self.height = int(raw.get("height", "180"))
        self.scale = int(raw.get("scale", "3"))
        self.headless = raw.get("headless", "false").lower() == "true"
        r = int(raw.get("clear.r", "32"))
        g = int(raw.get("clear.g", "32"))
        b = int(raw.get("clear.b", "64"))
        a = int(raw.get("clear.a", "255"))
        self.clear_color = to_pixel_int(r, g, b, a)

    @classmethod
    def load(cls, path: str | Path) -> GameSpec:
        raw: dict[str, str] = {}
        text = Path(path).read_text(encoding="utf-8")
        for line in text.splitlines():
            line = line.strip()
            if not line or line.startswith("#") or "=" not in line:
                continue
            key, value = line.split("=", 1)
            raw[key.strip()] = value.strip()
        return cls(raw)

    def get_initial_state(self) -> str:
        return self.raw.get("state.initial", "play").strip()

    def create_world(self) -> World | None:
        if "world.cols" not in self.raw or "world.rows" not in self.raw:
            return None
        cols = int(self.raw["world.cols"])
        rows = int(self.raw["world.rows"])
        tile = int(self.raw.get("world.tile", "16"))
        world = World.grid(cols, rows, tile).with_clear_color(self.clear_color)
        if self.raw.get("world.border", "false").lower() == "true":
            world.fill_border(World.SOLID)
        solids = self.raw.get("world.solid", "").strip()
        if solids:
            for pair in solids.split(";"):
                pair = pair.strip()
                if not pair:
                    continue
                x_s, y_s = pair.split(",")
                world.set_tile(int(x_s.strip()), int(y_s.strip()), World.SOLID)
        self._apply_spawns(world, tile)
        self._apply_triggers(world, tile)
        return world

    def _base_names(self, prefix: str) -> list[str]:
        names: list[str] = []
        seen: set[str] = set()
        for key in self.raw:
            if not key.startswith(prefix):
                continue
            rest = key[len(prefix) :]
            name = rest.split(".", 1)[0]
            if name and name not in seen:
                seen.add(name)
                names.append(name)
        return names

    def _apply_spawns(self, world: World, tile: int) -> None:
        for name in self._base_names("spawn."):
            pos = self.raw.get(f"spawn.{name}")
            if not pos:
                continue
            tx, ty = (int(p.strip()) for p in pos.split(","))
            tw = int(self.raw.get(f"spawn.{name}.w", "1"))
            th = int(self.raw.get(f"spawn.{name}.h", "1"))
            color = self._parse_color(self.raw.get(f"spawn.{name}.color"), to_pixel_int(100, 150, 255))
            world.add(
                Actor(tx * tile, ty * tile, tw * tile, th * tile, color).named(name)
            )

    def _apply_triggers(self, world: World, tile: int) -> None:
        for name in self._base_names("trigger."):
            pos = self.raw.get(f"trigger.{name}")
            if not pos:
                continue
            tx, ty = (int(p.strip()) for p in pos.split(","))
            tw = int(self.raw.get(f"trigger.{name}.w", "1"))
            th = int(self.raw.get(f"trigger.{name}.h", "1"))
            audio = self.raw.get(f"trigger.{name}.audio")
            from .verify import AudioProbe

            def make_enter(cue: str | None):
                def enter(actor: Actor) -> None:
                    if cue:
                        AudioProbe.record_play(cue)

                return enter

            world.add_trigger(
                Trigger(
                    tx * tile,
                    ty * tile,
                    tw * tile,
                    th * tile,
                    on_enter=make_enter(audio),
                    id=name,
                )
            )

    @staticmethod
    def _parse_color(rgb: str | None, fallback: int) -> int:
        if not rgb:
            return fallback
        parts = [int(p.strip()) for p in rgb.split(",")]
        if len(parts) < 3:
            return fallback
        a = parts[3] if len(parts) >= 4 else 255
        return to_pixel_int(parts[0], parts[1], parts[2], a)
