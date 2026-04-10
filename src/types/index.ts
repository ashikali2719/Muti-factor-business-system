export interface ProductInput {
  productName: string;
  stockQuantity: string;
  salesCount: string;
  marketDemand: string;
  productPrice: string;
}

export type RecommendedAction =
  | 'BUY STOCK NOW'
  | 'GIVE DISCOUNT'
  | 'INCREASE PRICE'
  | 'REDUCE PRICE'
  | 'DO NOT BUY STOCK NOW';

export type DecisionLevel = 'HIGH' | 'MEDIUM' | 'LOW';

export interface AnalysisResult {
  productName: string;
  competitorPrice: number;
  confidence: number;
  decisionLevel: DecisionLevel;
  recommendedAction: RecommendedAction;
  summary: string;
  insights: string[];
  yourPrice: number;
}
