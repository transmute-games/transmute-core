import { docs } from "../website/src/lib/docs";
import { diagramId, renderDiagram } from "./markdown";
import { resolve } from "node:path";

const root = resolve(import.meta.dir, "..");
for (const file of [
  "logo.svg",
  "favicon.svg",
  "og-image.svg",
  "og-image.png",
]) {
  await Bun.write(
    `${root}/website/public/${file}`,
    Bun.file(`${root}/assets/${file}`),
  );
}
const seen = new Set<string>();
for (const doc of docs) {
  const markdown = await Bun.file(`${root}/docs/${doc.file}`).text();
  await Bun.write(`${root}/website/public/docs/${doc.file}`, markdown);
  for (const [, source] of markdown.matchAll(/```mermaid\s*\n([\s\S]*?)```/g)) {
    const id = diagramId(source);
    if (
      !seen.has(id) &&
      !(await Bun.file(`${root}/website/public/diagrams/${id}.svg`).exists())
    )
      await renderDiagram(source, `${root}/website/public/diagrams/${id}.svg`);
    seen.add(id);
  }
}
console.log(`Prepared ${docs.length} documents and ${seen.size} diagrams`);
