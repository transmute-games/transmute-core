from transmute import Body2D, FrameAssert, Game, GameHarness, GameSpec, World
from transmute.world import to_pixel_int


def test_gamespec_world(tmp_path):
    p = tmp_path / "g.properties"
    p.write_text(
        "\n".join(
            [
                "title=T",
                "world.cols=8",
                "world.rows=6",
                "world.tile=16",
                "world.border=true",
                "spawn.hero=2,2",
                "trigger.coin=4,2",
                "trigger.coin.audio=pickup",
                "state.initial=play",
            ]
        )
    )
    spec = GameSpec.load(p)
    world = spec.create_world()
    assert world is not None
    assert world.is_solid(0, 0)
    assert world.find_actor("hero") is not None
    assert world.find_trigger("coin") is not None
    assert spec.get_initial_state() == "play"


def test_body2d_lands():
    class Plat:
        x, y, width, height = 0, 100, 200, 20

    body = Body2D(10, 0, 16, 16)
    for _ in range(50):
        body.step([Plat()])
    assert body.is_on_ground
    assert abs(body.y - (100 - 16)) < 0.01


def test_harness_pixel():
    bg = to_pixel_int(10, 20, 30)

    class G(Game):
        def render(self):
            self.context.fill_rect(0, 0, self.width, self.height, bg)

    with GameHarness(lambda: G(32, 32)) as h:
        h.step(1)
        FrameAssert.assert_pixel(h.renderer(), 0, 0, bg)


def test_try_move_blocked():
    world = World.grid(5, 5, 16)
    world.set_tile(2, 1, World.SOLID)
    from transmute.world import Actor

    a = Actor(16, 16, 16, 16, 0xFFFFFFFF)
    world.add(a)
    assert not a.try_move(16, 0)
    assert a.x == 16
