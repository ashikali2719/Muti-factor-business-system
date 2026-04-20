import { AnalysisResult } from '../types';
import { getActionStyle, getLevelStyle } from '../utils/decisionEngine';
import ConfidenceBar from './ConfidenceBar';
import PriceComparison from './PriceComparison';
import BusinessInsight from './BusinessInsight';
import { ClipboardList, AlertCircle } from 'lucide-react';

interface ResultCardProps {
  result: AnalysisResult;
}

export default function ResultCard({ result }: ResultCardProps) {
  const actionStyle = getActionStyle(result.recommendedAction);
  const levelStyle = getLevelStyle(result.decisionLevel);

  return (
    <div className="space-y-4 animate-fadeIn">
      <div className={`bg-white rounded-2xl shadow-sm border ${actionStyle.border} p-6`}>
        <div className="flex items-start justify-between gap-4 flex-wrap mb-5">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <ClipboardList className="w-4 h-4 text-slate-400" />
              <span className="text-xs font-medium text-slate-400 uppercase tracking-wide">Analysis Result</span>
            </div>
            <h2 className="text-lg font-bold text-slate-800">{result.productName}</h2>
          </div>
          <div className={`px-4 py-2 rounded-xl ${actionStyle.badge} text-white text-sm font-bold tracking-wide shadow-sm`}>
            {result.recommendedAction}
          </div>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-6">
          <div className="bg-slate-50 rounded-xl p-3 border border-slate-100">
            <p className="text-xs text-slate-400 font-medium uppercase tracking-wide mb-1">Competitor Price</p>
            <p className="text-base font-bold text-slate-800">₹{result.competitorPrice.toLocaleString()}</p>
          </div>
          <div className="bg-slate-50 rounded-xl p-3 border border-slate-100">
            <p className="text-xs text-slate-400 font-medium uppercase tracking-wide mb-1">Your Price</p>
            <p className="text-base font-bold text-slate-800">₹{result.yourPrice.toLocaleString()}</p>
          </div>
          <div className="bg-slate-50 rounded-xl p-3 border border-slate-100">
            <p className="text-xs text-slate-400 font-medium uppercase tracking-wide mb-1">Confidence</p>
            <p className="text-base font-bold text-slate-800">{result.confidence}%</p>
          </div>
          <div className={`rounded-xl p-3 border ${levelStyle.bg}`}>
            <p className="text-xs text-slate-400 font-medium uppercase tracking-wide mb-1">Decision Level</p>
            <div className="flex items-center gap-1.5">
              <span className={`w-2 h-2 rounded-full ${levelStyle.dot}`} />
              <p className={`text-base font-bold ${levelStyle.text}`}>{result.decisionLevel}</p>
            </div>
          </div>
        </div>

        <div className="mb-5">
          <ConfidenceBar confidence={result.confidence} barColor={actionStyle.bar} />
        </div>

        <div className={`flex items-start gap-3 p-4 rounded-xl ${actionStyle.bg} border ${actionStyle.border}`}>
          <AlertCircle className={`w-4 h-4 mt-0.5 shrink-0 ${actionStyle.text}`} />
          <p className={`text-sm leading-relaxed ${actionStyle.text}`}>{result.summary}</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <PriceComparison yourPrice={result.yourPrice} competitorPrice={result.competitorPrice} />
        <BusinessInsight insights={result.insights} summary={result.summary} />
      </div>
    </div>
  );
}
