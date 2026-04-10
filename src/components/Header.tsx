import { BarChart3, Brain } from 'lucide-react';

export default function Header() {
  return (
    <header className="bg-gradient-to-r from-slate-900 via-slate-800 to-slate-900 shadow-lg">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
        <div className="flex items-center gap-4">
          <div className="flex items-center justify-center w-12 h-12 rounded-xl bg-sky-500/20 border border-sky-500/30">
            <Brain className="w-6 h-6 text-sky-400" />
          </div>
          <div>
            <h1 className="text-xl sm:text-2xl font-bold text-white tracking-tight">
              Business Decision Support System
            </h1>
            <p className="text-sm text-slate-400 mt-0.5 flex items-center gap-1.5">
              <BarChart3 className="w-3.5 h-3.5" />
              Intelligent multi-factor inventory and pricing decision dashboard
            </p>
          </div>
          <div className="ml-auto hidden sm:flex items-center gap-2">
            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-500/15 border border-emerald-500/25 text-emerald-400 text-xs font-medium">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse" />
              Live Prototype
            </span>
          </div>
        </div>
      </div>
    </header>
  );
}
