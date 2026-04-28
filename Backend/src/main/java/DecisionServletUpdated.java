import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;

/**
 * Updated DecisionServlet that uses real market demand estimation
 * instead of manual user input.
 */
public class DecisionServletUpdated extends HttpServlet {
    private DemandService demandService;

    @Override
    public void init() throws ServletException {
        // Initialize DemandService with path to your Kaggle CSV
        // Path is relative to your working directory when running mvn jetty:run
        String csvPath = "path/to/your/dataset.csv";
        demandService = new DemandService(csvPath);
        System.out.println("DecisionServlet initialized: " + demandService.getDatasetStats());
    }

    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(200);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Add CORS headers
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        try {
            String productName = request.getParameter("productName");
            int stock = Integer.parseInt(request.getParameter("stock") != null ? request.getParameter("stock") : "0");
            int sales = Integer.parseInt(request.getParameter("sales") != null ? request.getParameter("sales") : "0");
            // CHANGE: demand is now estimated, not user-provided
            int marketDemand = demandService.estimateDemand(productName);
            double price = Double.parseDouble(request.getParameter("price") != null ? request.getParameter("price") : "0");

            // If demand estimation failed, use fallback value
            if (marketDemand < 0) {
                marketDemand = 50; // Default neutral demand
                System.out.println("Using fallback demand for: " + productName);
            }

            // Get competitor price from scraping service
            MarketPriceService priceService = new MarketPriceService();
            double competitorPrice = priceService.getFinalPrice(productName);

            // Create product with real market demand
            Product product = new Product(productName, stock, sales, marketDemand, price);

            // Process decision
            DecisionEngine engine = new DecisionEngine();
            DecisionResult result = engine.process(product, competitorPrice);

            // Send JSON response
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            
            // Construct JSON with all required fields
            String json = String.format(
                "{\"productName\":\"%s\",\"competitorPrice\":%.2f,\"confidence\":%d,\"decisionLevel\":\"%s\"," +
                "\"decision\":\"%s\",\"recommendedAction\":\"%s\",\"summary\":\"%s\",\"marketDemand\":%d}",
                escapeJSON(productName),
                competitorPrice,
                result.getConfidence(),
                result.getDecisionLevel(),
                escapeJSON(result.getDecision()),
                escapeJSON(result.getDecision()),
                escapeJSON(result.getSummary()),
                marketDemand
            );
            
            out.println(json);
            out.flush();

        } catch (NumberFormatException e) {
            response.setStatus(400);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println("{\"error\":\"Invalid input: " + escapeJSON(e.getMessage()) + "\"}");
        } catch (Exception e) {
            response.setStatus(500);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println("{\"error\":\"Server error: " + escapeJSON(e.getMessage()) + "\"}");
        }
    }

    /**
     * Escapes strings for safe JSON output
     */
    private String escapeJSON(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
