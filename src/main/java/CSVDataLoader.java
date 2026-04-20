import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

/**
 * Loads and parses Kaggle e-commerce CSV datasets with flexible column mapping.
 * Handles common CSV formats and column variations found in real datasets.
 */
public class CSVDataLoader {
    private static final String CHARSET = "UTF-8";
    private List<MarketData> cachedData;
    private String csvFilePath;

    public CSVDataLoader(String csvFilePath) {
        this.csvFilePath = csvFilePath;
        this.cachedData = null;
    }

    /**
     * Loads CSV and returns list of MarketData objects.
     * Caches data in memory for subsequent calls.
     */
    public List<MarketData> loadData() throws IOException {
        if (cachedData != null) {
            return cachedData;
        }

        cachedData = new ArrayList<>();
        File file = new File(csvFilePath);

        if (!file.exists()) {
            System.err.println("CSV file not found: " + csvFilePath);
            return cachedData;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file, Charset.forName(CHARSET)))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                System.err.println("CSV file is empty");
                return cachedData;
            }

            // Parse header to identify column indices
            ColumnMap columnMap = parseHeader(headerLine);

            String line;
            int rowNum = 1;
            while ((line = reader.readLine()) != null) {
                rowNum++;
                if (line.trim().isEmpty()) continue;

                try {
                    MarketData data = parseRow(line, columnMap);
                    if (data != null) {
                        cachedData.add(data);
                    }
                } catch (Exception e) {
                    System.err.println("Warning: Failed to parse row " + rowNum + ": " + e.getMessage());
                }
            }

            System.out.println("Loaded " + cachedData.size() + " market records from CSV");
        }

        return cachedData;
    }

    /**
     * Clears the cached data. Use if you need to reload the file.
     */
    public void clearCache() {
        cachedData = null;
    }

    /**
     * Parses CSV header line and maps column names to indices.
     * Handles variations in column naming conventions.
     */
    private ColumnMap parseHeader(String headerLine) {
        String[] columns = parseCSVLine(headerLine);
        ColumnMap map = new ColumnMap();

        for (int i = 0; i < columns.length; i++) {
            String col = columns[i].toLowerCase().trim();

            // Product name variations
            if (col.matches("product_name|productname|product|item_name|name|product_title|title")) {
                map.productNameIdx = i;
            }
            // Category variations
            else if (col.matches("category|product_category|category_name|class")) {
                map.categoryIdx = i;
            }
            // Sales volume variations
            else if (col.matches("sales|sales_volume|revenue|total_sales|amount_sold")) {
                map.salesVolumeIdx = i;
            }
            // Quantity sold variations
            else if (col.matches("quantity_sold|quantity|units_sold|qty|quantity_purchased")) {
                map.quantitySoldIdx = i;
            }
            // Rating variations
            else if (col.matches("rating|avg_rating|average_rating|score|product_rating")) {
                map.avgRatingIdx = i;
            }
            // Review count variations
            else if (col.matches("reviews|review_count|num_reviews|comment_count|feedback_count")) {
                map.reviewCountIdx = i;
            }
            // Price variations
            else if (col.matches("price|product_price|sale_price|unit_price")) {
                map.priceIdx = i;
            }
            // Popularity variations
            else if (col.matches("popularity|popularity_score|views|clicks|visit_count|trending_score")) {
                map.popularityScoreIdx = i;
            }
            // Stock variations
            else if (col.matches("stock|available|availability|quantity_available|in_stock")) {
                map.stockAvailableIdx = i;
            }
        }

        return map;
    }

    /**
     * Parses a single CSV row and creates a MarketData object.
     * Gracefully handles missing columns with default values.
     */
    private MarketData parseRow(String line, ColumnMap map) throws Exception {
        String[] values = parseCSVLine(line);

        String productName = getStringValue(values, map.productNameIdx, "Unknown");
        String category = getStringValue(values, map.categoryIdx, "General");
        double salesVolume = getDoubleValue(values, map.salesVolumeIdx, 0.0);
        int quantitySold = getIntValue(values, map.quantitySoldIdx, 0);
        double avgRating = getDoubleValue(values, map.avgRatingIdx, 3.0);
        int reviewCount = getIntValue(values, map.reviewCountIdx, 0);
        double price = getDoubleValue(values, map.priceIdx, 0.0);
        double popularityScore = getDoubleValue(values, map.popularityScoreIdx, 0.0);
        int stockAvailable = getIntValue(values, map.stockAvailableIdx, 0);

        // Skip invalid records
        if (productName.isEmpty() || price < 0 || quantitySold < 0) {
            return null;
        }

        return new MarketData(productName, category, salesVolume, quantitySold,
                             avgRating, reviewCount, price, popularityScore, stockAvailable);
    }

    /**
     * Parses a CSV line respecting quoted fields and commas within quotes.
     * Handles standard CSV escaping (double quotes for literal quotes).
     */
    private String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        boolean escaped = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (escaped) {
                current.append(c);
                escaped = false;
            } else if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Double quote inside quoted field = literal quote
                    current.append('"');
                    i++; // Skip next quote
                } else {
                    // Toggle quote state
                    inQuotes = !inQuotes;
                }
            } else if (c == '\\' && i + 1 < line.length()) {
                escaped = true;
            } else if (c == ',' && !inQuotes) {
                // Field separator
                result.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }

        result.add(current.toString().trim());
        return result.toArray(new String[0]);
    }

    private String getStringValue(String[] values, int index, String defaultValue) {
        if (index < 0 || index >= values.length || values[index].isEmpty()) {
            return defaultValue;
        }
        return values[index];
    }

    private double getDoubleValue(String[] values, int index, double defaultValue) {
        try {
            if (index < 0 || index >= values.length || values[index].isEmpty()) {
                return defaultValue;
            }
            return Double.parseDouble(values[index].replace(",", ""));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private int getIntValue(String[] values, int index, int defaultValue) {
        try {
            if (index < 0 || index >= values.length || values[index].isEmpty()) {
                return defaultValue;
            }
            return Integer.parseInt(values[index].replace(",", ""));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Inner class to track column indices from header
     */
    private static class ColumnMap {
        int productNameIdx = -1;
        int categoryIdx = -1;
        int salesVolumeIdx = -1;
        int quantitySoldIdx = -1;
        int avgRatingIdx = -1;
        int reviewCountIdx = -1;
        int priceIdx = -1;
        int popularityScoreIdx = -1;
        int stockAvailableIdx = -1;
    }
}
