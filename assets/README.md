# Shared Assets

This directory contains assets shared across multiple parts of the project (docs, website, README).

## Files

- `favicon.svg` - Transmute Core game controller logo (used as favicon and in navigation)
- `og-image.svg` - Open Graph image used for social media previews

## Usage

The assets in this directory are automatically copied to their respective destinations during build:

- **Website**: Copied to `website/public/` via `pnpm run copy-assets` (runs before `dev` and `build`)
- **Docs**: Can be referenced from this location or copied as needed
- **README**: Referenced via GitHub raw URL

## Modifying Assets

To update an asset:

1. Edit the file in this directory (`assets/`)
2. The changes will automatically propagate during the next build
3. For local development:
   - Website: Run `pnpm run dev` in `website/` (copies automatically)

## Why Not Symlinks?

We use build-time copying instead of symlinks to ensure compatibility with all deployment platforms, including those that don't preserve symlinks during build processes.

## Brand Colors

The Transmute Core brand uses the following colors from the Transmute Games logo:

- **Cyan**: #00d9ff - Primary accent
- **Lime**: #b4ff39 - Secondary accent
- **Blue**: #0066ff - Tertiary accent
- **Purple**: #6b46ff - Additional accent
- **Dark Navy**: #0a0e27 - Background
