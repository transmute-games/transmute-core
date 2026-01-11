'use client';

import React from 'react';
import { Github, Book, Rocket } from 'lucide-react';

export function CTASection() {
  return (
    <section className="py-20 bg-gradient-to-br from-dark-slate via-dark-navy to-dark-slate relative overflow-hidden">
      {/* Background decoration */}
      <div className="absolute inset-0 opacity-10">
        <div className="absolute top-0 left-0 w-full h-full"
          style={{
            backgroundImage: `radial-gradient(circle at 50% 50%, #00d9ff 1px, transparent 1px)`,
            backgroundSize: '50px 50px'
          }}
        />
      </div>

      <div className="relative z-10 max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-transmute-lime/10 border border-transmute-lime/30 mb-8">
          <Rocket className="w-4 h-4 text-transmute-lime" />
          <span className="text-sm text-transmute-lime font-medium">
            Start Building Today
          </span>
        </div>

        <h2 className="text-4xl sm:text-5xl font-bold text-cream mb-6">
          Ready to Build Your Game?
        </h2>

        <p className="text-xl text-cream/70 max-w-2xl mx-auto mb-12">
          Join developers worldwide using Transmute Core to create amazing retro-style
          2D games. Open source, MIT licensed, and free forever.
        </p>

        <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
          <a
            href="https://github.com/transmute-games/transmute-core"
            target="_blank"
            rel="noopener noreferrer"
            className="w-full sm:w-auto px-8 py-4 rounded-lg bg-gradient-to-r from-transmute-cyan to-transmute-lime text-dark-navy font-bold text-lg hover:shadow-2xl hover:shadow-transmute-cyan/50 transition-all transform hover:scale-105 flex items-center justify-center gap-2"
          >
            <Github className="w-5 h-5" />
            View on GitHub
          </a>
          <a
            href="https://github.com/transmute-games/transmute-core/blob/master/docs/GETTING_STARTED.md"
            target="_blank"
            rel="noopener noreferrer"
            className="w-full sm:w-auto px-8 py-4 rounded-lg border-2 border-transmute-cyan/50 text-cream font-bold text-lg hover:bg-transmute-cyan/10 transition-all flex items-center justify-center gap-2"
          >
            <Book className="w-5 h-5" />
            Read the Docs
          </a>
        </div>

        <div className="mt-12 pt-12 border-t border-transmute-cyan/20">
          <p className="text-cream/60 text-sm">
            Transmute Core is maintained by <a href="https://github.com/transmute-games" target="_blank" rel="noopener noreferrer" className="text-transmute-cyan hover:text-transmute-lime transition-colors">Transmute Games</a> and the open source community.
          </p>
        </div>
      </div>
    </section>
  );
}
