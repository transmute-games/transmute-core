# Transmute Core Marketing Website

Official marketing website for Transmute Core, a high-performance 2D pixel game engine for Java.

## 🚀 Features

- **Next.js 15** with App Router
- **React 18** and TypeScript
- **Tailwind CSS** for styling
- **Mobile-first responsive design**
- **Custom SVG favicon and og-image** with Transmute Games brand colors
- **Syntax-highlighted code examples** using react-syntax-highlighter
- **Smooth animations and transitions**
- **SEO optimized** with comprehensive metadata

## 🎨 Design

The website uses the Transmute Games brand colors:
- **Cyan** (#00d9ff) - Primary accent
- **Lime** (#b4ff39) - Secondary accent
- **Blue** (#0066ff) - Tertiary accent
- **Purple** (#6b46ff) - Additional accent
- **Dark Navy** (#0a0e27) - Background

## 📦 Development

### Prerequisites

- Node.js 18+ or Bun
- pnpm (recommended) or npm

### Install Dependencies

```bash
pnpm install
```

### Run Development Server

```bash
pnpm run dev
```

The site will be available at [http://localhost:3004](http://localhost:3004).

### Build for Production

```bash
pnpm run build
```

This creates a static export in the `out/` directory, ready for deployment.

### Start Production Server

```bash
pnpm run start
```

## 📁 Project Structure

```
website/
├── app/                    # Next.js App Router pages
│   ├── layout.tsx         # Root layout with metadata
│   └── page.tsx           # Home page
├── src/
│   ├── components/        # React components
│   │   ├── sections/      # Page sections
│   │   ├── CodeBlock.tsx  # Syntax highlighting component
│   │   └── MarketingLayout.tsx # Nav and footer
│   └── styles/
│       └── globals.css    # Global styles and Tailwind
├── public/                # Static assets (auto-copied from ../assets/)
│   ├── favicon.svg       # Game controller favicon (copied from ../assets/)
│   └── og-image.svg      # Social media preview image (copied from ../assets/)
├── package.json
├── tailwind.config.js    # Tailwind configuration with brand colors
├── tsconfig.json         # TypeScript configuration
└── next.config.js        # Next.js configuration
```

## 🎯 Sections

- **Hero** - Eye-catching intro with CTAs
- **Stats** - Key metrics and performance indicators
- **Features** - Core engine capabilities
- **Code Examples** - Interactive code demonstrations
- **Quick Start** - 3-step getting started guide
- **CTA** - Final call-to-action with links

## 🎨 Shared Assets

The `favicon.svg` and `og-image.svg` files are stored in `../assets/` (project root) and automatically copied to `public/` during build. This ensures:

- **Single source of truth** for brand assets
- **Reusability** across the project (docs, website, README)
- **Consistency** in branding

To modify these assets, edit the files in `../assets/` - changes will propagate automatically on the next build.

## 🌐 Deployment

The website is configured for static export and can be deployed to:

- **GitHub Pages** (recommended)
- Vercel
- Netlify
- Any static hosting service

### Deploy to GitHub Pages

```bash
pnpm run build
# The out/ directory contains the static site
```

## 📝 License

MIT License - see the [LICENSE](../.github/LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---

Made with ❤️ by the Transmute Games team
