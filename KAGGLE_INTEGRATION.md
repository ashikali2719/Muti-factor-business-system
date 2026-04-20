KAGGLE DATASET INTEGRATION GUIDE
================================

This guide explains how to integrate real market demand estimation into your Java backend.

---
FILES CREATED
---

1. MarketData.java
   - Data model representing a single record from the Kaggle CSV
   - Holds: productName, category, salesVolume, quantitySold, avgRating, reviewCount, price, popularityScore, stockAvailable

2. CSVDataLoader.java
   - Parses CSV files with flexible column detection
   - Handles common Kaggle dataset formats and column naming variations
   - Caches data in memory for performance
   - Core Java only (no external CSV libraries)

3. ProductMatcher.java
   - Matches user input product name to dataset records
   - Three-tier matching strategy:
     a) Exact match (case-insensitive)
     b) Partial match (substring contains)
     c) Fuzzy match (Levenshtein distance, >70% similarity)

4. DemandService.java
   - Calculates demand score (1-100) based on dataset metrics
   - Weighted algorithm: 35% sales volume, 25% quantity sold, 20% engagement (reviews), 10% rating, 10% popularity
   - Methods:
     - estimateDemand(productName) -> int (1-100 or -1 if not found)
     - calculateCustomDemand(...) -> int for what-if analysis
     - getDatasetStats() -> String for debugging

5. DecisionServletUpdated.java
   - Example of how to integrate DemandService into your servlet
   - Replace your current DecisionServlet with this pattern

---
SETUP STEPS
---

Step 1: Download Kaggle Dataset
   - Visit kaggle.com and find an e-commerce/retail dataset
   - Popular options:
     * "E-commerce Sales Dataset"
     * "Retail Sales Data"
     * "Product Sales Data"
     * "Amazon/Flipkart product dataset"
   - Download CSV file

Step 2: Prepare CSV File
   - Save CSV to your project root, or a specific data/ folder
   - Example path: c:\Users\ashik\Downloads\project-bolt-sb1-vw4ixrvp\data\products.csv
   - Note the full path

Step 3: Copy Java Files
   - Copy all 4 new Java files to: src/main/java/
   - They'll compile automatically with mvn clean install

Step 4: Update DecisionServlet
   - Option A: Replace your entire DecisionServlet with DecisionServletUpdated.java pattern
   - Option B: Add DemandService initialization to your existing servlet
   
   Key change:
   OLD: int demand = Integer.parseInt(request.getParameter("demand"));
   NEW: int demand = demandService.estimateDemand(productName);

Step 5: Configure CSV Path
   - In DecisionServlet.init():
     String csvPath = "path/to/your/dataset.csv";
     demandService = new DemandService(csvPath);
   
   - Use absolute path for reliability:
     String csvPath = "C:\\Users\\ashik\\Downloads\\project-bolt-sb1-vw4ixrvp\\data\\products.csv";

Step 6: Update pom.xml (No Additional Dependencies!)
   - The new code uses only core Java libraries
   - No new Maven dependencies needed
   - Your existing pom.xml will work as-is

Step 7: Rebuild and Test
   cd c:\Users\ashik\Downloads\project-bolt-sb1-vw4ixrvp
   mvn clean install
   mvn jetty:run

---
CSV FORMAT REQUIREMENTS
---

Your Kaggle CSV must have at least one column from each category:

REQUIRED (at least one of):
  Product Name: product_name, productname, product, name, item_name, product_title, title
  Sales Volume: sales, sales_volume, revenue, total_sales, amount_sold
  Quantity Sold: quantity_sold, quantity, units_sold, qty, quantity_purchased
  Reviews: reviews, review_count, num_reviews, comment_count, feedback_count

OPTIONAL (defaults applied if missing):
  - Category (defaults to "General")
  - Rating (defaults to 3.0)
  - Price (defaults to 0.0)
  - Popularity/Views (defaults to 0.0)
  - Stock Available (defaults to 0)

Example CSV structure:
  product_name,category,sales_volume,quantity_sold,avg_rating,review_count,price,popularity_score,stock_available
  iPhone 13 Pro,Electronics,5000000,15000,4.5,5000,79999,8500,245
  Samsung 65" TV,Electronics,2500000,8000,4.3,3200,45000,6200,120
  Saree Cotton,Textiles,150000,5000,4.1,800,1500,3100,450

---
INTEGRATION EXAMPLE
---

In your updated DecisionServlet:

  public void init() throws ServletException {
    // Initialize demand estimation with your CSV
    String csvPath = "C:\\path\\to\\your\\dataset.csv";
    demandService = new DemandService(csvPath);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    ...
    String productName = request.getParameter("productName");
    
    // NEW: Use real market demand instead of user input
    int marketDemand = demandService.estimateDemand(productName);
    if (marketDemand < 0) {
      marketDemand = 50; // Fallback for unmatched products
    }
    
    // Continue with existing logic
    Product product = new Product(productName, stock, sales, marketDemand, price);
    ...
  }

---
DEMAND SCORE CALCULATION
---

The estimateDemand() method returns 1-100 based on:

1. Sales Volume (35% weight)
   - Higher sales revenue = higher rank relative to dataset
   
2. Quantity Sold (25% weight)
   - Units moved indicates real-world demand
   
3. Review Engagement (20% weight)
   - Number of reviews = social proof and customer interest
   - Bonus: +5% if >100 reviews
   
4. Product Rating (10% weight)
   - Quality indicator (typically 1-5 scale)
   - Bonus: +3% if rating >= 4.2
   
5. Popularity Score (10% weight)
   - Views, clicks, or trending signals

All values are converted to percentile ranks (0-100) relative to the dataset, 
then weighted and combined.

---
WHAT-IF ANALYSIS
---

Use calculateCustomDemand() for scenario testing:

  int demand = demandService.calculateCustomDemand(
    100000,  // sales volume
    500,     // quantity sold
    200,     // review count
    4.2,     // rating
    5000     // popularity score
  );
  // Returns demand score 1-100

---
FALLBACK BEHAVIOR
---

If product not found in dataset:
  - estimateDemand() returns -1
  - Frontend receives -1 and can show warning or use default
  - Alternatively, use fallback demand value (50 for neutral)

If CSV file missing:
  - CSVDataLoader returns empty list
  - ProductMatcher returns null
  - estimateDemand() returns -1
  - Check console for error messages

---
PERFORMANCE NOTES
---

- CSV is loaded and cached on first request (lazy loading)
- Subsequent requests use cached data (no file I/O)
- Fuzzy matching uses Levenshtein distance (O(n*m) complexity)
- For datasets >10,000 rows, consider indexing product names

To reload CSV (if file changes):
  demandService.csvLoader.clearCache();

---
DEBUGGING & TESTING
---

Console output shows:
  "Loaded 5234 market records from CSV"
  "Estimated demand for 'iPhone' (matched as 'Apple iPhone 13'): 87"
  "Dataset Stats: 5234 products | Max Sales: 50000000 | Max Qty: 50000 | Max Reviews: 12000 | Avg Rating: 4.15"

Check dataset statistics:
  System.out.println(demandService.getDatasetStats());

---
NEXT STEPS
---

1. Download a Kaggle CSV dataset
2. Place CSV in your project folder
3. Update CSV path in DecisionServlet.init()
4. Copy the 4 new Java files to src/main/java/
5. Rebuild: mvn clean install
6. Run: mvn jetty:run
7. Test backend with frontend form (demand will now auto-estimate)

---
QUESTIONS?
---

Common issues:

Q: "CSV file not found" error
A: Check the CSV path in DecisionServlet.init() - use absolute path

Q: No match found for my product
A: Check if product name variations are close (iPhone vs Apple iPhone)
   View dataset with fuzzy matching debug to understand names

Q: Demand score always same value
A: Likely CSV not loading - check file path and console logs

Q: Performance slow on first request
A: Normal - CSV is being parsed. Subsequent requests are instant (cached)
