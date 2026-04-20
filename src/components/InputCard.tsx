import { Search, Info } from 'lucide-react';
import { ProductInput } from '../types';

interface InputCardProps {
  input: ProductInput;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onAnalyze: () => void;
  loading: boolean;
}

interface FieldProps {
  label: string;
  id: string;
  type?: string;
  placeholder: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  min?: string;
  max?: string;
  hint?: string;
}

function Field({ label, id, type = 'text', placeholder, value, onChange, min, max, hint }: FieldProps) {
  return (
    <div>
      <label htmlFor={id} className="block text-sm font-medium text-slate-700 mb-1.5">
        {label}
      </label>
      <input
        id={id}
        name={id}
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        min={min}
        max={max}
        className="w-full px-4 py-2.5 rounded-xl border border-gray-200 bg-slate-50 text-slate-800 placeholder-slate-400 text-sm focus:outline-none focus:ring-2 focus:ring-sky-400 focus:border-transparent transition-all"
      />
      {hint && (
        <p className="mt-1 text-xs text-slate-400 flex items-center gap-1">
          <Info className="w-3 h-3" />
          {hint}
        </p>
      )}
    </div>
  );
}

export default function InputCard({ input, onChange, onAnalyze, loading }: InputCardProps) {
  const isValid =
    input.productName.trim() &&
    input.stockQuantity &&
    input.salesCount &&
    input.productPrice;

  return (
    <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
      <div className="mb-5">
        <h2 className="text-base font-semibold text-slate-800">Product Analysis Input</h2>
        <p className="text-sm text-slate-400 mt-0.5">
          Enter product details to generate a decision recommendation
        </p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-5">
        <div className="sm:col-span-2">
          <Field
            label="Product Name"
            id="productName"
            placeholder="e.g. Saree, Laptop, Rice"
            value={input.productName}
            onChange={onChange}
          />
        </div>
        <Field
          label="Stock Quantity"
          id="stockQuantity"
          type="number"
          placeholder="e.g. 50"
          value={input.stockQuantity}
          onChange={onChange}
          min="0"
          hint="Current units in inventory"
        />
        <Field
          label="Sales Count"
          id="salesCount"
          type="number"
          placeholder="e.g. 40"
          value={input.salesCount}
          onChange={onChange}
          min="0"
          hint="Units sold in last period"
        />
        <Field
          label="Your Product Price (₹)"
          id="productPrice"
          type="number"
          placeholder="e.g. 1000"
          value={input.productPrice}
          onChange={onChange}
          min="0"
          hint="Your current selling price"
        />
      </div>

      <div className="pt-1">
        <p className="text-xs text-slate-400 mb-3 flex items-center gap-1.5">
          <Info className="w-3.5 h-3.5 shrink-0" />
          Live competitor price fetched from multiple e-commerce sources
        </p>
        <button
          onClick={onAnalyze}
          disabled={!isValid || loading}
          className="w-full flex items-center justify-center gap-2 py-3 px-6 rounded-xl bg-sky-600 hover:bg-sky-700 active:bg-sky-800 disabled:bg-slate-200 disabled:text-slate-400 disabled:cursor-not-allowed text-white font-semibold text-sm transition-all shadow-sm hover:shadow-md"
        >
          {loading ? (
            <>
              <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              Analyzing...
            </>
          ) : (
            <>
              <Search className="w-4 h-4" />
              Analyze Product
            </>
          )}
        </button>
      </div>
    </div>
  );
}
