"use client";
import { useEffect, useId, useRef, useState } from "react";
import Link from "next/link";
import { Search as SearchIcon, X } from "lucide-react";
import { docs, docUrl } from "@/lib/docs";

export function Search({ shortcut = true }: { shortcut?: boolean }) {
  const id = useId();
  const dialog = useRef<HTMLDialogElement>(null);
  const [query, setQuery] = useState("");
  const open = () => {
    setQuery("");
    dialog.current?.showModal();
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
  const matches = docs.filter((doc) =>
    `${doc.title} ${doc.group} ${doc.slug}`
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
      >
        <div className="search-heading">
          <h2 id={`${id}-title`}>Find your next step.</h2>
          <button
            aria-label="Close search"
            onClick={() => dialog.current?.close()}
          >
            <X size={20} />
          </button>
        </div>
        <label className="sr-only" htmlFor={`${id}-input`}>
          Search documentation
        </label>
        <input
          id={`${id}-input`}
          autoFocus
          placeholder="Search guides, APIs, and tutorials…"
          value={query}
          onChange={(event) => setQuery(event.target.value)}
        />
        <p role="status">{matches.length} results</p>
        <div className="search-results">
          {matches.map((doc) => (
            <Link
              key={doc.slug}
              href={docUrl(doc.slug)}
              onClick={() => dialog.current?.close()}
            >
              <span>{doc.title}</span>
              <small>{doc.group}</small>
            </Link>
          ))}
          {!matches.length && (
            <p>
              No guides match “{query}”.{" "}
              <button onClick={() => setQuery("")}>Clear search</button>
            </p>
          )}
        </div>
      </dialog>
    </>
  );
}
