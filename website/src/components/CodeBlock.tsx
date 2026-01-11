'use client';

import React from 'react';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { vscDarkPlus } from 'react-syntax-highlighter/dist/esm/styles/prism';

interface CodeBlockProps {
  code: string;
  language?: string;
  showLineNumbers?: boolean;
}

export function CodeBlock({
  code,
  language = 'java',
  showLineNumbers = true,
}: CodeBlockProps) {
  const customStyle = {
    ...vscDarkPlus,
    'pre[class*="language-"]': {
      ...vscDarkPlus['pre[class*="language-"]'],
      background: '#0a0e27',
      borderRadius: '0.75rem',
      padding: '1.5rem',
      fontSize: '0.875rem',
      lineHeight: '1.7',
    },
  };

  return (
    <div className="relative rounded-xl overflow-hidden border border-slate-gray/30 shadow-2xl">
      {/* Code header with dots */}
      <div className="bg-dark-slate px-4 py-3 border-b border-slate-gray/30 flex items-center gap-2">
        <div className="flex gap-2">
          <div className="w-3 h-3 rounded-full bg-red-500 opacity-80" />
          <div className="w-3 h-3 rounded-full bg-yellow-500 opacity-80" />
          <div className="w-3 h-3 rounded-full bg-green-500 opacity-80" />
        </div>
        <span className="text-slate-gray text-sm ml-2">{language}</span>
      </div>
      <SyntaxHighlighter
        language={language}
        style={customStyle}
        showLineNumbers={showLineNumbers}
        wrapLines={true}
        customStyle={{
          margin: 0,
          borderRadius: 0,
          background: '#0a0e27',
        }}
      >
        {code}
      </SyntaxHighlighter>
    </div>
  );
}
