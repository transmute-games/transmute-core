import sharp from "sharp";
import { resolve } from "node:path";

const root = resolve(import.meta.dir, "..");
const logo = await Bun.file(`${root}/assets/logo.svg`).text();
const mark = logo.replace(/<svg[^>]*>|<\/svg>/g, "");
const pixels: string[] = [];
for (let y = 330; y < 650; y += 6) {
  for (let x = -6; x < 1210; x += 6) {
    const wave = Math.sin(x / 95 + y / 110) * 12;
    const size = 1.5 + (y - 330) / 250;
    pixels.push(
      `M${x.toFixed(1)} ${(y + wave).toFixed(1)}h${size.toFixed(1)}v${size.toFixed(1)}h-${size.toFixed(1)}z`,
    );
  }
}
const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="1200" height="630" viewBox="0 0 1200 630">
<defs>
  <linearGradient id="glow" x2="0" y2="1"><stop offset="0" stop-color="#0b120d"/><stop offset=".5" stop-color="#0b120d"/><stop offset="1" stop-color="#708953"/></linearGradient>
  <linearGradient id="fade" x2="0" y2="1"><stop offset=".48" stop-color="white" stop-opacity="0"/><stop offset="1" stop-color="white"/></linearGradient>
  <mask id="texture"><rect width="1200" height="630" fill="url(#fade)"/></mask>
</defs>
<rect width="1200" height="630" fill="url(#glow)"/>
<path d="${pixels.join("")}" fill="#0b120d" opacity=".48" mask="url(#texture)"/>
<g transform="translate(55 44) scale(.5625)">${mark}</g>
<g font-family="Arial, sans-serif" fill="#f1f4e9">
  <text x="104" y="71" font-size="25" font-weight="bold">transmute / core</text>
  <g text-anchor="middle" letter-spacing="-3" font-size="76">
    <text x="600" y="260">Small engine.</text>
    <text x="600" y="345">Whole worlds.</text>
  </g>
  <g text-anchor="middle" font-size="24" fill="#c8d0bf">
    <text x="600" y="414">A focused Java engine for 2D pixel games.</text>
    <text x="600" y="452">Build your world. Make every pixel count.</text>
  </g>
</g>
</svg>`;
await Bun.write(`${root}/assets/og-image.svg`, svg);
await sharp(Buffer.from(svg)).png().toFile(`${root}/assets/og-image.png`);
for (const name of ["og-image.svg", "og-image.png"])
  await Bun.write(
    `${root}/website/public/${name}`,
    Bun.file(`${root}/assets/${name}`),
  );
console.log("Generated editable SVG and 1200 × 630 PNG social previews");
