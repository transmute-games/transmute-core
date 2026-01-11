'use client';

import React from 'react';
import { Terminal, Download, Play } from 'lucide-react';

export function QuickStartSection() {
  const steps = [
    {
      icon: Download,
      title: 'Install the CLI',
      description: 'Download and install the Transmute CLI tool',
      code: 'curl -fsSL https://raw.githubusercontent.com/transmute-games/transmute-core/master/scripts/install-cli.sh | sh',
      color: 'text-transmute-cyan',
    },
    {
      icon: Terminal,
      title: 'Create Your Project',
      description: 'Generate a new game project with the CLI',
      code: 'transmute new my-game',
      color: 'text-transmute-lime',
    },
    {
      icon: Play,
      title: 'Run Your Game',
      description: 'Build and run your game instantly',
      code: './gradlew run',
      color: 'text-transmute-purple',
    },
  ];

  return (
    <section id="quick-start" className="py-20 bg-dark-navy">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Section header */}
        <div className="text-center mb-16">
          <h2 className="text-4xl sm:text-5xl font-bold text-cream mb-4">
            Get Started in Minutes
          </h2>
          <p className="text-xl text-cream/60 max-w-3xl mx-auto">
            Create your first TransmuteCore game with just three simple commands.
          </p>
        </div>

        {/* Steps */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 max-w-6xl mx-auto">
          {steps.map((step, index) => (
            <div
              key={index}
              className="relative p-6 rounded-xl bg-dark-slate border border-transmute-cyan/20 hover:border-transmute-cyan/50 transition-all"
            >
              {/* Step number */}
              <div className="absolute -top-4 -left-4 w-10 h-10 rounded-full bg-gradient-to-br from-transmute-cyan to-transmute-lime flex items-center justify-center text-dark-navy font-bold text-lg">
                {index + 1}
              </div>

              {/* Icon */}
              <div className="flex justify-center mb-4">
                <div className="p-3 rounded-lg bg-dark-navy border border-transmute-cyan/30">
                  <step.icon className={`w-8 h-8 ${step.color}`} />
                </div>
              </div>

              {/* Content */}
              <h3 className="text-xl font-bold text-cream mb-2 text-center">
                {step.title}
              </h3>
              <p className="text-cream/60 text-center mb-4">
                {step.description}
              </p>

              {/* Code snippet */}
              <div className="bg-dark-navy rounded-lg p-4 border border-transmute-cyan/20">
                <code className="text-transmute-cyan text-sm font-mono break-all">
                  $ {step.code}
                </code>
              </div>
            </div>
          ))}
        </div>

        {/* Additional info */}
        <div className="mt-16 text-center">
          <div className="inline-flex flex-col sm:flex-row items-center gap-4 p-6 rounded-xl bg-dark-slate border border-transmute-cyan/20">
            <div className="text-cream/60">
              <strong className="text-cream">Prerequisites:</strong> Java 17 or higher
            </div>
            <a
              href="https://github.com/transmute-games/transmute-core/blob/master/docs/GETTING_STARTED.md"
              target="_blank"
              rel="noopener noreferrer"
              className="px-6 py-2 rounded-lg bg-gradient-to-r from-transmute-cyan to-transmute-lime text-dark-navy font-semibold hover:shadow-lg hover:shadow-transmute-cyan/50 transition-all"
            >
              View Full Guide
            </a>
          </div>
        </div>
      </div>
    </section>
  );
}
