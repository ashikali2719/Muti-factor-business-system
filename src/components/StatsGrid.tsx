import { Package, TrendingUp, Activity, DollarSign } from 'lucide-react';
import { AnalysisResult } from '../types';

interface StatsGridProps {
  result: AnalysisResult | null;
  defaultStock: string;
  defaultSales: string;
  defaultDemand: string;
}

interface StatCardProps {
  icon: React.ReactNode;
  label: string;
  value: string;
  color: string;
  iconBg: string;
}

function StatCard({ icon, label, value, color, iconBg }: StatCardProps) {
  return (
    <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 flex items-center gap-4">
      <div className={`flex items-center justify-center w-11 h-11 rounded-xl ${iconBg}`}>
        {icon}
      </div>
      <div>
        <p className="text-xs font-medium text-slate-400 uppercase tracking-wide">{label}</p>
        <p className={`text-xl font-bold ${color} mt-0.5`}>{value}</p>
      </div>
    </div>
  );
}

export default function StatsGrid({ result, defaultStock, defaultSales, defaultDemand }: StatsGridProps) {
  const competitorPrice = result ? `₹${result.competitorPrice.toLocaleString()}` : '—';

  return (
    <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
      <StatCard
        icon={<Package className="w-5 h-5 text-sky-600" />}
        label="Stock Units"
        value={defaultStock || '—'}
        color="text-slate-800"
        iconBg="bg-sky-50"
      />
      <StatCard
        icon={<TrendingUp className="w-5 h-5 text-emerald-600" />}
        label="Sales Count"
        value={defaultSales || '—'}
        color="text-slate-800"
        iconBg="bg-emerald-50"
      />
      <StatCard
        icon={<Activity className="w-5 h-5 text-amber-600" />}
        label="Market Demand"
        value={defaultDemand ? `${defaultDemand}/100` : '—'}
        color="text-slate-800"
        iconBg="bg-amber-50"
      />
      <StatCard
        icon={<DollarSign className="w-5 h-5 text-rose-600" />}
        label="Competitor Price"
        value={competitorPrice}
        color="text-slate-800"
        iconBg="bg-rose-50"
      />
    </div>
  );
}
