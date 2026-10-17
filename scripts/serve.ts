import { resolve, extname } from "node:path";
const root = resolve(import.meta.dir, "../website/out");
const port = Number(process.env.PORT || 3005);
Bun.serve({
  port,
  async fetch(request) {
    const pathname = decodeURIComponent(new URL(request.url).pathname);
    const target = resolve(root, `.${pathname}`);
    if (target !== root && !target.startsWith(`${root}/`))
      return new Response("Forbidden", { status: 403 });
    const candidates =
      pathname === "/"
        ? [`${root}/index.html`]
        : extname(pathname)
          ? [target]
          : [`${target}.html`, `${target}/index.html`];
    for (const candidate of candidates) {
      const file = Bun.file(candidate);
      if (await file.exists())
        return new Response(file, { headers: { "Content-Type": file.type } });
    }
    return new Response(Bun.file(`${root}/404.html`), {
      status: 404,
      headers: { "Content-Type": "text/html" },
    });
  },
});
console.log(`Production preview: http://localhost:${port}`);
