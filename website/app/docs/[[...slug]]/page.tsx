import Image from "next/image";
import { readFile } from "node:fs/promises";
import path from "node:path";
import { createHash } from "node:crypto";
import { notFound } from "next/navigation";
import type { Metadata } from "next";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import { DocsLayout } from "@/components/DocsLayout";
import { docs, docUrl, headingId, repo } from "@/lib/docs";
import "@/styles/docs.css";

type Props = { params: Promise<{ slug?: string[] }> };
export const dynamicParams = false;
export function generateStaticParams() {
  return docs.map((doc) => ({ slug: doc.slug ? doc.slug.split("/") : [] }));
}
async function getDoc(params: Props["params"]) {
  const { slug } = await params;
  const doc = docs.find((entry) => entry.slug === (slug || []).join("/"));
  if (!doc) notFound();
  const markdown = await readFile(
    path.join(process.cwd(), "../docs", doc.file),
    "utf8",
  );
  return { doc, markdown };
}
export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const { doc, markdown } = await getDoc(params);
  return {
    title: doc.title,
    ...(process.env.SITE_URL ? { alternates: { canonical: docUrl(doc.slug) } } : {}),
    description: markdown.split("\n\n")[1]?.slice(0, 160),
  };
}
export default async function DocPage({ params }: Props) {
  const { doc, markdown } = await getDoc(params);
  const link = (href: string) => {
    if (/^(https?:|#|mailto:)/.test(href)) return href;
    const [file, fragment] = href.split("#");
    const resolved = path.posix.normalize(
      path.posix.join(path.posix.dirname(doc.file), file),
    );
    const target = docs.find((entry) => entry.file === resolved);
    if (target) return docUrl(target.slug) + (fragment ? `#${fragment}` : "");
    return `${repo}/blob/master/${path.posix.normalize(`docs/${resolved}`)}${fragment ? `#${fragment}` : ""}`;
  };
  return (
    <DocsLayout slug={doc.slug} markdown={markdown}>
      <ReactMarkdown
        remarkPlugins={[remarkGfm]}
        components={{
          h2: ({ children }) => (
            <h2 id={headingId(String(children))}>{children}</h2>
          ),
          a: ({ href, children }) => <a href={link(href || "")}>{children}</a>,
          pre: ({ children }) => <div className="code-block">{children}</div>,
          code: ({ className, children }) => {
            const source = String(children).trim();
            if (className === "language-mermaid") {
              const id = createHash("sha256")
                .update(source)
                .digest("hex")
                .slice(0, 16);
              return (
                <figure className="diagram">
                  <Image
                    width={800}
                    height={400}
                    src={`/diagrams/${id}.svg`}
                    alt={`Diagram: ${source.split("\n").slice(1).join("; ")}`}
                  />
                  <details>
                    <summary>Diagram source</summary>
                    <pre>{source}</pre>
                  </details>
                </figure>
              );
            }
            return className ? (
              <pre>
                <code className={className}>{children}</code>
              </pre>
            ) : (
              <code>{children}</code>
            );
          },
          ul: ({ children }) => (
            <ul className={doc.slug === "api" ? "api-list" : undefined}>
              {children}
            </ul>
          ),
        }}
      >
        {markdown}
      </ReactMarkdown>
    </DocsLayout>
  );
}
