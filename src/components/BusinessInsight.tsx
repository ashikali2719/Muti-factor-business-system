import { Lightbulb, CheckCircle } from 'lucide-react';

interface BusinessInsightProps {
  insights: string[];
  summary: string;
}

export default function BusinessInsight({ insights, summary }: BusinessInsightProps) {
  return (
    <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
      <div className="flex items-center gap-2.5 mb-4">
        <div className="flex items-center justify-center w-8 h-8 rounded-lg bg-amber-50">
          <Lightbulb className="w-4 h-4 text-amber-500" />
        </div>
        <div>
          <h3 className="text-base font-semibold text-slate-800">Business Insights</h3>
          <p className="text-xs text-slate-400">Live analysis summary from backend</p>
        </div>
      </div>

      <div className="text-sm text-slate-600 leading-relaxed mb-4">
        {summary}
      </div>

      {insights.length > 0 ? (
        <ul className="space-y-3">
          {insights.map((insight, i) => (
            <li key={i} className="flex items-start gap-3">
              <CheckCircle className="w-4 h-4 text-sky-500 mt-0.5 shrink-0" />
              <p className="text-sm text-slate-600 leading-relaxed">{insight}</p>
            </li>
          ))}
        </ul>
      ) : null}

      <div className="mt-4 pt-4 border-t border-gray-100">
        <p className="text-xs text-slate-400 italic">
          Insights are based on live backend analysis from competitor price and inventory data.
        </p>
      </div>
    </div>
  );
}
