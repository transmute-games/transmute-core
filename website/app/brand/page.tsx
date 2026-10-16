import Image from "next/image";
import { MarketingNav, MarketingFooter } from "@/components/MarketingLayout";
export const metadata = {
  title: "Brand & assets",
  ...(process.env.SITE_URL ? { alternates: { canonical: "/brand" } } : {}),
  description: "Transmute Core logo and social preview assets.",
};
export default function BrandPage() {
  return (
    <>
      <MarketingNav />
      <main id="main" className="wrap brand-page">
        <p className="eyebrow">TRANSMUTE GAMES</p>
        <h1>Made for little worlds.</h1>
        <p>
          Use these assets when linking to Transmute Core. Keep the logo’s
          proportions and leave room around the mark.
        </p>
        <div className="brand-assets">
          <a href="/logo.svg" download>
            <Image
              src="/logo.svg"
              width={100}
              height={100}
              alt="Transmute Core controller logo"
            />
            <span>Download SVG logo</span>
          </a>
          <a href="/og-image.png" download>
            <Image
              src="/og-image.png"
              width={480}
              height={252}
              alt="Transmute Core social preview"
            />
            <span>Download social image · 1200 × 630</span>
          </a>
        </div>
      </main>
      <MarketingFooter />
    </>
  );
}
