import { ProductInput, AnalysisResult, RecommendedAction, DecisionLevel } from '../types';

function hashString(str: string): number {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    const char = str.charCodeAt(i);
    hash = (hash << 5) - hash + char;
    hash = hash & hash;
  }
  return Math.abs(hash);
}

function simulateCompetitorPrice(productName: string, productPrice: number): number {
  const hash = hashString(productName.toLowerCase().trim());
  const multiplier = 1.15 + (hash % 80) / 200;
  return Math.round(productPrice * multiplier * 10) / 10;
}

export function analyzeProduct(input: ProductInput): AnalysisResult {
  const stock = parseFloat(input.stockQuantity) || 0;
  const sales = parseFloat(input.salesCount) || 0;
  const demand = parseFloat(input.marketDemand) || 0;
  const price = parseFloat(input.productPrice) || 0;
  const productName = input.productName.trim() || 'Unknown Product';

  const competitorPrice = simulateCompetitorPrice(productName, price);
  const stockRatio = stock > 0 ? sales / stock : 0;
  const priceRatio = competitorPrice > 0 ? price / competitorPrice : 1;

  let action: RecommendedAction;
  let confidence: number;
  let summary: string;
  let decisionLevel: DecisionLevel;
  let insights: string[];

  if (demand >= 70 && stockRatio >= 0.75) {
    action = 'BUY STOCK NOW';
    confidence = Math.min(95, 60 + demand * 0.35);
    decisionLevel = 'HIGH';
    summary = 'Strong demand and healthy sales velocity signal it is the right time to replenish stock immediately.';
    insights = [
      `Market demand is high at ${demand}/100, indicating strong consumer interest.`,
      `Sales-to-stock ratio of ${(stockRatio * 100).toFixed(0)}% suggests inventory will deplete soon.`,
      price < competitorPrice
        ? `Your price (${price.toLocaleString()}) is competitive against the market (${competitorPrice.toLocaleString()}).`
        : 'Monitor competitor pricing to maintain market position.',
    ];
  } else if (priceRatio > 1.15) {
    action = 'REDUCE PRICE';
    confidence = Math.min(88, 50 + Math.round((priceRatio - 1) * 220));
    decisionLevel = priceRatio > 1.3 ? 'HIGH' : 'MEDIUM';
    summary = 'Your price is notably higher than the competitor benchmark. A price reduction can restore market competitiveness.';
    insights = [
      `Your price is ${Math.round((priceRatio - 1) * 100)}% above the competitor benchmark.`,
      'Customers may be choosing alternatives due to the pricing gap.',
      demand < 50
        ? 'Low demand may be a direct result of higher pricing in this segment.'
        : 'Reducing price could convert moderate demand into strong sales.',
    ];
  } else if (priceRatio < 0.85 && demand >= 60) {
    action = 'INCREASE PRICE';
    confidence = Math.min(82, 42 + demand * 0.38);
    decisionLevel = 'MEDIUM';
    summary = 'Your price is below the market average while demand remains healthy. Optimizing margins through a price increase is recommended.';
    insights = [
      `Your price is ${Math.round((1 - priceRatio) * 100)}% below the competitor benchmark.`,
      'Healthy demand indicates customers perceive strong value in your product.',
      'A gradual price increase may improve revenue without significant customer loss.',
    ];

  } else if (stockRatio > 1.5 || (stock > 60 && demand < 35)) {
    action = 'GIVE DISCOUNT';
    confidence = Math.min(78, 35 + Math.round(stockRatio * 18));
    decisionLevel = 'MEDIUM';
    summary = 'Excess inventory relative to current sales rate. A strategic discount can accelerate stock movement.';
    insights = [
      `Inventory level (${stock} units) is high relative to current sales pace.`,
      'Promotional discounts can attract price-sensitive customers and clear stock.',
      demand < 40
        ? 'Low market demand reinforces the need for a promotional strategy.'
        : 'Discounting can convert moderate interest into active purchases.',
    ];
  } else {
    const buyUrgency = (demand / 100) * 0.45 + Math.min(stockRatio, 1) * 0.35 + Math.max(0, 1 - priceRatio) * 0.2;
    confidence = Math.round(Math.max(10, Math.min(40, buyUrgency * 60 + 5)));
    decisionLevel = confidence >= 30 ? 'MEDIUM' : 'LOW';
    action = 'DO NOT BUY STOCK NOW';
    summary = 'Stock is not in a critical level or demand is not high enough to justify buying more at this time.';
    insights = [
      demand < 50
        ? `Market demand is currently at ${demand}/100 — not strong enough to justify stock replenishment.`
        : 'Demand is moderate; continue monitoring for upward trends before restocking.',
      price < competitorPrice
        ? `Competitor price (${competitorPrice.toLocaleString()}) is higher than yours, giving you a pricing advantage.`
        : `Your price (${price.toLocaleString()}) is above the market average; consider reviewing pricing.`,
      `Current decision confidence is ${decisionLevel.toLowerCase()} — additional data may improve accuracy.`,
    ];
  }

  return {
    productName,
    competitorPrice,
    confidence: Math.round(confidence),
    decisionLevel,
    recommendedAction: action,
    summary,
    insights,
    yourPrice: price,
  };
}

export function getActionStyle(action: RecommendedAction) {
  switch (action) {
    case 'BUY STOCK NOW':
      return {
        bg: 'bg-emerald-50',
        border: 'border-emerald-200',
        badge: 'bg-emerald-500',
        text: 'text-emerald-700',
        bar: 'bg-emerald-500',
        ring: 'ring-emerald-300',
      };
    case 'GIVE DISCOUNT':
      return {
        bg: 'bg-orange-50',
        border: 'border-orange-200',
        badge: 'bg-orange-500',
        text: 'text-orange-700',
        bar: 'bg-orange-500',
        ring: 'ring-orange-300',
      };
    case 'INCREASE PRICE':
      return {
        bg: 'bg-purple-50',
        border: 'border-purple-200',
        badge: 'bg-purple-500',
        text: 'text-purple-700',
        bar: 'bg-purple-500',
        ring: 'ring-purple-300',
      };
    case 'REDUCE PRICE':
      return {
        bg: 'bg-red-50',
        border: 'border-red-200',
        badge: 'bg-red-500',
        text: 'text-red-700',
        bar: 'bg-red-500',
        ring: 'ring-red-300',
      };
    case 'DO NOT BUY STOCK NOW':
    default:
      return {
        bg: 'bg-blue-50',
        border: 'border-blue-200',
        badge: 'bg-blue-500',
        text: 'text-blue-700',
        bar: 'bg-blue-500',
        ring: 'ring-blue-300',
      };
  }
}

export function getLevelStyle(level: DecisionLevel) {
  switch (level) {
    case 'HIGH':
      return { dot: 'bg-red-500', text: 'text-red-600', bg: 'bg-red-50 border-red-200' };
    case 'MEDIUM':
      return { dot: 'bg-amber-500', text: 'text-amber-600', bg: 'bg-amber-50 border-amber-200' };
    case 'LOW':
    default:
      return { dot: 'bg-slate-400', text: 'text-slate-500', bg: 'bg-slate-50 border-slate-200' };
  }
}
