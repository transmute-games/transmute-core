/** @type {import('tailwindcss').Config} */
export default {
  content: ['./app/**/*.{js,ts,jsx,tsx}', './src/**/*.{js,ts,jsx,tsx}'],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        cream: '#fefdfb',
        'dark-navy': '#0a0e27',
        'dark-slate': '#1a1d29',
        'transmute-cyan': '#00d9ff',
        'transmute-blue': '#0066ff',
        'transmute-lime': '#b4ff39',
        'transmute-purple': '#6b46ff',
        'dark-gray': '#2d3748',
        'slate-gray': '#475569',
      },
      animation: {
        'fade-in': 'fadeIn 0.5s ease-in',
        'slide-up': 'slideUp 0.5s ease-out',
        'slide-in-right': 'slideInRight 0.3s ease-out',
        'pulse-slow': 'pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        'glow': 'glow 2s ease-in-out infinite alternate',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { transform: 'translateY(20px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
        slideInRight: {
          '0%': { transform: 'translateX(100%)', opacity: '0' },
          '100%': { transform: 'translateX(0)', opacity: '1' },
        },
        glow: {
          '0%': { filter: 'drop-shadow(0 0 5px rgba(0, 217, 255, 0.5))' },
          '100%': { filter: 'drop-shadow(0 0 20px rgba(180, 255, 57, 0.8))' },
        },
      },
    },
  },
  plugins: [],
};
