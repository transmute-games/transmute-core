'use client';

import React from 'react';
import { Gamepad2, Palette, Box, Music, Database, FileCode } from 'lucide-react';

export function FeaturesSection() {
  const features = [
    {
      icon: Gamepad2,
      title: 'Fixed Timestep Game Loop',
      description: 'Consistent 60 FPS game logic with delta time support for smooth gameplay.',
      gradient: 'from-transmute-cyan to-transmute-blue',
    },
    {
      icon: Palette,
      title: 'Custom Pixel Rendering',
      description: 'Direct pixel manipulation using BufferedImage and DataBufferInt for maximum performance.',
      gradient: 'from-transmute-lime to-transmute-cyan',
    },
    {
      icon: Box,
      title: 'Sprite & Animation System',
      description: 'Built-in sprite sheet support with frame-based animations out of the box.',
      gradient: 'from-transmute-purple to-transmute-blue',
    },
    {
      icon: FileCode,
      title: 'State Management',
      description: 'Stack-based state system for managing menus, gameplay, pause screens, and more.',
      gradient: 'from-transmute-blue to-transmute-lime',
    },
    {
      icon: Database,
      title: 'Asset Management',
      description: 'Deferred loading system with automatic asset registration and batch loading.',
      gradient: 'from-transmute-cyan to-transmute-purple',
    },
    {
      icon: Music,
      title: 'Audio Support',
      description: 'Built-in audio playback for sound effects and background music in your games.',
      gradient: 'from-transmute-lime to-transmute-purple',
    },
  ];

  return (
    <section id="features" className="py-20 bg-dark-navy">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Section header */}
        <div className="text-center mb-16">
          <h2 className="text-4xl sm:text-5xl font-bold text-cream mb-4">
            Everything You Need
          </h2>
          <p className="text-xl text-cream/60 max-w-3xl mx-auto">
            TransmuteCore comes packed with features to help you build retro-style
            2D games quickly and efficiently.
          </p>
        </div>

        {/* Features grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {features.map((feature, index) => (
            <div
              key={index}
              className="group p-6 rounded-xl bg-dark-slate border border-transmute-cyan/20 hover:border-transmute-cyan/50 transition-all hover:scale-105"
            >
              <div className={`w-12 h-12 rounded-lg bg-gradient-to-br ${feature.gradient} p-[2px] mb-4`}>
                <div className="w-full h-full rounded-lg bg-dark-slate flex items-center justify-center">
                  <feature.icon className="w-6 h-6 text-transmute-cyan" />
                </div>
              </div>
              <h3 className="text-xl font-bold text-cream mb-2">
                {feature.title}
              </h3>
              <p className="text-cream/60">
                {feature.description}
              </p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
