import java.util.*;

/**
 * Matches input product against market dataset records.
 * Uses multiple strategies: exact match, contains, and fuzzy name matching.
 */
public class ProductMatcher {

    /**
     * Finds the best matching product from dataset.
     * Returns null if no reasonable match found.
     */
    public static MarketData findBestMatch(String inputProduct, List<MarketData> dataset) {
        if (dataset == null || dataset.isEmpty() || inputProduct == null || inputProduct.isEmpty()) {
            return null;
        }

        String normalized = normalizeProductName(inputProduct);

        // First try exact match (case-insensitive)
        MarketData exactMatch = findExactMatch(normalized, dataset);
        if (exactMatch != null) {
            return exactMatch;
        }

        // Then try partial match
        MarketData partialMatch = findPartialMatch(normalized, dataset);
        if (partialMatch != null) {
            return partialMatch;
        }

        // Finally try fuzzy match
        MarketData fuzzyMatch = findFuzzyMatch(normalized, dataset);
        if (fuzzyMatch != null) {
            return fuzzyMatch;
        }

        return null;
    }

    /**
     * Exact match: normalized input equals normalized dataset product
     */
    private static MarketData findExactMatch(String normalized, List<MarketData> dataset) {
        for (MarketData data : dataset) {
            if (normalizeProductName(data.getProductName()).equals(normalized)) {
                return data;
            }
        }
        return null;
    }

    /**
     * Partial match: normalized input is contained in dataset product name
     */
    private static MarketData findPartialMatch(String normalized, List<MarketData> dataset) {
        for (MarketData data : dataset) {
            String datasetName = normalizeProductName(data.getProductName());
            if (datasetName.contains(normalized) || normalized.contains(datasetName)) {
                return data;
            }
        }
        return null;
    }

    /**
     * Fuzzy match: finds closest match using Levenshtein distance similarity.
     * Only returns match if similarity > 70%.
     */
    private static MarketData findFuzzyMatch(String normalized, List<MarketData> dataset) {
        double bestSimilarity = 0;
        MarketData bestMatch = null;

        for (MarketData data : dataset) {
            String datasetName = normalizeProductName(data.getProductName());
            double similarity = calculateSimilarity(normalized, datasetName);

            if (similarity > bestSimilarity && similarity > 0.7) {
                bestSimilarity = similarity;
                bestMatch = data;
            }
        }

        return bestMatch;
    }

    /**
     * Calculates string similarity using Levenshtein distance algorithm.
     * Returns value between 0 (not similar) and 1 (identical).
     */
    private static double calculateSimilarity(String s1, String s2) {
        int maxLength = Math.max(s1.length(), s2.length());
        if (maxLength == 0) return 1.0;

        int distance = levenshteinDistance(s1, s2);
        return 1.0 - (double) distance / maxLength;
    }

    /**
     * Levenshtein distance: minimum edits (insert, delete, replace) to transform s1 to s2
     */
    private static int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(
                        dp[i - 1][j] + 1,      // deletion
                        dp[i][j - 1] + 1),    // insertion
                        dp[i - 1][j - 1] + cost); // substitution
            }
        }

        return dp[s1.length()][s2.length()];
    }

    /**
     * Normalizes product name for comparison.
     * Handles special characters, extra spaces, case, and common variations.
     */
    private static String normalizeProductName(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }

        return name
                .toLowerCase()              // To lowercase
                .replaceAll("\\s+", " ")    // Normalize spaces
                .replaceAll("[^a-z0-9\\s]", "") // Remove special chars
                .trim();
    }
}
