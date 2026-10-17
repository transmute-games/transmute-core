import Link from "next/link";
import { Brand } from "./Brand";
import { Search } from "./Search";
import { ThemeToggle, CopyMarkdown } from "./DocsTools";
import { docs, docUrl, repo, headingId } from "@/lib/docs";

export function DocsLayout({
  children,
  slug,
  markdown,
}: {
  children: React.ReactNode;
  slug: string;
  markdown: string;
}) {
  const headings = [...markdown.matchAll(/^## (.+)$/gm)].map(
    (match) => match[1],
  );
  const index = docs.findIndex((doc) => doc.slug === slug);
  const navigation = (
    <>
      {Array.from(new Set(docs.map((doc) => doc.group))).map((group) => (
        <div key={group}>
          <p className="sidebar-label">{group}</p>
          {docs
            .filter((doc) => doc.group === group)
            .map((doc) => (
              <Link
                href={docUrl(doc.slug)}
                key={doc.slug}
                aria-current={slug === doc.slug ? "page" : undefined}
              >
                {doc.title}
                {doc.group === "Engine API" && doc.slug !== "api" && (
                  <small>JAVA</small>
                )}
              </Link>
            ))}
        </div>
      ))}
    </>
  );
  return (
    <div className="docs-shell" data-theme="dark">
      <a className="skip" href="#main">
        Skip to content
      </a>
      <header className="docs-header">
        <Brand />
        <nav aria-label="Sections">
          <Link href="/docs" aria-current={!slug ? "page" : undefined}>
            Docs
          </Link>
          <Link
            href="/docs/api"
            aria-current={slug.startsWith("api") ? "page" : undefined}
          >
            API reference
          </Link>
          <Link
            href="/docs/examples"
            aria-current={slug === "examples" ? "page" : undefined}
          >
            Examples
          </Link>
        </nav>
        <div className="docs-controls">
          <Search />
          <ThemeToggle />
        </div>
      </header>
      <div className="docs-grid">
        <aside className="docs-sidebar" aria-label="Primary">
          {navigation}
        </aside>
        <div className="docs-center">
          <details className="docs-mobile-nav">
            <summary>Browse documentation</summary>
            <nav aria-label="Documentation sections">{navigation}</nav>
          </details>
          <main id="main" className="prose">
            {children}
          </main>
          <nav className="doc-pagination" aria-label="Pagination">
            {index > 0 && (
              <Link href={docUrl(docs[index - 1].slug)}>
                <small>Previous</small>
                {docs[index - 1].title}
              </Link>
            )}
            {index < docs.length - 1 && (
              <Link href={docUrl(docs[index + 1].slug)}>
                <small>Next</small>
                {docs[index + 1].title}
              </Link>
            )}
          </nav>
          <p className="doc-source">
            <a href={`${repo}/blob/master/docs/${docs[index].file}`}>
              Edit this page on GitHub
            </a>
          </p>
        </div>
        <aside className="docs-toc" aria-label="On this page">
          <strong>On this page</strong>
          {headings.map((heading) => (
            <a href={`#${headingId(heading)}`} key={heading}>
              {heading}
            </a>
          ))}
          <div className="doc-tools">
            <a href="#top">↑ &nbsp; Scroll to top</a>
            <CopyMarkdown markdown={markdown} />
            <a href={`/docs/${docs[index].file}`} download>
              Download Markdown
            </a>
          </div>
        </aside>
      </div>
    </div>
  );
}
