'use client';

import React from 'react';
import { Zap, Code2, Clock, Users } from 'lucide-react';

export function StatsSection() {
  const stats = [
    {
      icon: Zap,
      value: '60 FPS',
      label: 'Stable Performance',
      color: 'text-transmute-cyan',
    },
    {
      icon: Code2,
      value: 'Java 17+',
      label: 'Modern Java',
      color: 'text-transmute-lime',
    },
    {
      icon: Clock,
      value: '< 50ms',
      label: 'Startup Time',
      color: 'text-transmute-purple',
    },
    {
      icon: Users,
      value: 'Open Source',
      label: 'MIT License',
      color: 'text-transmute-blue',
    },
  ];

  return (
    <section className="py-20 bg-dark-slate border-y border-transmute-cyan/20">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-8">
          {stats.map((stat, index) => (
            <div
              key={index}
              className="text-center group hover:scale-105 transition-transform"
            >
              <div className="flex justify-center mb-4">
                <div className="p-4 rounded-xl bg-dark-navy border border-transmute-cyan/30 group-hover:border-transmute-lime/50 transition-colors">
                  <stat.icon className={`w-8 h-8 ${stat.color}`} />
                </div>
              </div>
              <div className={`text-3xl font-bold ${stat.color} mb-2`}>
                {stat.value}
              </div>
              <div className="text-cream/60 text-sm">{stat.label}</div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
