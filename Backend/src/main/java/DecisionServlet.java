import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.time.Instant;

public class DecisionServlet extends HttpServlet {

    private static final int FALLBACK_DEMAND = 50;

    private MarketPriceService marketPriceService;
    private DecisionEngine engine;

    @Override
    public void init() throws ServletException {
        super.init();
        marketPriceService = new MarketPriceService();
        engine = new DecisionEngine();
        System.out.println("DecisionServlet initialized successfully.");
    }

    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(200);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");

        try {
            String productName = request.getParameter("productName");

            if (productName == null || productName.trim().isEmpty()) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "productName is required");
                return;
            }

            int stock = parseIntOrDefault(request.getParameter("stock"), 0);
            int sales = parseIntOrDefault(request.getParameter("sales"), 0);
            double price = parseDoubleOrDefault(request.getParameter("price"), 0.0);

            double competitorPrice;

            try {
                competitorPrice = marketPriceService.getFinalPrice(productName);
            } catch (Exception e) {
                System.out.println("Scraping failed: " + e.getMessage());
                competitorPrice = 0.0;
            }

            PythonMLService mlService = new PythonMLService();

            double predictedDemand = mlService.predictDemand(
                    stock,
                    sales,
                    sales,
                    price,
                    0,
                    competitorPrice
            );

            int demand = (int) Math.round(predictedDemand);

            if (demand < 0) {
                demand = FALLBACK_DEMAND;
            }

            demand = Math.max(0, Math.min(100, demand));

            Product product = new Product(productName, stock, sales, demand, price);
            DecisionResult result = engine.process(product, competitorPrice);

            String timestamp = Instant.now().toString();

            PrintWriter out = response.getWriter();

            out.println(buildJsonResponse(
                    productName,
                    stock,
                    sales,
                    demand,
                    price,
                    competitorPrice,
                    result,
                    timestamp
            ));

        } catch (Exception e) {
            response.setStatus(500);
            PrintWriter out = response.getWriter();
            out.println("{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void sendJsonError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        PrintWriter out = response.getWriter();
        out.println("{\"error\":\"" + escapeJson(message) + "\"}");
    }

    private int parseIntOrDefault(String value, int fallback) {
        try {
            if (value == null || value.trim().isEmpty()) return fallback;
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private double parseDoubleOrDefault(String value, double fallback) {
        try {
            if (value == null || value.trim().isEmpty()) return fallback;
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private String buildJsonResponse(String productName,
                                     int stock,
                                     int sales,
                                     int demand,
                                     double yourPrice,
                                     double competitorPrice,
                                     DecisionResult result,
                                     String timestamp) {

        StringBuilder builder = new StringBuilder();

        builder.append("{");
        builder.append("\"productName\":\"").append(escapeJson(productName)).append("\",");
        builder.append("\"stock\":").append(stock).append(",");
        builder.append("\"sales\":").append(sales).append(",");
        builder.append("\"demand\":").append(demand).append(",");
        builder.append("\"yourPrice\":").append(String.format("%.2f", yourPrice)).append(",");
        builder.append("\"competitorPrice\":").append(String.format("%.2f", competitorPrice)).append(",");
        builder.append("\"confidence\":").append(result.getConfidence()).append(",");
        builder.append("\"decisionLevel\":\"").append(escapeJson(result.getDecisionLevel())).append("\",");
        builder.append("\"decision\":\"").append(escapeJson(result.getDecision())).append("\",");
        builder.append("\"recommendedAction\":\"").append(escapeJson(result.getDecision())).append("\",");
        builder.append("\"summary\":\"").append(escapeJson(result.getSummary())).append("\",");
        builder.append("\"explanation\":\"").append(escapeJson(result.getExplanation())).append("\",");
        builder.append("\"timestamp\":\"").append(escapeJson(timestamp)).append("\",");
        builder.append("\"insights\":[");

        boolean first = true;

        for (String insight : result.getInsights()) {
            if (!first) builder.append(",");
            builder.append("\"").append(escapeJson(insight)).append("\"");
            first = false;
        }

        builder.append("]");
        builder.append("}");

        return builder.toString();
    }

    private String escapeJson(String value) {
        if (value == null) return "";

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}