"use client";
import { useState } from "react";
const colors = [
  ["Paper", "#f7f7f2"],
  ["Charcoal", "#292b25"],
  ["Sage", "#526447"],
  ["Soft sage", "#eceee5"],
  ["White", "#ffffff"],
];
export function Palette() {
  const [status, setStatus] = useState("");
  async function copy(name: string, hex: string) {
    try {
      await navigator.clipboard.writeText(hex);
      setStatus(`Copied ${name}: ${hex}`);
    } catch {
      setStatus(`Could not copy. Select and copy this value: ${hex}`);
    }
  }
  return (
    <>
      <div className="palette">
        {colors.map(([name, hex]) => (
          <button
            key={hex}
            onClick={() => copy(name, hex)}
            aria-label={`Copy ${name} ${hex}`}
          >
            <span className="swatch" style={{ background: hex }} />
            <strong>{name}</strong>
            <code>{hex}</code>
          </button>
        ))}
      </div>
      <p role="status" className="palette-status">
        {status || "Hex values are ready to copy."}
      </p>
    </>
  );
}
