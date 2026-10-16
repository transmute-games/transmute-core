import { chromium } from "@playwright/test";
import { createHash } from "node:crypto";
import { mkdir, mkdtemp, rm } from "node:fs/promises";
import { tmpdir } from "node:os";
import { join } from "node:path";

export const diagramId = (source: string) =>
  createHash("sha256").update(source.trim()).digest("hex").slice(0, 16);
export async function renderDiagram(source: string, destination: string) {
  const temp = await mkdtemp(join(tmpdir(), "transmute-diagram-"));
  try {
    await mkdir(destination.slice(0, destination.lastIndexOf("/")), {
      recursive: true,
    });
    await Bun.write(`${temp}/diagram.mmd`, source);
    await Bun.write(
      `${temp}/puppeteer.json`,
      JSON.stringify({
        executablePath: chromium.executablePath(),
        args: ["--no-sandbox"],
      }),
    );
    const result = Bun.spawn(
      [
        "bunx",
        "--no-install",
        "mmdc",
        "-i",
        `${temp}/diagram.mmd`,
        "-o",
        destination,
        "-p",
        `${temp}/puppeteer.json`,
        "-b",
        "transparent",
      ],
      { stdout: "pipe", stderr: "pipe" },
    );
    const [code, error] = await Promise.all([
      result.exited,
      new Response(result.stderr).text(),
    ]);
    if (code) throw new Error(`Invalid Mermaid diagram:\n${source}\n${error}`);
  } finally {
    await rm(temp, { recursive: true, force: true });
  }
}
