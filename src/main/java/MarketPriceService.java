import java.util.*;

public class MarketPriceService {
    private List<PriceScraper> scrapers;

    public MarketPriceService() {
        scrapers = Arrays.asList(new AmazonScraper(), new FlipkartScraper(), new MeeshoScraper());
    }

    public double getFinalPrice(String productName) {
        List<Double> prices = new ArrayList<>();
        for (PriceScraper scraper : scrapers) {
            double price = scraper.scrapePrice(productName);
            if (price > 0) {
                prices.add(price);
            }
        }
        if (prices.isEmpty()) {
            return 0; // fallback
        }
        return Collections.min(prices);
    }
}