'use client';

import React from 'react';
import { MarketingNav, MarketingFooter } from '@/components/MarketingLayout';
import { HeroSection } from '@/components/sections/HeroSection';
import { StatsSection } from '@/components/sections/StatsSection';
import { FeaturesSection } from '@/components/sections/FeaturesSection';
import { CodeExamplesSection } from '@/components/sections/CodeExamplesSection';
import { QuickStartSection } from '@/components/sections/QuickStartSection';
import { CTASection } from '@/components/sections/CTASection';

export default function Page() {
  const scrollToFeatures = () => {
    document.querySelector('#features')?.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <div className="min-h-screen bg-dark-navy">
      <MarketingNav transparent />
      <HeroSection onLearnMore={scrollToFeatures} />
      <StatsSection />
      <FeaturesSection />
      <CodeExamplesSection />
      <QuickStartSection />
      <CTASection />
      <MarketingFooter />
    </div>
  );
}
