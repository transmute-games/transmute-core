'use client';

import React from 'react';
import { ArrowDown, Download, Github } from 'lucide-react';

interface HeroSectionProps {
  onLearnMore: () => void;
}

export function HeroSection({ onLearnMore }: HeroSectionProps) {
  return (
    <section className="relative min-h-screen flex items-center justify-center overflow-hidden bg-gradient-to-br from-dark-navy via-dark-slate to-dark-navy">
      {/* Animated background grid */}
      <div className="absolute inset-0 opacity-10">
        <div className="absolute inset-0" style={{
          backgroundImage: `
            linear-gradient(to right, #00d9ff 1px, transparent 1px),
            linear-gradient(to bottom, #00d9ff 1px, transparent 1px)
          `,
          backgroundSize: '40px 40px'
        }} />
      </div>

      {/* Glowing orbs */}
      <div className="absolute top-20 left-10 w-96 h-96 bg-transmute-cyan/20 rounded-full blur-3xl animate-pulse-slow" />
      <div className="absolute bottom-20 right-10 w-96 h-96 bg-transmute-lime/20 rounded-full blur-3xl animate-pulse-slow" style={{ animationDelay: '1s' }} />

      <div className="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 text-center">
        {/* Badge */}
        <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-transmute-cyan/10 border border-transmute-cyan/30 mb-8 animate-fade-in">
          <span className="w-2 h-2 rounded-full bg-transmute-lime animate-pulse" />
          <span className="text-sm text-transmute-cyan font-medium">
            Open Source · MIT License · Java 17+
          </span>
        </div>

        {/* Main heading */}
        <h1 className="text-5xl sm:text-6xl lg:text-7xl font-bold mb-6 animate-slide-up">
          <span className="text-cream">Build Retro Games</span>
          <br />
          <span className="text-gradient">Blazing Fast</span>
        </h1>

        {/* Subtitle */}
        <p className="text-xl sm:text-2xl text-cream/70 max-w-3xl mx-auto mb-12 animate-slide-up" style={{ animationDelay: '0.1s' }}>
          TransmuteCore is a high-performance 2D pixel game engine for Java with
          custom rendering, fixed 60 FPS game loop, and zero reflection overhead.
        </p>

        {/* CTA Buttons */}
        <div className="flex flex-col sm:flex-row items-center justify-center gap-4 mb-16 animate-slide-up" style={{ animationDelay: '0.2s' }}>
          <a
            href="https://github.com/transmute-games/transmute-core"
            target="_blank"
            rel="noopener noreferrer"
            className="w-full sm:w-auto px-8 py-4 rounded-lg bg-gradient-to-r from-transmute-cyan to-transmute-lime text-dark-navy font-bold text-lg hover:shadow-2xl hover:shadow-transmute-cyan/50 transition-all transform hover:scale-105"
          >
            <span className="flex items-center justify-center gap-2">
              <Download className="w-5 h-5" />
              Get Started
            </span>
          </a>
          <a
            href="https://github.com/transmute-games/transmute-core"
            target="_blank"
            rel="noopener noreferrer"
            className="w-full sm:w-auto px-8 py-4 rounded-lg border-2 border-transmute-cyan/50 text-cream font-bold text-lg hover:bg-transmute-cyan/10 transition-all"
          >
            <span className="flex items-center justify-center gap-2">
              <Github className="w-5 h-5" />
              View on GitHub
            </span>
          </a>
        </div>

        {/* Code snippet preview */}
        <div className="max-w-2xl mx-auto bg-dark-navy/80 backdrop-blur-sm rounded-xl border border-transmute-cyan/30 p-6 text-left animate-slide-up" style={{ animationDelay: '0.3s' }}>
          <div className="flex items-center gap-2 mb-4">
            <div className="flex gap-2">
              <div className="w-3 h-3 rounded-full bg-red-500/80" />
              <div className="w-3 h-3 rounded-full bg-yellow-500/80" />
              <div className="w-3 h-3 rounded-full bg-green-500/80" />
            </div>
            <span className="text-slate-gray text-sm">terminal</span>
          </div>
          <div className="font-mono text-sm space-y-1">
            <div className="text-transmute-cyan">$ transmute new my-game</div>
            <div className="text-cream/60">Creating new TransmuteCore project...</div>
            <div className="text-transmute-lime">✓ Project created successfully!</div>
          </div>
        </div>

        {/* Scroll indicator */}
        <button
          onClick={onLearnMore}
          className="absolute bottom-8 left-1/2 transform -translate-x-1/2 animate-bounce"
          aria-label="Learn more"
        >
          <ArrowDown className="w-8 h-8 text-transmute-cyan" />
        </button>
      </div>
    </section>
  );
}
