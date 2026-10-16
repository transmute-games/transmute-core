import { test, expect } from "bun:test";
import { mkdtemp, rm } from "node:fs/promises";
import { tmpdir } from "node:os";
import { resolve } from "node:path";

const script = resolve("scripts/release-version.sh");
test("release tags tested head once and skips stale builds", async () => {
  const temp = await mkdtemp(`${tmpdir()}/transmute-release-test-`);
  const run = async (args: string[], cwd = temp, env = {}) => {
    const process = Bun.spawn(args, {
      cwd,
      env: { ...Bun.env, ...env },
      stdout: "pipe",
      stderr: "pipe",
    });
    const [code, output, error] = await Promise.all([
      process.exited,
      new Response(process.stdout).text(),
      new Response(process.stderr).text(),
    ]);
    if (code) throw new Error(`${args.join(" ")}: ${error}`);
    return output.trim();
  };
  try {
    await run(["git", "init", "--bare", "remote.git"]);
    await run(["git", "clone", "remote.git", "work"]);
    const work = `${temp}/work`;
    await run(["git", "checkout", "-b", "master"], work);
    await run(["git", "config", "commit.gpgsign", "false"], work);
    await run(["git", "config", "tag.gpgsign", "false"], work);
    await run(["git", "config", "core.hooksPath", "/dev/null"], work);
    await run(["git", "config", "user.name", "Release Test"], work);
    await run(["git", "config", "user.email", "test@example.invalid"], work);
    await run(["git", "commit", "--allow-empty", "-m", "test: initial"], work);
    await run(["git", "push", "origin", "master"], work);
    const first = await run(["git", "rev-parse", "HEAD"], work);
    const output = `${temp}/output`;
    const release = (sha: string) =>
      run(["bash", script], work, { RELEASE_SHA: sha, GITHUB_OUTPUT: output });
    await release(first);
    expect(await run(["git", "tag", "--list"], work)).toBe("cli-v1.0.0");
    await release(first);
    expect(await run(["git", "tag", "--list"], work)).toBe("cli-v1.0.0");
    await run(["git", "tag", "cli-v9.9.9-beta"], work);
    await run(["git", "commit", "--allow-empty", "-m", "fix: next"], work);
    await run(["git", "push", "origin", "master"], work);
    expect(await release(first)).toContain("Skipping superseded commit");
    const second = await run(["git", "rev-parse", "HEAD"], work);
    await release(second);
    expect(await run(["git", "rev-parse", "cli-v1.0.1"], work)).toBe(second);
    expect(await Bun.file(output).text()).toBe(
      "tag=cli-v1.0.0\ntag=cli-v1.0.0\ntag=cli-v1.0.1\n",
    );
  } finally {
    await rm(temp, { recursive: true, force: true });
  }
}, 30000);
