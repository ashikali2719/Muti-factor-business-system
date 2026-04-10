import { Tag, TrendingDown, TrendingUp } from 'lucide-react';

interface PriceComparisonProps {
  yourPrice: number;
  competitorPrice: number;
}

export default function PriceComparison({ yourPrice, competitorPrice }: PriceComparisonProps) {
  const max = Math.max(yourPrice, competitorPrice) * 1.15;
  const yourWidth = Math.round((yourPrice / max) * 100);
  const competitorWidth = Math.round((competitorPrice / max) * 100);

  const diff = competitorPrice - yourPrice;
  const diffPct = competitorPrice > 0 ? Math.abs(Math.round((diff / competitorPrice) * 100)) : 0;
  const youAreLower = yourPrice < competitorPrice;

  return (
    <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
      <div className="flex items-center justify-between mb-5">
        <div>
          <h3 className="text-base font-semibold text-slate-800">Price Comparison</h3>
          <p className="text-sm text-slate-400 mt-0.5">Your price vs live competitor price</p>
        </div>
        <div className={`flex items-center gap-1 text-xs font-semibold px-2.5 py-1 rounded-full ${
          youAreLower
            ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
            : 'bg-red-50 text-red-700 border border-red-200'
        }`}>
          {youAreLower ? <TrendingDown className="w-3.5 h-3.5" /> : <TrendingUp className="w-3.5 h-3.5" />}
          {youAreLower ? `${diffPct}% below market` : `${diffPct}% above market`}
        </div>
      </div>

      <div className="space-y-5">
        <div>
          <div className="flex items-center justify-between mb-2">
            <div className="flex items-center gap-2">
              <div className="w-2.5 h-2.5 rounded-full bg-sky-500" />
              <span className="text-sm font-medium text-slate-700">Your Price</span>
            </div>
            <span className="text-sm font-bold text-slate-800">₹{yourPrice.toLocaleString()}</span>
          </div>
          <div className="w-full bg-slate-100 rounded-full h-4 overflow-hidden">
            <div
              className="h-4 rounded-full bg-sky-500 transition-all duration-700 ease-out"
              style={{ width: `${yourWidth}%` }}
            />
          </div>
        </div>

        <div>
          <div className="flex items-center justify-between mb-2">
            <div className="flex items-center gap-2">
              <div className="w-2.5 h-2.5 rounded-full bg-rose-400" />
              <span className="text-sm font-medium text-slate-700">Competitor Price</span>
            </div>
            <span className="text-sm font-bold text-slate-800">₹{competitorPrice.toLocaleString()}</span>
          </div>
          <div className="w-full bg-slate-100 rounded-full h-4 overflow-hidden">
            <div
              className="h-4 rounded-full bg-rose-400 transition-all duration-700 ease-out"
              style={{ width: `${competitorWidth}%` }}
            />
          </div>
        </div>
      </div>

      <div className="mt-5 pt-4 border-t border-gray-100">
        <div className="flex items-center gap-2 text-sm">
          <Tag className="w-4 h-4 text-slate-400" />
          <span className="text-slate-500">
            Price difference:{' '}
            <span className={`font-semibold ${youAreLower ? 'text-emerald-600' : 'text-red-600'}`}>
              ₹{Math.abs(diff).toLocaleString()} {youAreLower ? 'cheaper than market' : 'above market'}
            </span>
          </span>
        </div>
      </div>
    </div>
  );
}
