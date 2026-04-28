interface ConfidenceBarProps {
  confidence: number;
  barColor: string;
}

export default function ConfidenceBar({ confidence, barColor }: ConfidenceBarProps) {
  const getLabel = (v: number) => {
    if (v >= 70) return 'High';
    if (v >= 40) return 'Moderate';
    return 'Low';
  };

  const getTextColor = (v: number) => {
    if (v >= 70) return 'text-emerald-600';
    if (v >= 40) return 'text-amber-600';
    return 'text-slate-500';
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-2">
        <span className="text-sm font-medium text-slate-600">Analysis Confidence</span>
        <div className="flex items-center gap-2">
          <span className={`text-xs font-medium ${getTextColor(confidence)}`}>
            {getLabel(confidence)}
          </span>
          <span className="text-lg font-bold text-slate-800">{confidence}%</span>
        </div>
      </div>
      <div className="w-full bg-slate-100 rounded-full h-3 overflow-hidden">
        <div
          className={`h-3 rounded-full transition-all duration-700 ease-out ${barColor}`}
          style={{ width: `${confidence}%` }}
        />
      </div>
      <div className="flex justify-between mt-1.5 text-xs text-slate-400">
        <span>0%</span>
        <span>50%</span>
        <span>100%</span>
      </div>
    </div>
  );
}
