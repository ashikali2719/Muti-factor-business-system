import java.util.ArrayList;
import java.util.List;

public class DecisionEngine {
    private ExplanationService expService = new ExplanationService();
    public DecisionResult process(Product product, double competitorPrice) {
        int stock = Math.max(0, product.getStock());
        int sales = Math.max(0, product.getSales());
        double price = Math.max(0.0, product.getPrice());

        // Dynamic demand calculation based on sales velocity and price competitiveness
        int demand;
        if (stock == 0) {
            demand = sales > 0 ? 100 : 0;  // If no stock but sales, high demand
        } else {
            demand = (int) Math.min(100, (sales / (double) stock) * 100);  // Sales-to-stock ratio
        }
        // Boost demand if your price is lower than competitor (more competitive)
        if (competitorPrice > 0 && price < competitorPrice) {
            demand = Math.min(100, demand + 10);
        }

        double priceRatio = competitorPrice > 0 ? price / competitorPrice : 1.0;
        boolean highDemand = demand >= 70;
        boolean lowDemand = demand < 45;
        boolean stockLow = stock < Math.max(5, sales / 2);
        boolean stockHigh = stock > sales * 1.5;
        boolean overpriced = competitorPrice > 0 && priceRatio > 1.12;
        boolean underpriced = competitorPrice > 0 && priceRatio < 0.88;

        String decision;
        String decisionLevel;
        int confidence;
        List<String> insights = new ArrayList<>();

        if (highDemand && stockLow) {
            decision = "BUY STOCK NOW";
            decisionLevel = "HIGH";
            confidence = 88;
            insights.add("Demand is strong while inventory is low.");
            insights.add("Replenishing stock will help prevent lost sales.");
        } else if (highDemand && stockHigh) {
            if (overpriced) {
                decision = "REDUCE PRICE";
                decisionLevel = "MEDIUM";
                confidence = 72;
                insights.add("High demand is present, but your price is above market.");
                insights.add("A price reduction can increase velocity without hurting demand.");
            } else {
                decision = "BUY STOCK NOW";
                decisionLevel = "MEDIUM";
                confidence = 75;
                insights.add("Demand remains healthy and there is still room to grow sales.");
                insights.add("Monitor inventory while preparing to reorder soon.");
            }
        } else if (lowDemand && stockHigh) {
            decision = "REDUCE PRICE";
            decisionLevel = "HIGH";
            confidence = 80;
            insights.add("Inventory is high, but demand is low.");
            insights.add("A price reduction is the safest strategy to move stock.");
        } else if (overpriced) {
            decision = "REDUCE PRICE";
            decisionLevel = "MEDIUM";
            confidence = 68;
            insights.add("Your price is higher than the competitor benchmark.");
            insights.add("Adjust pricing before demand softens further.");
        } else if (underpriced && demand >= 55) {
            decision = "INCREASE PRICE";
            decisionLevel = "MEDIUM";
            confidence = 62;
            insights.add("Price is below market while demand is healthy.");
            insights.add("Raising price slightly can improve margins without hurting demand.");
        } else {
            decision = "DO NOT BUY STOCK NOW";
            decisionLevel = "LOW";
            confidence = 52;
            insights.add("Demand and inventory conditions do not justify new stock purchases.");
            if (competitorPrice > 0) {
                insights.add("Keep an eye on competitor pricing before revisiting this decision.");
            }
        }

        String summary = buildSummary(stock, sales, demand, price, competitorPrice, decision);
        String explanation = expService.generateExplanation(decision, price, competitorPrice, stock, sales, demand);
        return new DecisionResult(confidence, decisionLevel, decision, summary, insights, explanation);
    }

    private String buildSummary(int stock, int sales, int demand, double price, double competitorPrice, String decision) {
        StringBuilder builder = new StringBuilder();
        builder.append("Decision: ").append(decision).append(". ");
        builder.append("Demand score is ").append(demand).append("/100.");

        if (competitorPrice > 0) {
            builder.append(" Competitor price is ₹").append(String.format("%.2f", competitorPrice)).append(".");
        }

        if (stock < Math.max(5, sales / 2)) {
            builder.append(" Inventory is low relative to recent sales.");
        } else if (stock > sales * 1.5) {
            builder.append(" Inventory is high compared to recent sales.");
        }

        if (competitorPrice > 0) {
            if (price > competitorPrice) {
                builder.append(" Your price is above competitor levels.");
            } else if (price < competitorPrice) {
                builder.append(" Your price is below competitor levels.");
            }
        }

        return builder.toString();
    }
}