SAMPLE KAGGLE DATASET STRUCTURE
===============================

This file shows example CSV formats from real Kaggle datasets that work with DemandService.

---
EXAMPLE 1: E-Commerce Sales Dataset (India)
---

CSV filename: amazon_flipkart_sales.csv

product_name,category,sales_revenue,units_sold,rating,review_count,price,popularity_views,available_stock
"iPhone 13 Pro Max",Electronics,8500000,12000,4.6,5400,99999,45000,156
"Samsung 65 inch QLED TV",Electronics,5200000,8500,4.4,3200,49999,32000,89
"Cotton Saree Traditional",Textiles,420000,2100,4.3,450,1999,8500,320
"JBL Flip 6 Speaker",Electronics,1200000,4500,4.5,2100,6999,18000,420
"Basmati Rice 5kg",Groceries,850000,15000,4.2,1200,499,22000,880
"Laptop Stand Aluminum",Office,320000,8000,4.1,950,1299,12000,150
"USB-C Cable 3m",Electronics,180000,12000,4.0,540,299,8500,2100
"Yoga Mat Non-Slip",Fitness,450000,5500,4.3,1100,799,14000,380
"Smart Watch Band",Accessories,280000,6200,3.9,420,499,6800,120
"Bookshelf Wood 3-Shelf",Furniture,620000,1200,4.4,880,4999,9500,45

Column Interpretation:
- product_name: Exact product name from sellers
- category: Product category
- sales_revenue: Total sales value in INR/currency units
- units_sold: Number of units sold
- rating: Average rating (1-5 scale)
- review_count: Number of customer reviews
- price: Current listing price
- popularity_views: Total product page views
- available_stock: Current inventory


---
EXAMPLE 2: Retail Dataset Format
---

CSV filename: retail_products.csv

Product,Category,Total_Sales_Amount,Quantity_Purchased,Avg_Rating,Number_of_Reviews,Unit_Price,Page_Views,Units_in_Stock
MacBook Pro 16",Computers,12500000,8500,4.7,6200,145000,52000,45
"Dell XPS 13",Computers,8900000,6500,4.5,4100,95000,38000,62
"Sony WH-1000XM4 Headphones",Audio,3200000,8000,4.6,3500,24999,28000,145
"Logitech MX Master 3",Peripherals,1500000,5500,4.5,2100,9999,16000,210
"Samsung 27\" Monitor",Displays,2800000,4200,4.4,1800,24999,19000,85
"Mechanical Keyboard RGB",Peripherals,980000,7500,4.3,2200,5999,14000,320
"External SSD 1TB",Storage,2100000,6000,4.5,2800,9999,21000,190
"Webcam HD 1080P",Peripherals,650000,5500,4.2,1600,4999,12000,280
"USB Hub 7-Port",Accessories,420000,8500,4.1,1100,2499,9500,450
"Monitor Arm Stand",Accessories,780000,3200,4.4,950,3299,11000,125

Column Interpretation:
- Product: Product name
- Category: Product type/category
- Total_Sales_Amount: Total revenue generated
- Quantity_Purchased: Units sold
- Avg_Rating: Customer satisfaction rating
- Number_of_Reviews: Engagement metric
- Unit_Price: Product price
- Page_Views: Popularity indicator
- Units_in_Stock: Current stock level


---
EXAMPLE 3: Mixed Format (Flexible Naming)
---

CSV filename: market_data.csv

name,type,revenue,qty,stars,comments,cost,visits,stocked
"iPad Pro 12.9",Tablets,4500000,5000,4.5,2800,34999,22000,89
"iPhone 14 Pro",Phones,18000000,25000,4.7,12000,99999,85000,234
"AirPods Pro",Audio,5200000,12000,4.6,6500,24999,38000,145
"Apple Watch Series 8",Wearables,2800000,4500,4.5,2100,41999,16000,67
"Magic Keyboard",Accessories,1200000,3000,4.4,1400,7999,12000,45
"USB-C Cable",Cables,450000,8500,4.2,980,299,9000,850
"Screen Protector",Accessories,620000,10000,4.1,1200,99,8500,1200
"Phone Case",Accessories,890000,12000,4.3,2100,399,11000,1500
"Charger 20W",Accessories,780000,6500,4.4,1600,999,10000,320
"Car Mount",Accessories,340000,4000,4.2,850,599,6000,180

Column Interpretation:
- name: Product name
- type: Category
- revenue: Total sales
- qty: Quantity sold
- stars: Rating
- comments: Review count
- cost: Price
- visits: Page views/popularity
- stocked: Current inventory


---
KAGGLE DATASET RECOMMENDATIONS
---

Best Public Datasets:
1. "E-commerce Sales Dataset" (Kaggle - various shops)
2. "Retail Sales Data" (Walmart, supermarket chains)
3. "Amazon Product Recommendations" (category data)
4. "Flipkart Products Dataset" (Indian e-commerce)
5. "Online Retail II" (UK e-commerce transactions)

Download Steps:
1. Go to kaggle.com
2. Search for retail/e-commerce dataset
3. Click dataset
4. Click "Download" (requires Kaggle login)
5. Extract CSV file
6. Place in your project


---
NORMALIZING YOUR CSV
---

If your CSV has different column names:

Before using with DemandService:
1. Open CSV in Excel/LibreOffice
2. Add/rename columns to match one of the examples above
3. Remove sensitive columns (customer data, etc)
4. Ensure numeric columns have no currency symbols
5. Save and place in project root

Example cleanup:
- Change "Product Name" → "product_name"
- Change "₹ Revenue" → "sales_volume" (remove currency)
- Change "Stars (1-5)" → "rating"
- Change "Review Count" → "reviews"


---
SAMPLE DEMAND CALCULATIONS
---

Using Example 1 dataset:

Product: "iPhone 13 Pro Max"
  Sales: 8,500,000 (rank: 95th percentile)
  Quantity: 12,000 (rank: 92nd percentile)
  Reviews: 5,400 (rank: 98th percentile) → +5% boost
  Rating: 4.6/5 (rank: 95th percentile) → +3% boost
  Popularity: 45,000 views (rank: 88th percentile)
  
  Weighted: 95*0.35 + 92*0.25 + 98*0.20 + 95*0.10 + 88*0.10 = 93
  With boosts: 93 * 1.05 * 1.03 = 100 ✓
  
  Result: Demand = 100 (HIGH)


Product: "USB-C Cable 3m"
  Sales: 180,000 (rank: 20th percentile)
  Quantity: 12,000 (rank: 92nd percentile)
  Reviews: 540 (rank: 15th percentile)
  Rating: 4.0/5 (rank: 60th percentile)
  Popularity: 8,500 views (rank: 25th percentile)
  
  Weighted: 20*0.35 + 92*0.25 + 15*0.20 + 60*0.10 + 25*0.10 = 41
  
  Result: Demand = 41 (MEDIUM-LOW)
  
  Explanation: High quantity sold but low sales revenue and engagement suggests 
  low-value product with steady demand


---
TESTING YOUR CSV
---

After placing CSV in your project, test with:

  DemandService service = new DemandService("path/to/your/dataset.csv");
  System.out.println(service.getDatasetStats());
  
  int demand = service.estimateDemand("iPhone 13 Pro Max");
  System.out.println("Demand: " + demand); // Should output 1-100

Check console output for:
  "Loaded 234 market records from CSV"
  May show warnings if some columns are missing (that's OK - defaults apply)


---
REQUIREMENTS CHECKLIST
---

✓ CSV file downloaded from Kaggle
✓ CSV has product name column
✓ CSV has sales or quantity column
✓ CSV has reviews column (for engagement)
✓ CSV has rating column (optional, defaults to 3.0)
✓ Numeric columns have no currency symbols (₹, $, €, etc)
✓ CSV placed in project root or specific data/ folder
✓ Path configured in DecisionServlet.init()
✓ All 4 Java files copied to src/main/java/
✓ mvn clean install runs without errors
