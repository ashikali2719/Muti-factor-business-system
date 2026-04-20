import java.util.List;

/**
 * Quick-start example showing how to use DemandService standalone.
 * Useful for testing before integrating into the full servlet.
 * 
 * Compile & run:
 *   javac -cp . DemandServiceTest.java MarketData.java CSVDataLoader.java ProductMatcher.java DemandService.java
 *   java -cp . DemandServiceTest
 */
public class DemandServiceTest {
    public static void main(String[] args) {
        // Step 1: Configure path to your Kaggle CSV
        String csvPath = "path/to/your/dataset.csv";  // UPDATE THIS PATH
        
        System.out.println("=== Demand Service Test ===\n");
        
        // Step 2: Create DemandService instance
        DemandService demandService = new DemandService(csvPath);
        
        // Step 3: Load and display dataset stats
        System.out.println(demandService.getDatasetStats());
        System.out.println();
        
        // Step 4: Estimate demand for various products
        testDemandEstimation(demandService, "iPhone 13 Pro Max");
        testDemandEstimation(demandService, "Samsung TV");
        testDemandEstimation(demandService, "Cotton Saree");
        testDemandEstimation(demandService, "USB Cable");
        testDemandEstimation(demandService, "Random Product XYZ");
        
        System.out.println("\n=== Manual Demand Calculation ===\n");
        
        // Step 5: Test custom demand calculation
        testCustomDemand(demandService);
        
        System.out.println("\n=== Test Complete ===");
    }
    
    private static void testDemandEstimation(DemandService service, String productName) {
        System.out.println("Testing: " + productName);
        int demand = service.estimateDemand(productName);
        
        if (demand < 0) {
            System.out.println("  → Not found in dataset (demand = -1)");
        } else if (demand >= 80) {
            System.out.println("  → Demand: " + demand + " (VERY HIGH - Buy immediately)");
        } else if (demand >= 60) {
            System.out.println("  → Demand: " + demand + " (HIGH - Good buying opportunity)");
        } else if (demand >= 40) {
            System.out.println("  → Demand: " + demand + " (MEDIUM - Monitor trends)");
        } else if (demand >= 20) {
            System.out.println("  → Demand: " + demand + " (LOW - Wait for better signals)");
        } else {
            System.out.println("  → Demand: " + demand + " (VERY LOW - Avoid stock purchase)");
        }
        System.out.println();
    }
    
    private static void testCustomDemand(DemandService service) {
        System.out.println("Scenario 1: Popular product with high engagement");
        int demand1 = service.calculateCustomDemand(
            5000000,   // sales volume (5M)
            15000,     // quantity sold
            5000,      // review count
            4.5,       // rating
            40000      // popularity score
        );
        System.out.println("  → Demand: " + demand1 + "/100\n");
        
        System.out.println("Scenario 2: Low-value bulk product");
        int demand2 = service.calculateCustomDemand(
            200000,    // sales volume (200K)
            20000,     // quantity sold (high quantity)
            500,       // review count (low engagement)
            3.8,       // rating
            5000       // popularity score
        );
        System.out.println("  → Demand: " + demand2 + "/100\n");
        
        System.out.println("Scenario 3: Niche premium product");
        int demand3 = service.calculateCustomDemand(
            3000000,   // sales volume (3M)
            2000,      // quantity sold (low quantity)
            3000,      // review count
            4.7,       // rating (high quality)
            15000      // popularity score
        );
        System.out.println("  → Demand: " + demand3 + "/100\n");
        
        System.out.println("Scenario 4: Dead product");
        int demand4 = service.calculateCustomDemand(
            50000,     // sales volume
            100,       // quantity sold
            50,        // review count
            2.5,       // rating (poor)
            500        // popularity score
        );
        System.out.println("  → Demand: " + demand4 + "/100\n");
    }
}

/**
 * INTEGRATION: How to use DemandService in your actual servlet
 * 
 * In DecisionServlet:
 * 
 *   private DemandService demandService;
 *   
 *   public void init() throws ServletException {
 *       String csvPath = "C:\\path\\to\\your\\dataset.csv";
 *       demandService = new DemandService(csvPath);
 *       System.out.println(demandService.getDatasetStats());
 *   }
 *   
 *   protected void doPost(HttpServletRequest request, HttpServletResponse response) 
 *       throws ServletException, IOException {
 *       
 *       String productName = request.getParameter("productName");
 *       
 *       // NEW: Auto-estimate demand from dataset
 *       int marketDemand = demandService.estimateDemand(productName);
 *       if (marketDemand < 0) {
 *           marketDemand = 50; // Fallback neutral demand
 *       }
 *       
 *       // ... rest of existing code ...
 *       Product product = new Product(productName, stock, sales, marketDemand, price);
 *   }
 */
