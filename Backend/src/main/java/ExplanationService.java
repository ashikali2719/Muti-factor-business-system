/**
 * ExplanationService generates human-readable explanations for decisions.
 */
public class ExplanationService {

    public String generateExplanation(String decision, double price, double competitorPrice, int stock, int sales, int demand) {
        StringBuilder exp = new StringBuilder();
        exp.append("Recommend ").append(decision.toLowerCase()).append(" because ");

        boolean first = true;
        if (competitorPrice > 0) {
            double diff = price - competitorPrice;
            if (Math.abs(diff) > price * 0.05) { // significant difference
                if (diff > 0) {
                    exp.append("competitor price is lower");
                } else {
                    exp.append("competitor price is higher");
                }
                first = false;
            }
        }

        if (stock < sales * 0.5) {
            if (!first) exp.append(", ");
            exp.append("stock is low");
            first = false;
        } else if (stock > sales * 2) {
            if (!first) exp.append(", ");
            exp.append("stock is high");
            first = false;
        }

        if (demand > 70) {
            if (!first) exp.append(", ");
            exp.append("demand is high");
            first = false;
        } else if (demand < 45) {
            if (!first) exp.append(", ");
            exp.append("demand is low");
            first = false;
        } else {
            if (!first) exp.append(", ");
            exp.append("demand is moderate");
            first = false;
        }

        if (sales > 0) {
            if (!first) exp.append(", ");
            exp.append("sales are ").append(sales > 10 ? "increasing" : "decreasing");
        }

        exp.append(".");
        return exp.toString();
    }
}