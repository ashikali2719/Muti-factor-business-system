import { useState, useEffect } from 'react';
import Header from './components/Header';
import StatsGrid from './components/StatsGrid';
import InputCard from './components/InputCard';
import ResultCard from './components/ResultCard';
import Charts from './components/Charts';
import HistoryDashboard from './components/HistoryDashboard';
import Footer from './components/Footer';
import { ProductInput, AnalysisResult } from './types';

const DEFAULT_INPUT: ProductInput = {
  productName: '',
  stockQuantity: '',
  salesCount: '',
  productPrice: '',
};

export default function App() {
  const [formData, setFormData] = useState<ProductInput>(DEFAULT_INPUT);
  const [result, setResult] = useState<AnalysisResult | null>(null);
  const [loading, setLoading] = useState(false);
  const [hasAnalyzed, setHasAnalyzed] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [history, setHistory] = useState<AnalysisResult[]>([]);

  useEffect(() => {
    const storedHistory = localStorage.getItem('analysisHistory');
    if (storedHistory) {
      try {
        setHistory(JSON.parse(storedHistory));
      } catch (e) {
        console.error('Failed to parse history from localStorage', e);
      }
    }
  }, []);

  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  }

  function handleClearHistory() {
    setHistory([]);
    localStorage.removeItem('analysisHistory');
  }

  async function handleAnalyze() {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch('http://localhost:8090/decision', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          productName: formData.productName,
          stock: formData.stockQuantity,
          sales: formData.salesCount,
          price: formData.productPrice,
        }),
      });

      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || 'Backend request failed');
      }

      const data = await response.json();

      const newResult: AnalysisResult = {
        productName: data.productName,
        stock: data.stock,
        sales: data.sales,
        demand: data.demand,
        yourPrice: data.yourPrice,
        competitorPrice: data.competitorPrice,
        confidence: data.confidence,
        decisionLevel: data.decisionLevel as any,
        recommendedAction: (data.recommendedAction || data.decision || 'DO NOT BUY STOCK NOW') as any,
        summary: data.summary,
        insights: Array.isArray(data.insights) ? data.insights : [],
        timestamp: data.timestamp,
      };

      setResult(newResult);
      setHasAnalyzed(true);

      // Add to history
      setHistory(prev => {
        const updated = [newResult, ...prev].slice(0, 10); // Keep last 10
        localStorage.setItem('analysisHistory', JSON.stringify(updated));
        return updated;
      });

      setTimeout(() => {
        document.getElementById('result-section')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }, 100);
    } catch (err) {
      console.error('Error:', err);
      setError('Unable to fetch analysis. Please try again.');
      setHasAnalyzed(false);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col">
      <Header />

      <main className="flex-1 max-w-7xl mx-auto w-full px-4 sm:px-6 lg:px-8 py-8 space-y-6">
        <StatsGrid
          result={result}
          defaultStock={formData.stockQuantity}
          defaultSales={formData.salesCount}
        />

        <div className="grid grid-cols-1 lg:grid-cols-5 gap-6">
          <div className="lg:col-span-2">
            <InputCard
              input={formData}
              onChange={handleChange}
              onAnalyze={handleAnalyze}
              loading={loading}
            />
          </div>

          <div className="lg:col-span-3">
          {error ? (
            <div className="bg-red-50 text-red-700 border border-red-200 rounded-2xl p-5">
              <p className="font-semibold">Analysis failed</p>
              <p className="text-sm mt-2">{error}</p>
            </div>
          ) : !hasAnalyzed ? (
            <div className="bg-white rounded-2xl shadow-sm border border-dashed border-gray-200 h-full min-h-[280px] flex flex-col items-center justify-center text-center p-8">
              <div className="w-14 h-14 rounded-2xl bg-slate-100 flex items-center justify-center mb-4">
                <svg className="w-7 h-7 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M9 17.25v1.007a3 3 0 01-.879 2.122L7.5 21h9l-.621-.621A3 3 0 0115 18.257V17.25m6-12V15a2.25 2.25 0 01-2.25 2.25H5.25A2.25 2.25 0 013 15V5.25m18 0A2.25 2.25 0 0018.75 3H5.25A2.25 2.25 0 003 5.25m18 0H3" />
                </svg>
              </div>
              <h3 className="text-base font-semibold text-slate-700 mb-2">No Analysis Yet</h3>
              <p className="text-sm text-slate-400 max-w-xs leading-relaxed">
                Fill in the product details on the left and click "Analyze Product" to generate your business decision recommendation.
              </p>
            </div>
          ) : result ? (
            <div id="result-section">
              <ResultCard result={result} />
            </div>
          ) : null}
        </div>        </div>

        {hasAnalyzed && result && (
          <>
            <Charts result={result} />
            <HistoryDashboard history={history} onClearHistory={handleClearHistory} />
          </>
        )}

        {hasAnalyzed && result && (
          <div className="text-center">
            <button
              onClick={() => {
                setResult(null);
                setHasAnalyzed(false);
                setFormData(DEFAULT_INPUT);
              }}
              className="text-sm text-slate-500 hover:text-slate-700 underline underline-offset-4 transition-colors"
            >
              Reset and analyze another product
            </button>
          </div>
        )}
      </main>

      <Footer />
    </div>
  );
}
