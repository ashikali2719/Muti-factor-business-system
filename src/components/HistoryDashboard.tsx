import { AnalysisResult } from '../types';
import { Trash2, Clock } from 'lucide-react';

interface HistoryDashboardProps {
  history: AnalysisResult[];
  onClearHistory: () => void;
}

export default function HistoryDashboard({ history, onClearHistory }: HistoryDashboardProps) {
  return (
    <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-6">
      <div className="flex items-center justify-between mb-6">
        <h3 className="text-lg font-semibold text-slate-800">Analysis History</h3>
        <button
          onClick={onClearHistory}
          className="flex items-center gap-2 px-3 py-1.5 text-sm text-red-600 hover:text-red-700 hover:bg-red-50 rounded-lg transition-colors"
        >
          <Trash2 className="w-4 h-4" />
          Clear History
        </button>
      </div>

      {history.length === 0 ? (
        <p className="text-slate-500 text-center py-8">No analysis history yet.</p>
      ) : (
        <div className="space-y-4 max-h-96 overflow-y-auto">
          {history.map((item, index) => (
            <div key={index} className="border border-gray-200 rounded-lg p-4">
              <div className="flex items-start justify-between mb-2">
                <h4 className="font-medium text-slate-800">{item.productName}</h4>
                <div className="flex items-center gap-1 text-xs text-slate-500">
                  <Clock className="w-3 h-3" />
                  {new Date(item.timestamp).toLocaleString()}
                </div>
              </div>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-2 text-sm">
                <div>
                  <span className="text-slate-500">Your Price:</span>
                  <span className="font-medium ml-1">₹{item.yourPrice}</span>
                </div>
                <div>
                  <span className="text-slate-500">Competitor:</span>
                  <span className="font-medium ml-1">₹{item.competitorPrice}</span>
                </div>
                <div>
                  <span className="text-slate-500">Demand:</span>
                  <span className="font-medium ml-1">{item.demand}</span>
                </div>
                <div>
                  <span className="text-slate-500">Confidence:</span>
                  <span className="font-medium ml-1">{item.confidence}%</span>
                </div>
              </div>
              <div className="mt-2">
                <span className="text-slate-500">Decision:</span>
                <span className="font-medium ml-1">{item.recommendedAction}</span>
              </div>
              <p className="text-sm text-slate-600 mt-1">{item.summary}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}