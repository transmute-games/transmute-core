'use client';

import React from 'react';
import Link from 'next/link';
import { Github, Book, Zap, Menu, X } from 'lucide-react';
import clsx from 'clsx';

interface MarketingNavProps {
  transparent?: boolean;
}

export function MarketingNav({ transparent = false }: MarketingNavProps) {
  const [isScrolled, setIsScrolled] = React.useState(false);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = React.useState(false);

  React.useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 20);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const navClasses = clsx(
    'fixed top-0 left-0 right-0 z-50 transition-all duration-300',
    transparent && !isScrolled
      ? 'bg-transparent'
      : 'bg-dark-navy/95 backdrop-blur-sm border-b border-transmute-cyan/20'
  );

  return (
    <nav className={navClasses}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link href="/" className="flex items-center gap-3 hover:opacity-80 transition-opacity">
            <img
              src="/favicon.svg"
              alt="TransmuteCore Logo"
              className="w-10 h-10"
            />
            <span className="text-xl font-bold text-gradient">
              TransmuteCore
            </span>
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center gap-8">
            <a
              href="#features"
              className="text-cream/80 hover:text-transmute-cyan transition-colors"
            >
              Features
            </a>
            <a
              href="#quick-start"
              className="text-cream/80 hover:text-transmute-cyan transition-colors"
            >
              Quick Start
            </a>
            <a
              href="https://github.com/transmute-games/transmute-core"
              target="_blank"
              rel="noopener noreferrer"
              className="text-cream/80 hover:text-transmute-cyan transition-colors flex items-center gap-2"
            >
              <Github className="w-4 h-4" />
              GitHub
            </a>
            <a
              href="https://github.com/transmute-games/transmute-core/blob/master/docs/GETTING_STARTED.md"
              target="_blank"
              rel="noopener noreferrer"
              className="text-cream/80 hover:text-transmute-cyan transition-colors flex items-center gap-2"
            >
              <Book className="w-4 h-4" />
              Docs
            </a>
            <a
              href="https://github.com/transmute-games/transmute-core"
              target="_blank"
              rel="noopener noreferrer"
              className="px-6 py-2 rounded-lg bg-gradient-to-r from-transmute-cyan to-transmute-lime text-dark-navy font-semibold hover:shadow-lg hover:shadow-transmute-cyan/50 transition-all"
            >
              Get Started
            </a>
          </div>

          {/* Mobile menu button */}
          <button
            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
            className="md:hidden text-cream p-2"
          >
            {isMobileMenuOpen ? (
              <X className="w-6 h-6" />
            ) : (
              <Menu className="w-6 h-6" />
            )}
          </button>
        </div>
      </div>

      {/* Mobile Menu */}
      {isMobileMenuOpen && (
        <div className="md:hidden bg-dark-slate border-t border-transmute-cyan/20">
          <div className="px-4 py-4 space-y-3">
            <a
              href="#features"
              onClick={() => setIsMobileMenuOpen(false)}
              className="block text-cream/80 hover:text-transmute-cyan transition-colors py-2"
            >
              Features
            </a>
            <a
              href="#quick-start"
              onClick={() => setIsMobileMenuOpen(false)}
              className="block text-cream/80 hover:text-transmute-cyan transition-colors py-2"
            >
              Quick Start
            </a>
            <a
              href="https://github.com/transmute-games/transmute-core"
              target="_blank"
              rel="noopener noreferrer"
              className="block text-cream/80 hover:text-transmute-cyan transition-colors flex items-center gap-2 py-2"
            >
              <Github className="w-4 h-4" />
              GitHub
            </a>
            <a
              href="https://github.com/transmute-games/transmute-core/blob/master/docs/GETTING_STARTED.md"
              target="_blank"
              rel="noopener noreferrer"
              className="block text-cream/80 hover:text-transmute-cyan transition-colors flex items-center gap-2 py-2"
            >
              <Book className="w-4 h-4" />
              Docs
            </a>
            <a
              href="https://github.com/transmute-games/transmute-core"
              target="_blank"
              rel="noopener noreferrer"
              className="block px-6 py-2 rounded-lg bg-gradient-to-r from-transmute-cyan to-transmute-lime text-dark-navy font-semibold text-center"
            >
              Get Started
            </a>
          </div>
        </div>
      )}
    </nav>
  );
}

export function MarketingFooter() {
  return (
    <footer className="bg-dark-slate border-t border-transmute-cyan/20">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* Brand */}
          <div className="col-span-1 md:col-span-2">
            <div className="flex items-center gap-3 mb-4">
              <img
                src="/favicon.svg"
                alt="TransmuteCore Logo"
                className="w-10 h-10"
              />
              <span className="text-xl font-bold text-gradient">
                TransmuteCore
              </span>
            </div>
            <p className="text-cream/60 max-w-md">
              High-performance 2D pixel game engine for Java. Build retro-style
              games with custom pixel rendering, fixed timestep game loop, and
              built-in animation system.
            </p>
            <div className="flex gap-4 mt-4">
              <a
                href="https://github.com/transmute-games/transmute-core"
                target="_blank"
                rel="noopener noreferrer"
                className="text-cream/60 hover:text-transmute-cyan transition-colors"
              >
                <Github className="w-5 h-5" />
              </a>
            </div>
          </div>

          {/* Links */}
          <div>
            <h3 className="font-semibold text-cream mb-4">Resources</h3>
            <ul className="space-y-2">
              <li>
                <a
                  href="https://github.com/transmute-games/transmute-core/blob/master/docs/GETTING_STARTED.md"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-cream/60 hover:text-transmute-cyan transition-colors"
                >
                  Getting Started
                </a>
              </li>
              <li>
                <a
                  href="https://github.com/transmute-games/transmute-core/tree/master/docs/tutorials"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-cream/60 hover:text-transmute-cyan transition-colors"
                >
                  Tutorials
                </a>
              </li>
              <li>
                <a
                  href="https://github.com/transmute-games/transmute-core"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-cream/60 hover:text-transmute-cyan transition-colors"
                >
                  API Reference
                </a>
              </li>
            </ul>
          </div>

          {/* Community */}
          <div>
            <h3 className="font-semibold text-cream mb-4">Community</h3>
            <ul className="space-y-2">
              <li>
                <a
                  href="https://github.com/transmute-games/transmute-core"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-cream/60 hover:text-transmute-cyan transition-colors"
                >
                  GitHub
                </a>
              </li>
              <li>
                <a
                  href="https://github.com/transmute-games/transmute-core/issues"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-cream/60 hover:text-transmute-cyan transition-colors"
                >
                  Issues
                </a>
              </li>
              <li>
                <a
                  href="https://github.com/transmute-games/transmute-core/discussions"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-cream/60 hover:text-transmute-cyan transition-colors"
                >
                  Discussions
                </a>
              </li>
              <li>
                <a
                  href="https://github.com/transmute-games"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-cream/60 hover:text-transmute-cyan transition-colors"
                >
                  Transmute Games
                </a>
              </li>
            </ul>
          </div>
        </div>

        <div className="mt-8 pt-8 border-t border-transmute-cyan/20 text-center text-cream/60 text-sm">
          <p>
            © {new Date().getFullYear()} Transmute Games. Released under the MIT
            License.
          </p>
        </div>
      </div>
    </footer>
  );
}
