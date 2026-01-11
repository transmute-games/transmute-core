import type { Metadata } from 'next';
import '@/styles/globals.css';

export const metadata: Metadata = {
  title: 'Transmute Core - High-Performance 2D Pixel Game Engine for Java',
  description: 'Build retro-style 2D games with Transmute Core, a high-performance Java game engine featuring custom pixel rendering, fixed timestep game loop, built-in animation system, and comprehensive documentation.',
  openGraph: {
    title: 'Transmute Core - High-Performance 2D Pixel Game Engine for Java',
    description: 'Build retro-style 2D games with Transmute Core, a high-performance Java game engine featuring custom pixel rendering, fixed timestep game loop, built-in animation system, and comprehensive documentation.',
    url: 'https://transmute-games.github.io/transmute-core',
    siteName: 'Transmute Core',
    images: [
      {
        url: '/og-image.svg',
        width: 1200,
        height: 630,
        alt: 'Transmute Core - High-Performance 2D Pixel Game Engine',
      },
    ],
    locale: 'en_US',
    type: 'website',
  },
  twitter: {
    card: 'summary_large_image',
    title: 'Transmute Core - High-Performance 2D Pixel Game Engine for Java',
    description: 'Build retro-style 2D games with Transmute Core, a high-performance Java game engine featuring custom pixel rendering, fixed timestep game loop, built-in animation system, and comprehensive documentation.',
    images: ['/og-image.svg'],
  },
  icons: {
    icon: [
      {
        url: '/favicon.svg',
        type: 'image/svg+xml',
      },
    ],
    apple: [
      {
        url: '/favicon.svg',
        type: 'image/svg+xml',
      },
    ],
  },
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className="scroll-smooth">
      <head>
        <meta charSet="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      </head>
      <body>{children}</body>
    </html>
  );
}
