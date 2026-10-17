import Image from "next/image";
import { MarketingNav, MarketingFooter } from "@/components/MarketingLayout";
import { Brand } from "@/components/Brand";
import { Palette } from "@/components/Palette";
export const metadata = {
  title: "Brand & assets",
  ...(process.env.SITE_URL ? { alternates: { canonical: "/brand" } } : {}),
  description:
    "Transmute Core logos, colors, typography, and social preview downloads.",
};
export default function BrandPage() {
  return (
    <>
      <MarketingNav />
      <main id="main" className="wrap brand-page">
        <header className="section">
          <p className="eyebrow">TRANSMUTE CORE / BRAND & ASSETS</p>
          <h1>
            Small engine.
            <br />
            <em>A world of character.</em>
          </h1>
          <p className="lead">
            The pieces that make Transmute Core feel like home. For your next
            README, game showcase, or introduction to the engine.
          </p>
          <a className="button" href="#downloads">
            Get the assets
          </a>
        </header>
        <section className="section">
          <p className="eyebrow">01 / THE MARK</p>
          <h2>
            A little controller. <em>Endless possibilities.</em>
          </h2>
          <p>
            Our controller mark is an invitation to make something playable.
            Keep its colors and proportions, with clear space on every side.
          </p>
          <div className="brand-assets">
            <figure>
              <Image
                src="/logo.svg"
                width={140}
                height={140}
                alt="Transmute Core controller mark"
              />
              <figcaption>Original mark</figcaption>
            </figure>
            <figure>
              <Brand />
              <figcaption>The engine signature</figcaption>
            </figure>
          </div>
        </section>
        <section className="section">
          <p className="eyebrow">02 / THE FIRST IMPRESSION</p>
          <h2>
            Whole worlds. <em>Worth sharing.</em>
          </h2>
          <p>
            A quiet glow and a field of pixels. Use the original social preview
            for project introductions and links to Transmute Core.
          </p>
          <Image
            className="brand-preview"
            src="/og-image.png"
            width={1200}
            height={630}
            alt="Transmute Core: Small engine. Whole worlds. Cream text over a dark green pixel-wave background."
          />
        </section>
        <section className="section">
          <p className="eyebrow">03 / COLOR</p>
          <h2>
            Grounded colors. <em>Room to play.</em>
          </h2>
          <p>
            Paper, charcoal, and sage keep the focus on what you make. Select a
            swatch to copy its hex value.
          </p>
          <Palette />
        </section>
        <section className="section">
          <p className="eyebrow">04 / TYPOGRAPHY</p>
          <h2>
            A clear voice. <em>A human touch.</em>
          </h2>
          <div className="feature-grid">
            {[
              [
                "DM Sans / Interface",
                "Small engine. Whole worlds.",
                "Headings, navigation, and everyday reading.",
                "inherit",
              ],
              [
                "Georgia / Emphasis",
                "Make it yours.",
                "A softer accent for a few well-chosen words.",
                "Georgia, serif",
              ],
              [
                "IBM Plex Mono / Code",
                "./gradlew run",
                "Commands and technical details, set clearly.",
                '"IBM Plex Mono", monospace',
              ],
            ].map(([name, sample, description, fontFamily]) => (
              <article className="feature" key={name}>
                <p className="eyebrow">{name}</p>
                <h3 style={{ fontFamily }}>{sample}</h3>
                <p>{description}</p>
              </article>
            ))}
          </div>
        </section>
        <section id="downloads" className="section">
          <p className="eyebrow">05 / THE TOOLKIT</p>
          <h2>
            Ready for your <em>next mention.</em>
          </h2>
          <p>
            Download the original files. Use them to identify Transmute Core and
            link back to the project.
          </p>
          <div className="feature-grid">
            {[
              ["Vector mark", "logo.svg", "SVG · original colors · scalable"],
              ["Social preview", "og-image.png", "PNG · 1200 × 630"],
              ["Browser icon", "favicon.svg", "SVG · scalable icon"],
            ].map(([name, file, detail]) => (
              <a className="feature" href={`/${file}`} download key={file}>
                <h3>{name}</h3>
                <p>{detail}</p>
                <span className="text-link">Download {name.toLowerCase()}</span>
              </a>
            ))}
          </div>
          <h3>A little care goes a long way.</h3>
          <p>
            Use “Transmute Core” in prose. Keep the mark intact, avoid
            stretching or recoloring it, and choose a simple background. These
            assets identify the engine; they don’t imply endorsement. The
            engine’s license still applies to its code.
          </p>
        </section>
      </main>
      <MarketingFooter />
    </>
  );
}
