"use client";
import { useEffect, useId, useRef, useState } from "react";
import Link from "next/link";
import { BookOpen, Code2, Search as SearchIcon, X } from "lucide-react";
import { docs, docUrl } from "@/lib/docs";

const categories = ["Everything", "Guides", "API", "Tutorials"] as const;
type Category = (typeof categories)[number];
const categoryOf = (group: string): Category =>
  group === "Engine API"
    ? "API"
    : group === "Tutorials"
      ? "Tutorials"
      : "Guides";

export function Search({ shortcut = true }: { shortcut?: boolean }) {
  const id = useId();
  const dialog = useRef<HTMLDialogElement>(null);
  const input = useRef<HTMLInputElement>(null);
  const results = useRef<HTMLDivElement>(null);
  const [query, setQuery] = useState("");
  const [category, setCategory] = useState<Category>("Everything");
  const open = () => {
    setQuery("");
    setCategory("Everything");
    dialog.current?.showModal();
    input.current?.focus();
  };
  useEffect(() => {
    const onShortcut = (event: KeyboardEvent) => {
      if (shortcut && (event.metaKey || event.ctrlKey) && event.key === "k") {
        event.preventDefault();
        open();
      }
    };
    window.addEventListener("keydown", onShortcut);
    return () => window.removeEventListener("keydown", onShortcut);
  }, [shortcut]);
  const matches = docs.filter(
    (doc) =>
      (category === "Everything" || categoryOf(doc.group) === category) &&
      `${doc.title} ${doc.group} ${doc.description}`
        .toLowerCase()
        .includes(query.trim().toLowerCase()),
  );
  return (
    <>
      <button
        className="search-trigger"
        onClick={open}
        aria-label="Search documentation"
        aria-haspopup="dialog"
      >
        <SearchIcon size={16} />
        <span>Search</span>
        <kbd>⌘ K</kbd>
      </button>
      <dialog
        className="search-dialog"
        ref={dialog}
        aria-labelledby={`${id}-title`}
        onClick={(event) => {
          if (event.target === dialog.current) dialog.current.close();
        }}
        onKeyDown={(event) => {
          if (
            !["ArrowDown", "ArrowUp"].includes(event.key) ||
            event.altKey ||
            event.metaKey ||
            event.ctrlKey
          )
            return;
          const links = Array.from(
            results.current?.querySelectorAll("a") ?? [],
          );
          if (!links.length) return;
          event.preventDefault();
          const index = links.indexOf(
            document.activeElement as HTMLAnchorElement,
          );
          const next =
            index < 0
              ? event.key === "ArrowDown"
                ? 0
                : links.length - 1
              : index + (event.key === "ArrowDown" ? 1 : -1);
          if (next < 0 || next >= links.length) input.current?.focus();
          else links[next].focus();
        }}
      >
        <div className="search-content">
          <div className="search-heading">
            <div>
              <p className="search-eyebrow">The Transmute library</p>
              <h2 id={`${id}-title`}>Find your next step.</h2>
            </div>
            <button
              className="search-close"
              aria-label="Close search"
              onClick={() => dialog.current?.close()}
            >
              <X size={24} />
            </button>
          </div>
          <label className="search-field">
            <SearchIcon size={24} aria-hidden="true" />
            <span className="sr-only">Search documentation</span>
            <input
              ref={input}
              autoFocus
              placeholder="Search guides, APIs, and tutorials…"
              value={query}
              onChange={(event) => {
                setQuery(event.target.value);
                results.current?.scrollTo(0, 0);
              }}
              onKeyDown={(event) => {
                if (event.key === "Enter" && !event.nativeEvent.isComposing)
                  results.current?.querySelector("a")?.click();
              }}
            />
          </label>
          <div
            className="search-filters"
            role="group"
            aria-label="Filter documentation"
          >
            {categories.map((item) => (
              <button
                key={item}
                aria-pressed={category === item}
                onClick={() => {
                  setCategory(item);
                  results.current?.scrollTo(0, 0);
                }}
              >
                {item}
              </button>
            ))}
          </div>
          <div className="search-summary">
            <span>
              {query.trim() ? "Search results" : "Good places to start"}
            </span>
            <span role="status">{matches.length} results</span>
          </div>
          <div className="search-results" ref={results}>
            {matches.map((doc) => {
              const Icon = doc.group === "Engine API" ? Code2 : BookOpen;
              return (
                <Link
                  key={doc.slug}
                  href={docUrl(doc.slug)}
                  onClick={() => dialog.current?.close()}
                >
                  <span className="search-result-icon">
                    <Icon size={24} aria-hidden="true" />
                  </span>
                  <span className="search-result-copy">
                    <strong>{doc.title}</strong>
                    <span>{doc.description}</span>
                  </span>
                  <small>{categoryOf(doc.group)}</small>
                </Link>
              );
            })}
            {!matches.length && (
              <div className="search-empty">
                <BookOpen size={28} aria-hidden="true" />
                <h3>No matches yet.</h3>
                <p>Try a different term or explore all documentation.</p>
                <button
                  onClick={() => {
                    setQuery("");
                    setCategory("Everything");
                    input.current?.focus();
                  }}
                >
                  Clear search
                </button>
              </div>
            )}
          </div>
          <footer className="search-footer">
            <span>Your next game starts here.</span>
            <span>
              <kbd>↑</kbd> <kbd>↓</kbd> Move <kbd>↵</kbd> Open <kbd>esc</kbd>{" "}
              Close
            </span>
          </footer>
        </div>
      </dialog>
    </>
  );
}
