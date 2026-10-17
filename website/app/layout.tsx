import type { Metadata } from "next";
import "@/styles/globals.css";

export const metadata: Metadata = {
  ...(process.env.SITE_URL
    ? {
        metadataBase: new URL(process.env.SITE_URL),
        alternates: { canonical: "/" },
      }
    : {}),
  robots: { index: !!process.env.SITE_URL, follow: !!process.env.SITE_URL },
  title: {
    default: "Transmute Core — Small engine. Whole worlds.",
    template: "%s · Transmute Core",
  },
  description:
    "A focused Java engine for 2D pixel games. Build worlds, draw every pixel, and verify your game without a display.",
  ...(process.env.SITE_URL
    ? {
        openGraph: {
          images: [
            {
              url: "/og-image.png",
              width: 1200,
              height: 630,
              alt: "Transmute Core — Small engine. Whole worlds.",
            },
          ],
        },
        twitter: { card: "summary_large_image", images: ["/og-image.png"] },
      }
    : {}),
  icons: { icon: "/favicon.svg" },
};
export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body id="top">{children}</body>
    </html>
  );
}
