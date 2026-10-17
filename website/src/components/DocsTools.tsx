"use client";
import { useEffect, useState } from "react";
import { Copy, Moon, Sun } from "lucide-react";

export function ThemeToggle() {
  const [light, setLight] = useState(false);
  useEffect(() => {
    setLight(localStorage.getItem("docs-theme") === "light");
  }, []);
  useEffect(() => {
    document
      .querySelector(".docs-shell")
      ?.setAttribute("data-theme", light ? "light" : "dark");
  }, [light]);
  return (
    <button
      aria-label="Toggle theme"
      onClick={() => {
        localStorage.setItem("docs-theme", light ? "dark" : "light");
        setLight(!light);
      }}
    >
      {light ? <Moon size={18} /> : <Sun size={18} />}
    </button>
  );
}
export function CopyMarkdown({ markdown }: { markdown: string }) {
  const [status, setStatus] = useState("Copy as Markdown");
  return (
    <button
      className="copy-markdown"
      onClick={async () => {
        try {
          await navigator.clipboard.writeText(markdown);
          setStatus("Copied");
        } catch {
          setStatus("Copy unavailable — use source link");
        }
      }}
    >
      <Copy size={14} />
      <span role="status">{status}</span>
    </button>
  );
}
