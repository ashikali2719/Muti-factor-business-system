/**
 * Represents a single market data record from the Kaggle dataset.
 * Flexible structure to handle common e-commerce dataset columns.
 */
public class MarketData {
    private String productName;
    private String category;
    private double salesVolume;      // Total sales revenue
    private int quantitySold;        // Units sold
    private double avgRating;        // 1-5 or similar
    private int reviewCount;         // Number of reviews
    private double price;            // Product price
    private double popularityScore;  // Views, clicks, or custom score
    private int stockAvailable;      // Available inventory

    public MarketData(String productName, String category, double salesVolume, int quantitySold,
                     double avgRating, int reviewCount, double price, double popularityScore, int stockAvailable) {
        this.productName = productName;
        this.category = category;
        this.salesVolume = salesVolume;
        this.quantitySold = quantitySold;
        this.avgRating = avgRating;
        this.reviewCount = reviewCount;
        this.price = price;
        this.popularityScore = popularityScore;
        this.stockAvailable = stockAvailable;
    }

    // Getters
    public String getProductName() { return productName; }
    public String getCategory() { return category; }
    public double getSalesVolume() { return salesVolume; }
    public int getQuantitySold() { return quantitySold; }
    public double getAvgRating() { return avgRating; }
    public int getReviewCount() { return reviewCount; }
    public double getPrice() { return price; }
    public double getPopularityScore() { return popularityScore; }
    public int getStockAvailable() { return stockAvailable; }
}
