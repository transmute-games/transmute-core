import { Glob } from "bun";

const groups = [
  { glob: "website/{app,src}/**/*.{ts,tsx,css}", max: 2750 },
  { glob: "scripts/*.{ts,sh}", max: 700 },
];
for (const { glob, max } of groups) {
  let total = 0;
  for await (const path of new Glob(glob).scan(".")) {
    const lines = (await Bun.file(path).text()).trimEnd().split("\n").length;
    if (lines > 300)
      throw new Error(
        `${path}: ${lines} lines exceeds the 300-line file budget`,
      );
    total += lines;
  }
  if (total > max) throw new Error(`${glob}: ${total} lines exceeds ${max}`);
  console.log(`${glob}: ${total}/${max} lines`);
}
const readme = (await Bun.file("README.md").text())
  .trimEnd()
  .split("\n").length;
if (readme > 80) throw new Error(`README.md: ${readme}/80 lines`);
