# Website development

The website is a Next.js static export. Bun manages the workspace and runs tooling; Gradle remains responsible for Java builds.

## Develop

```bash
flox activate
bun install --frozen-lockfile
bunx playwright install chromium
bun scripts/build-docs.ts
bun run --cwd website dev
```

Open `http://localhost:3004`. Alternatively, use `flox services start website` after preparing docs.

## Content and assets

Edit the Markdown under `docs/`. `website/src/lib/docs.ts` maps files to routes. The build copies downloadable Markdown and renders Mermaid diagrams to SVG; no Mermaid runtime is shipped to visitors. Relative Markdown links resolve to website routes where available and GitHub otherwise.

Brand assets live in `assets/` and `website/public/`. The build copies shared assets from `assets/` to the public directory. The social image is a 1200 × 630 PNG; SVG is retained for editing. Run `bun scripts/generate-brand.ts` to regenerate both social images from the controller mark and the shared pixel-wave background in `assets/og-background.svg`. The background is kept separate from typography and decorative tiles so layout changes preserve its exact texture. Fonts are self-hosted with their license notices. No analytics, forms, or third-party tracking are included.

## Verify and preview

```bash
bash scripts/check.sh web
bun run --cwd website start
```

The production export is `website/out/`. Browser tests use a separate static preview on port 3005 and check links, navigation, search, themes, mobile reflow, and accessibility. The live development server can remain on port 3004.

## Hosting

Deploy `website/out/` to a static host that resolves clean paths to `.html` files and serves `404.html` for missing routes. Set `SITE_URL` to the real deployment origin at build time to generate canonical metadata, sitemap, and indexable robots rules. Without it, the build stays `noindex` and omits absolute social metadata. No production domain is assumed.

The exported site uses root-relative URLs. Hosting beneath a subdirectory requires configuring Next.js `basePath` and adapting public asset URLs before deployment.

The connected Vercel project uses `website/` as its root. `website/vercel.json` overrides legacy package-manager settings, installs the locked Bun workspace and Chromium for build-time diagrams, and publishes the static export with clean URLs. Enable access to files outside the root directory so the build can read shared docs and scripts. Configure `SITE_URL` for the production environment only; previews remain unindexed.

The Minicraft showcase uses the wordmark linked by the [game’s README](https://github.com/transmute-games/minicraft), sourced from the [Minicraft+ website](https://minicraftplus.github.io/Minicraft_logo.png) and stored locally as `website/public/minicraft-logo.webp`. It identifies the featured game; it is not a Transmute Core brand asset.
