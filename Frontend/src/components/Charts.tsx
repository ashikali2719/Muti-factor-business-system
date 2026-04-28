import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { AnalysisResult } from '../types';

interface ChartsProps {
  result: AnalysisResult;
}

export default function Charts({ result }: ChartsProps) {
  const priceData = [
    { name: 'Your Price', value: result.yourPrice },
    { name: 'Competitor Price', value: result.competitorPrice },
  ];

  const salesDemandData = [
    { name: 'Sales', value: result.sales },
    { name: 'Demand', value: result.demand },
  ];

  return (
    <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-6 space-y-6">
      <h3 className="text-lg font-semibold text-slate-800">Analysis Charts</h3>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <h4 className="text-sm font-medium text-slate-600 mb-4">Price Comparison</h4>
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={priceData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip formatter={(value) => [`₹${value}`, '']} />
              <Bar dataKey="value" fill="#3b82f6" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div>
          <h4 className="text-sm font-medium text-slate-600 mb-4">Sales vs Demand</h4>
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={salesDemandData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="value" fill="#10b981" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
}