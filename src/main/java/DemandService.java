import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * DemandService uses ML to predict demand based on price.
 * Falls back to rule-based logic if model fails.
 */
public class DemandService {
    private static final int DEFAULT_DEMAND = 50;
    private static final int MIN_SCORE = 1;
    private static final int MAX_SCORE = 100;

    private final String csvPath;
    private final Map<String, CategoryAggregate> categoryAggregates = new HashMap<>();
    private boolean datasetLoaded = false;
    private int loadedRecords = 0;
    private double overallMinAvgQuantity = 0;
    private double overallMaxAvgQuantity = 0;
    private DemandPredictor predictor;

    public DemandService(String csvPath) {
        this.csvPath = csvPath;
        predictor = new DemandPredictor();
        loadDataset(); // for fallback
    }

    public int getDemand(String productName, double price) {
        // Try ML prediction first
        double predicted = predictor.predictDemand(price);
        if (predicted >= 0) {
            return (int) Math.round(predicted);
        }

        // Fallback to rule-based
        return getDemandRuleBased(productName);
    }

    private int getDemandRuleBased(String productName) {
        if (!datasetLoaded) {
            System.err.println("Dataset not loaded, using fallback demand");
            return DEFAULT_DEMAND;
        }

        if (productName == null || productName.trim().isEmpty()) {
            System.err.println("Product name missing, using fallback demand");
            return DEFAULT_DEMAND;
        }

        String normalizedProduct = normalize(productName);
        String category = inferCategoryFromName(normalizedProduct);
        CategoryAggregate aggregate = categoryAggregates.get(category);

        if (aggregate == null || aggregate.count == 0) {
            System.err.println("No category data for '" + category + "', using fallback demand");
            return DEFAULT_DEMAND;
        }

        double averageQuantity = aggregate.totalQuantity / (double) aggregate.count;
        int score = normalizeScore(averageQuantity, overallMinAvgQuantity, overallMaxAvgQuantity);
        System.out.println("Demand score for '" + productName + "' in category '" + category + "' is " + score);
        return score;
    }

    public boolean isDatasetLoaded() {
        return datasetLoaded;
    }

    public int getLoadedRecordCount() {
        return loadedRecords;
    }

    public int estimateDemand(String productName) {
        return getDemand(productName, 0.0);
    }

    public int calculateCustomDemand(int salesVolume, int quantitySold, int reviewCount, double rating, int popularity) {
        double salesScore = Math.min(100, salesVolume / 100000.0 * 100);
        double quantityScore = Math.min(100, quantitySold / 1000.0 * 100);
        double reviewScore = Math.min(100, reviewCount / 1000.0 * 100);
        double ratingScore = Math.min(100, Math.max(0, rating / 5.0 * 100));
        double popularityScore = Math.min(100, popularity / 10000.0 * 100);

        double weighted = (salesScore * 0.35)
                        + (quantityScore * 0.25)
                        + (reviewScore * 0.20)
                        + (ratingScore * 0.10)
                        + (popularityScore * 0.10);

        int score = MIN_SCORE + (int) Math.round(weighted * 0.8);
        return Math.max(MIN_SCORE, Math.min(MAX_SCORE, score));
    }

    public String getDatasetStats() {
        if (!datasetLoaded) {
            return "Dataset not loaded";
        }
        return String.format("Loaded %d dataset records. Categories: %s", loadedRecords, categoryAggregates.keySet());
    }

    private void loadDataset() {
        File file = new File(csvPath);
        if (!file.exists()) {
            System.err.println("Dataset file not found: " + csvPath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                System.err.println("Dataset file is empty: " + csvPath);
                return;
            }

            HeaderIndices indices = parseHeader(headerLine);
            if (indices.categoryIdx < 0 || indices.quantityIdx < 0) {
                System.err.println("Dataset header is missing required columns. Expected Product Category and Quantity.");
                return;
            }

            String row;
            while ((row = reader.readLine()) != null) {
                if (row.trim().isEmpty()) {
                    continue;
                }

                String[] columns = splitCsv(row);
                String categoryText = getColumnValue(columns, indices.categoryIdx);
                int quantity = parseInteger(getColumnValue(columns, indices.quantityIdx), 0);

                if (quantity <= 0) {
                    continue;
                }

                String category = normalizeCategory(categoryText);
                CategoryAggregate aggregate = categoryAggregates.computeIfAbsent(category, k -> new CategoryAggregate());
                aggregate.count++;
                aggregate.totalQuantity += quantity;
                aggregate.minQuantity = Math.min(aggregate.minQuantity, quantity);
                aggregate.maxQuantity = Math.max(aggregate.maxQuantity, quantity);
                loadedRecords++;
            }

            if (categoryAggregates.isEmpty()) {
                System.err.println("Dataset loaded but no valid category records found.");
                return;
            }

            computeOverallCategoryRanges();
            datasetLoaded = true;
            System.out.println("Loaded " + loadedRecords + " records from dataset");
        } catch (IOException e) {
            System.err.println("Failed to load dataset: " + e.getMessage());
        }
    }

    private HeaderIndices parseHeader(String headerLine) {
        HeaderIndices indices = new HeaderIndices();
        String[] headerColumns = splitCsv(headerLine);

        for (int i = 0; i < headerColumns.length; i++) {
            String normalized = headerColumns[i].trim().toLowerCase();
            if (normalized.matches("product category|category|product_category|category_name")) {
                indices.categoryIdx = i;
            }
            if (normalized.matches("quantity|qty|quantity_sold|units_sold|quantity_per_unit")) {
                indices.quantityIdx = i;
            }
        }

        return indices;
    }

    private void computeOverallCategoryRanges() {
        double minAverage = Double.MAX_VALUE;
        double maxAverage = Double.MIN_VALUE;

        for (CategoryAggregate aggregate : categoryAggregates.values()) {
            if (aggregate.count == 0) {
                continue;
            }
            double average = aggregate.totalQuantity / (double) aggregate.count;
            aggregate.averageQuantity = average;
            minAverage = Math.min(minAverage, average);
            maxAverage = Math.max(maxAverage, average);
        }

        if (minAverage == Double.MAX_VALUE || maxAverage == Double.MIN_VALUE) {
            minAverage = 0;
            maxAverage = 0;
        }

        overallMinAvgQuantity = minAverage;
        overallMaxAvgQuantity = maxAverage;
    }

    private int normalizeScore(double averageQuantity, double minValue, double maxValue) {
        if (maxValue <= minValue) {
            return DEFAULT_DEMAND;
        }

        double ratio = (averageQuantity - minValue) / (maxValue - minValue);
        int score = MIN_SCORE + (int) Math.round(ratio * (MAX_SCORE - MIN_SCORE));
        return Math.max(MIN_SCORE, Math.min(MAX_SCORE, score));
    }

    private String normalizeCategory(String categoryText) {
        if (categoryText == null) {
            return "other";
        }

        String normalized = normalize(categoryText);
        if (normalized.contains("electronic") || normalized.contains("phone") || normalized.contains("laptop") || normalized.contains("camera") || normalized.contains("tablet") || normalized.contains("charger") || normalized.contains("speaker")) {
            return "electronics";
        }
        if (normalized.contains("shirt") || normalized.contains("jeans") || normalized.contains("saree") || normalized.contains("dress") || normalized.contains("jacket") || normalized.contains("clothes") || normalized.contains("apparel") || normalized.contains("fashion")) {
            return "clothing";
        }
        if (normalized.contains("beauty") || normalized.contains("makeup") || normalized.contains("skincare") || normalized.contains("perfume") || normalized.contains("cosmetic") || normalized.contains("cream") || normalized.contains("serum") || normalized.contains("lotion")) {
            return "beauty";
        }
        return "other";
    }

    private String inferCategoryFromName(String productName) {
        if (productName == null || productName.isEmpty()) {
            return "other";
        }

        if (productName.contains("phone") || productName.contains("laptop") || productName.contains("camera") || productName.contains("tv") || productName.contains("headphone") || productName.contains("speaker") || productName.contains("charger")) {
            return "electronics";
        }
        if (productName.contains("shirt") || productName.contains("jeans") || productName.contains("saree") || productName.contains("dress") || productName.contains("jacket") || productName.contains("trouser") || productName.contains("pant") || productName.contains("shoe")) {
            return "clothing";
        }
        if (productName.contains("lipstick") || productName.contains("cream") || productName.contains("serum") || productName.contains("perfume") || productName.contains("makeup") || productName.contains("skincare") || productName.contains("facewash") || productName.contains("lotion")) {
            return "beauty";
        }
        return "other";
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private String getColumnValue(String[] columns, int index) {
        if (index < 0 || index >= columns.length) {
            return "";
        }
        return columns[index].trim();
    }

    private int parseInteger(String value, int fallback) {
        try {
            if (value == null || value.isEmpty()) {
                return fallback;
            }
            return Integer.parseInt(value.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private String[] splitCsv(String line) {
        if (line == null) {
            return new String[0];
        }

        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        java.util.List<String> values = new java.util.ArrayList<>();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        values.add(current.toString().trim());
        return values.toArray(new String[0]);
    }

    private static class HeaderIndices {
        int categoryIdx = -1;
        int quantityIdx = -1;
    }

    private static class CategoryAggregate {
        int count;
        double totalQuantity;
        double averageQuantity;
        int minQuantity = Integer.MAX_VALUE;
        int maxQuantity = Integer.MIN_VALUE;
    }
}
