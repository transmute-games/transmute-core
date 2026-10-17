import Link from "next/link";
import Image from "next/image";
import { MarketingNav, MarketingFooter } from "@/components/MarketingLayout";

export default function Home() {
  return (
    <>
      <MarketingNav />
      <main id="main" className="wrap">
        <section className="hero">
          <div>
            <p className="eyebrow">THE JAVA PIXEL GAME ENGINE</p>
            <h1>
              Small engine.
              <br />
              <em>Whole worlds.</em>
            </h1>
            <p className="lead">
              From your first pixel to a world worth exploring. Build 2D games
              in Java, with every frame in your hands.
            </p>
            <div className="actions">
              <Link className="button" href="/docs">
                Build your first game
              </Link>
              <Link className="text-link" href="/docs/api">
                Explore the API
              </Link>
            </div>
            <p className="hero-note">
              Java 17+ <span>·</span> No runtime dependencies <span>·</span>{" "}
              Headless by design
            </p>
          </div>
          <div className="world-card">
            <div className="world-heading">
              <span>YOUR NEXT WORLD STARTS HERE</span>
              <span className="live-dot">320 × 180</span>
            </div>
            <Image
              priority
              src="/world.svg"
              alt="An original pixel landscape with a small adventurer, platforms, trees, and collectible coins"
              width="640"
              height="420"
            />
            <div className="world-caption">
              <span>Imagine. Build. Play.</span>
              <code>init → update → render</code>
            </div>
          </div>
        </section>
        <div className="principles">
          <span>
            A little code.
            <br />
            <strong>A lot to play with.</strong>
          </span>
          <span>◇ &nbsp; Pixels with purpose</span>
          <span>⌘ &nbsp; Java you already know</span>
          <span>✓ &nbsp; Tests that play your game</span>
        </div>
        <section id="features" className="section">
          <p className="eyebrow">BUILT FOR THE MAKING</p>
          <div className="section-heading">
            <h2>
              Less engine.
              <br />
              <em>More game.</em>
            </h2>
            <p>
              A focused set of tools for the things every little world needs.
              Bring your ideas. Keep your code.
            </p>
          </div>
          <div className="feature-grid">
            {[
              [
                "01",
                "Every pixel is yours.",
                "Draw sprites, animate characters, and work directly with the pixel buffer. A fixed timestep keeps your game logic consistent.",
                "/docs/api/game",
              ],
              [
                "02",
                "Give your world some rules.",
                "Tile maps, gravity, collisions, and triggers. Build on World and Body2D, with working examples to guide you.",
                "/docs/api/world",
              ],
              [
                "03",
                "Play it before you ship it.",
                "Press keys, advance frames, and assert pixels and audio. Run your game in CI without ever opening a window.",
                "/docs/testing",
              ],
            ].map(([number, title, description, href]) => (
              <Link className="feature" key={number} href={href}>
                <span className="eyebrow">{number} / CORE CAPABILITY</span>
                <h3>{title}</h3>
                <p>{description}</p>
                <span className="text-link">Read the guide</span>
              </Link>
            ))}
          </div>
        </section>
        <section id="quick-start" className="start-section">
          <div>
            <p className="eyebrow">A FIRST FRAME, NOT A BLANK PAGE</p>
            <h2>
              Start small.
              <br />
              <em>Make it yours.</em>
            </h2>
            <p>
              Choose a verified example. Add your sprites, write your rules, and
              let the next idea take over.
            </p>
            <Link className="button" href="/docs/examples">
              Explore the examples
            </Link>
          </div>
          <div className="terminal">
            <div className="terminal-heading">
              <span>YOUR TERMINAL</span>
              <span>Java 17+</span>
            </div>
            <pre>
              <code>
                <span className="code-comment">
                  # Create a world of your own
                </span>
                {
                  "\ntransmute new my-game -t platformer -y\ncd my-game\n./gradlew run\n\n"
                }
                <span className="code-comment">
                  # Or verify a checked-in example
                </span>
                {"\ncd examples/platformer\n./gradlew test verifyHeadless"}
              </code>
            </pre>
            <Link href="/docs">Installation & setup</Link>
          </div>
        </section>
        <section className="closing showcase" aria-labelledby="minicraft-title">
          <p className="eyebrow">BUILT WITH TRANSMUTE CORE</p>
          <Image
            src="/minicraft-logo.webp"
            alt="Minicraft"
            width={388}
            height={60}
            className="minicraft-logo"
          />
          <h2 id="minicraft-title">
            A whole world. <em>Meet Minicraft.</em>
          </h2>
          <p>
            A Minicraft+ adaptation powered by Transmute Core, with singleplayer
            survival and LAN multiplayer. See the engine become a playable game.
          </p>
          <div className="actions">
            <a
              className="button"
              href="https://github.com/transmute-games/minicraft"
            >
              Explore Minicraft on GitHub
            </a>
            <Link className="text-link" href="/docs/examples">
              Build your own game
            </Link>
          </div>
        </section>
      </main>
      <MarketingFooter />
    </>
  );
}
