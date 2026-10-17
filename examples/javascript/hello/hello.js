import { readFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import { FrameAssert, Game, GameHarness, GameSpec, toPixelInt } from "transmute";

const __dirname = dirname(fileURLToPath(import.meta.url));
const spec = GameSpec.parse(readFileSync(join(__dirname, "gamespec.properties"), "utf8"));

class HelloGame extends Game {
  constructor(spec) {
    super(spec.width, spec.height);
    this.spec = spec;
  }
  render() {
    this.context.fillRect(0, 0, this.width, this.height, this.spec.clearColor);
    this.context.fillRect(20, 20, 40, 40, toPixelInt(220, 180, 60));
  }
}

const headless = process.argv.includes("--headless");
if (headless) {
  const h = new GameHarness(() => new HelloGame(spec));
  h.step(1);
  FrameAssert.assertPixel(h.renderer(), 0, 0, spec.clearColor);
  console.log(`hello headless ok hash=0x${FrameAssert.hash(h.renderer()).toString(16)}`);
} else {
  console.log("Use --headless in JS v1");
  process.exit(1);
}
