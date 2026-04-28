import { BarChart3 } from 'lucide-react';

export default function Footer() {
  return (
    <footer className="bg-white border-t border-gray-100 mt-10">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-5">
        <div className="flex flex-col sm:flex-row items-center justify-between gap-3">
          <div className="flex items-center gap-2 text-slate-400">
            <BarChart3 className="w-4 h-4" />
            <p className="text-sm">
              Prototype dashboard for business decision support
            </p>
          </div>
          <div className="flex items-center gap-4 text-xs text-slate-400">
            <span>Multi-factor Decision Engine</span>
            <span className="w-1 h-1 rounded-full bg-slate-300" />
            <span>Frontend Prototype — Backend Integration Ready</span>
          </div>
        </div>
      </div>
    </footer>
  );
}
