"use client";
import { useRef, useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  BookOpen,
  Compass,
  Gamepad2,
  Github,
  Menu,
  X,
  Layers,
  FlaskConical,
  Code2,
} from "lucide-react";
import { Brand } from "./Brand";
import { Search } from "./Search";
import { repo } from "@/lib/docs";

export function MarketingNav() {
  const path = usePathname();
  const dialog = useRef<HTMLDialogElement>(null);
  const [open, setOpen] = useState(false);
  const close = () => {
    dialog.current?.close();
    setOpen(false);
  };
  return (
    <>
      <a className="skip" href="#main">
        Skip to content
      </a>
      <header className="site-header">
        <div className="wrap header-inner">
          <Brand />
          <nav className="desktop-nav" aria-label="Main navigation">
            {[
              ["/", "Explore the engine"],
              ["/docs", "Getting started"],
              ["/docs/architecture", "Our approach"],
            ].map(([href, label]) => (
              <Link
                key={href}
                href={href}
                aria-current={path === href ? "page" : undefined}
              >
                {label}
              </Link>
            ))}
          </nav>
          <Search />
          <a className="github-link" href={repo}>
            <Github size={18} /> GitHub
          </a>
          <button
            className="menu-toggle"
            aria-haspopup="dialog"
            aria-controls="mobile-menu"
            aria-expanded={open}
            onClick={() => {
              dialog.current?.showModal();
              setOpen(true);
            }}
          >
            Menu <Menu size={18} />
          </button>
        </div>
      </header>
      <dialog
        id="mobile-menu"
        className="mobile-dialog"
        ref={dialog}
        aria-labelledby="mobile-title"
        onClose={() => setOpen(false)}
      >
        <div className="mobile-panel-heading">
          <Brand />
          <button aria-label="Close navigation" onClick={close}>
            <X size={22} />
          </button>
        </div>
        <div className="mobile-panel-body">
          <p className="eyebrow">A LITTLE CODE GOES A LONG WAY</p>
          <h2 id="mobile-title">What’s next?</h2>
          <Search shortcut={false} />
          <nav
            className="mobile-primary"
            aria-label="Mobile navigation"
            onClick={(event) => {
              if ((event.target as HTMLElement).closest("a")) close();
            }}
          >
            <Link className="mobile-feature-card" href="/">
              <Gamepad2 />
              <span>
                <strong>Explore the engine</strong>
                <small>A small toolkit for your next world.</small>
              </span>
            </Link>
            <div className="mobile-resource-grid">
              <Link href="/docs">
                <BookOpen />
                <strong>Getting started</strong>
                <small>From install to your first frame.</small>
              </Link>
              <Link href="/docs/architecture">
                <Compass />
                <strong>Our approach</strong>
                <small>Small pieces, working together.</small>
              </Link>
            </div>
          </nav>
          <section className="mobile-tasks">
            <div className="mobile-section-label">
              <h3>Browse by task</h3>
              <span>Start with an idea</span>
            </div>
            <nav aria-label="Game development topics" onClick={close}>
              {[
                [Layers, "Build a world", "/docs/api/world"],
                [Code2, "Explore the API", "/docs/api"],
                [FlaskConical, "Test your game", "/docs/testing"],
              ].map(([Icon, title, href]) => {
                const Symbol = Icon as typeof Layers;
                return (
                  <Link href={href as string} key={href as string}>
                    <Symbol size={20} />
                    <span>{title as string}</span>
                  </Link>
                );
              })}
            </nav>
          </section>
          <aside className="mobile-starter">
            <p className="eyebrow">NOT SURE WHERE TO BEGIN?</p>
            <h3>A first pixel is a good start.</h3>
            <p>
              Meet the hello example: a little game with a verification path
              built in.
            </p>
            <Link href="/docs/examples" onClick={close}>
              Explore the examples
            </Link>
          </aside>
        </div>
        <div className="mobile-panel-footer">
          <a className="icon-link" href={repo}>
            <Github size={16} /> GitHub
          </a>
          <a href={`${repo}/issues`}>Feedback & issues</a>
        </div>
      </dialog>
    </>
  );
}
export function MarketingFooter() {
  const groups = [
    [
      "The engine",
      ["API reference", "/docs/api"],
      ["Worlds & physics", "/docs/api/world"],
      ["Headless testing", "/docs/testing"],
    ],
    [
      "Resources",
      ["Brand & assets", "/brand"],
      ["For agents", `${repo}/blob/master/AGENTS.md`],
      ["Getting started", "/docs"],
      ["Our approach", "/docs/architecture"],
      ["Contributing", "/docs/contributing"],
    ],
    [
      "In the open",
      ["Transmute Games", "https://github.com/transmute-games"],
      ["GitHub repository", repo],
      ["Feedback & issues", `${repo}/issues`],
      ["Releases", `${repo}/releases`],
      [
        "Credits & acknowledgments",
        `${repo}/blob/master/THIRD_PARTY_LICENSES.md`,
      ],
    ],
  ];
  return (
    <footer className="site-footer">
      <div className="wrap">
        <div className="footer-grid">
          <div className="footer-brand">
            <Brand />
            <p>
              A little code.
              <br />A whole new world.
            </p>
            <span>A focused toolkit for thoughtful game makers.</span>
          </div>
          {groups.map(([title, ...links]) => (
            <nav key={title as string} aria-label={`Footer ${title}`}>
              <h2>{title as string}</h2>
              {links.map((link) => (
                <Link key={link[1]} href={link[1]}>
                  {link[0]}
                </Link>
              ))}
            </nav>
          ))}
        </div>
        <div className="footer-bottom">
          <span>© 2026 Transmute Games</span>
          <a href={`${repo}/blob/master/LICENSE`}>PolyForm Shield license</a>
          <span className="footer-note">Built for the worlds you imagine.</span>
          <a href="#top">Back to top ↑</a>
        </div>
      </div>
    </footer>
  );
}
