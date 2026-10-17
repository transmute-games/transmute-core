import sharp from "sharp";
import { resolve } from "node:path";

const root = resolve(import.meta.dir, "..");
const logo = await Bun.file(`${root}/assets/logo.svg`).text();
const mark = logo.replace(/<svg[^>]*>|<\/svg>/g, "");
const background = await Bun.file(`${root}/assets/og-background.svg`).text();
const world = (
  await Bun.file(`${root}/website/public/world.svg`).text()
).replace("<svg ", '<svg x="12" y="12" width="432" height="284" ');
const svg = background.replace(
  "</svg>",
  `
<g transform="translate(58 44) scale(.71875)">${mark}</g>
<g font-family="Arial, sans-serif">
  <text x="118" y="77" font-size="27" font-weight="bold" fill="#f7f7f2">transmute / core</text>
  <text x="1142" y="73" text-anchor="end" font-size="18" fill="#cad3c3">A little code. A lot to play.</text>
  <circle cx="62" cy="174" r="4" fill="#b7d99e"/>
  <text x="78" y="180" font-size="17" letter-spacing="2" fill="#cad3c3">JAVA. MADE FOR PLAY.</text>
  <text x="58" y="286" font-size="79" font-weight="bold" letter-spacing="-4" fill="#f7f7f2">Small engine.</text>
  <text x="58" y="380" font-family="Georgia, serif" font-style="italic" font-size="90" letter-spacing="-3" fill="#c5dfb0">Whole worlds.</text>
  <text x="62" y="445" font-size="23" fill="#cad3c3">Build 2D games in Java. Make every pixel count.</text>
  <text x="58" y="582" font-size="16" letter-spacing="1" fill="#e2eadb">JAVA 17+ / PIXEL RENDERING / HEADLESS TESTING</text>
</g>
<g transform="translate(700 215) rotate(-4 228 154)">
  <rect width="456" height="308" rx="22" fill="#1c271d" stroke="#5a7050"/>
  <clipPath id="scene"><rect x="12" y="12" width="432" height="284" rx="9"/></clipPath>
  <g clip-path="url(#scene)">${world}</g>
</g>
</svg>`,
);
await Bun.write(`${root}/assets/og-image.svg`, svg);
await sharp(Buffer.from(svg)).png().toFile(`${root}/assets/og-image.png`);
for (const name of ["og-image.svg", "og-image.png"])
  await Bun.write(
    `${root}/website/public/${name}`,
    Bun.file(`${root}/assets/${name}`),
  );
console.log("Generated editable SVG and 1200 × 630 PNG social previews");
