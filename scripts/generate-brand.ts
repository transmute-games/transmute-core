import sharp from "sharp";
import { resolve } from "node:path";

const root = resolve(import.meta.dir, "..");
const logo = await Bun.file(`${root}/assets/logo.svg`).text();
const mark = logo.replace(/<svg[^>]*>|<\/svg>/g, "");
const background = await Bun.file(`${root}/assets/og-background.svg`).text();
const tile = (x: number, y: number, angle: number, content: string) =>
  `<g transform="translate(${x} ${y}) rotate(${angle} 48 48)"><rect width="96" height="96" rx="24" fill="#1c271d" stroke="#5a7050"/>${content}</g>`;
const svg = background.replace(
  "</svg>",
  `
<g transform="translate(58 44) scale(.71875)">${mark}</g>
<g font-family="Arial, sans-serif">
  <text x="118" y="77" font-size="27" font-weight="bold" fill="#f7f7f2">transmute / core</text>
  <text x="1142" y="73" text-anchor="end" font-size="18" fill="#cad3c3">A little code. A lot to play.</text>
  <circle cx="457" cy="174" r="4" fill="#b7d99e"/>
  <text x="473" y="180" font-size="17" letter-spacing="2" fill="#cad3c3">JAVA. MADE FOR PLAY.</text>
  <text x="600" y="286" text-anchor="middle" font-size="79" font-weight="bold" letter-spacing="-4" fill="#f7f7f2">Small engine.</text>
  <text x="600" y="380" text-anchor="middle" font-family="Georgia, serif" font-style="italic" font-size="90" letter-spacing="-3" fill="#c5dfb0">Whole worlds.</text>
  <text x="600" y="445" text-anchor="middle" font-size="23" fill="#cad3c3">Build 2D games in Java. Make every pixel count.</text>
  <text x="58" y="582" font-size="16" letter-spacing="1" fill="#e2eadb">JAVA 17+ / PIXEL RENDERING / HEADLESS TESTING</text>
</g>
${tile(95, 180, -12, '<g fill="#b7d99e"><path d="M25 25h20v20H25zm26 0h20v20H51zM25 51h20v20H25z"/><path d="M51 51h20v20H51z" opacity=".4"/></g>')}
${tile(1005, 185, 12, '<g fill="none" stroke="#b7d99e" stroke-width="3" stroke-linejoin="round"><rect x="21" y="25" width="54" height="46" rx="4"/><path d="M21 36h54m-40 23 9-9 9 9 11-15"/></g>')}
${tile(150, 390, 10, '<path d="m27 32 15 15-15 15m23 0h19" fill="none" stroke="#c5dfb0" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>')}
${tile(958, 395, -10, `<g transform="translate(8 8) scale(1.25)">${mark}</g>`)}
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
