# Shared brand assets

The controller mark and social image belong to Transmute Core.

- `logo.svg` and `favicon.svg`: scalable controller mark.
- `og-image.svg`: editable social composition.
- `og-image.png`: 1200 × 630 social preview for platforms that require raster images.

The website build copies these files to `website/public/`. The README references this directory. After editing the social SVG, regenerate the PNG from the repository root:

```bash
bun -e 'import sharp from "sharp"; await sharp("assets/og-image.svg").png().toFile("assets/og-image.png")'
```

The palette is paper `#f7f7f2`, ink `#292b25`, sage `#eceee5`, and green `#526447`. Keep the logo proportions intact. Font and icon notices are in [third-party licenses](../THIRD_PARTY_LICENSES.md).
