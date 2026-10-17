import { Glob } from "bun";
import { existsSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { mkdtemp, rm } from "node:fs/promises";
import { tmpdir } from "node:os";
import { diagramId, renderDiagram } from "./markdown";

const files = [...new Glob("**/*.md").scanSync(".")].filter(
  (p) =>
    !p.startsWith("website/public/docs/") &&
    !/(^|\/)(node_modules|playwright-report|test-results|build|out|\.next[^/]*|\.git|\.flox|\.venv[^/]*|\.cache|\.website-studio)\//.test(
      p,
    ),
);
const errors: string[] = [];
const diagrams = new Map<string, string>();
for (const path of files) {
  const text = await Bun.file(path).text();
  for (const [, source] of text.matchAll(/```mermaid\s*\n([\s\S]*?)```/g))
    diagrams.set(diagramId(source), source);
  const prose = text.replace(/```[\s\S]*?```/g, "");
  for (const [, href] of prose.matchAll(
    /\[[^\]]*\]\(([^\s)]+)(?:\s+"[^"]*")?\)/g,
  )) {
    if (/^(https?:|mailto:|#)/.test(href)) continue;
    const target = decodeURIComponent(href.split("#")[0]);
    if (target && !existsSync(resolve(dirname(path), target)))
      errors.push(`${path}: broken link ${href}`);
  }
}
if (errors.length) throw new Error(errors.join("\n"));
const temp = await mkdtemp(`${tmpdir()}/transmute-docs-`);
try {
  for (const [id, source] of diagrams)
    await renderDiagram(source, `${temp}/${id}.svg`);
} finally {
  await rm(temp, { recursive: true, force: true });
}
console.log(
  `Validated links in ${files.length} Markdown files and rendered ${diagrams.size} Mermaid diagrams`,
);
