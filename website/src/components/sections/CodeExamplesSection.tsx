'use client';

import React from 'react';
import { CodeBlock } from '../CodeBlock';

export function CodeExamplesSection() {
  const helloWorldCode = `import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.GameConfig;
import TransmuteCore.core.Manager;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Context;
import TransmuteCore.graphics.Color;

public class MyGame extends TransmuteCore {
    public MyGame(GameConfig config) {
        super(config);
    }

    @Override
    public void init() {
        // Initialize your game
    }

    @Override
    public void update(Manager manager, double delta) {
        // Update game logic (60 times/second)
    }

    @Override
    public void render(Manager manager, IRenderer renderer) {
        Context ctx = (Context) renderer;
        ctx.renderText("Hello, TransmuteCore!", 50, 100,
                      Color.toPixelInt(255, 255, 255, 255));
    }

    public static void main(String[] args) {
        GameConfig config = new GameConfig.Builder()
            .title("My Game")
            .version("1.0")
            .dimensions(320, GameConfig.ASPECT_RATIO_SQUARE)
            .scale(3)
            .build();

        MyGame game = new MyGame(config);
        game.start();
    }
}`;

  return (
    <section className="py-20 bg-dark-slate">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Section header */}
        <div className="text-center mb-16">
          <h2 className="text-4xl sm:text-5xl font-bold text-cream mb-4">
            Simple & Powerful API
          </h2>
          <p className="text-xl text-cream/60 max-w-3xl mx-auto">
            Get started with just a few lines of code. TransmuteCore&apos;s API is
            designed to be intuitive and easy to learn.
          </p>
        </div>

        {/* Code example */}
        <div className="max-w-4xl mx-auto">
          <CodeBlock code={helloWorldCode} language="java" />
          <div className="mt-8 text-center">
            <p className="text-cream/60 mb-4">
              That&apos;s it! You now have a working game window with custom rendering.
            </p>
            <a
              href="https://github.com/transmute-games/transmute-core/blob/master/docs/tutorials/01-hello-world.md"
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center gap-2 text-transmute-cyan hover:text-transmute-lime transition-colors"
            >
              View Full Tutorial →
            </a>
          </div>
        </div>
      </div>
    </section>
  );
}
