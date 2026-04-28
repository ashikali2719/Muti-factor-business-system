import org.apache.commons.math3.stat.regression.SimpleRegression;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * DemandPredictor uses Linear Regression to predict demand based on price.
 * Trains model at initialization using CSV data.
 */
public class DemandPredictor {
    private SimpleRegression regression;
    private boolean trained = false;

    public DemandPredictor() {
        regression = new SimpleRegression();
        trainModel();
    }

    private void trainModel() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data/Products.csv"), StandardCharsets.UTF_8))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    double price = Double.parseDouble(parts[7]); // Price per Unit
                    double quantity = Double.parseDouble(parts[6]); // Quantity
                    regression.addData(price, quantity);
                }
            }
            trained = true;
        } catch (Exception e) {
            System.err.println("Failed to train model: " + e.getMessage());
        }
    }

    public double predictDemand(double price) {
        if (!trained || regression.getN() == 0) {
            return 50.0; // fallback
        }
        double pred = regression.predict(price);
        return Math.max(0, Math.min(100, pred)); // normalize to 0-100
    }
}