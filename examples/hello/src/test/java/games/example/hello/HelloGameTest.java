package games.example.hello;

import TransmuteCore.core.GameConfig;
import TransmuteCore.core.GameSpec;
import TransmuteCore.util.verify.FrameAssert;
import TransmuteCore.util.verify.GameHarness;
import org.junit.Test;

public class HelloGameTest
{
    @Test
    public void headlessFrameIsStable()
    {
        GameSpec spec = GameSpec.loadClasspath("gamespec.properties");
        GameConfig config = new GameConfig.Builder()
            .title(spec.getTitle())
            .version("1.0.0")
            .size(320, 180)
            .scale(1)
            .headless(true)
            .showStartScreen(false)
            .build();

        try (GameHarness harness = GameHarness.of(() -> new HelloGame(config, spec.getClearColor())))
        {
            harness.step(1);
            FrameAssert.assertPixel(harness.renderer(), 0, 0, spec.getClearColor());
            int hash = FrameAssert.hash(harness.renderer());
            harness.step(2);
            FrameAssert.assertHash(harness.renderer(), hash);
        }
    }
}
