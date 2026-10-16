import Link from "next/link";
import { MarketingNav, MarketingFooter } from "@/components/MarketingLayout";
export default function NotFound() {
  return (
    <>
      <MarketingNav />
      <main id="main" className="wrap brand-page">
        <p className="eyebrow">404 / UNCHARTED TERRITORY</p>
        <h1>This world isn’t here.</h1>
        <p>
          The page may have moved. Start again with the engine or find what you
          need in the docs.
        </p>
        <div className="actions">
          <Link className="button" href="/">
            Back to the engine
          </Link>
          <Link className="text-link" href="/docs">
            Read the docs
          </Link>
        </div>
      </main>
      <MarketingFooter />
    </>
  );
}
