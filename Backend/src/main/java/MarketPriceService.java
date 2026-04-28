import java.util.*;

public class MarketPriceService {
    private List<PriceScraper> scrapers;

    public MarketPriceService() {
        scrapers = Arrays.asList(
            new AmazonScraper(),
            new FlipkartScraper(),
            new MeeshoScraper()
        );
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
            return 0;
        }

        // Remove abnormal prices
        prices = removeOutliers(prices);

        if (prices.isEmpty()) {
            return 0;
        }

        // Use median instead of minimum
        return calculateMedian(prices);
    }

    private List<Double> removeOutliers(List<Double> prices) {
        if (prices.size() <= 2) {
            return prices;
        }

        Collections.sort(prices);

        double median = calculateMedian(prices);
        List<Double> filtered = new ArrayList<>();

        for (double price : prices) {
            double differencePercent = Math.abs(price - median) / median;

            // Keep prices within 50% range of median
            if (differencePercent <= 0.50) {
                filtered.add(price);
            }
        }

        return filtered;
    }

    private double calculateMedian(List<Double> prices) {
        Collections.sort(prices);

        int n = prices.size();

        if (n % 2 == 1) {
            return prices.get(n / 2);
        } else {
            return (prices.get(n / 2 - 1) + prices.get(n / 2)) / 2.0;
        }
    }
}