import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PythonMLService {

    public double predictDemand(double inventory, double unitsSold, double unitsOrdered,
                                double price, double discount, double competitorPrice) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "python",
                    "ML/predict.py",
                    String.valueOf(inventory),
                    String.valueOf(unitsSold),
                    String.valueOf(unitsOrdered),
                    String.valueOf(price),
                    String.valueOf(discount),
                    String.valueOf(competitorPrice)
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String output = reader.readLine();
            process.waitFor();

            return Double.parseDouble(output);

        } catch (Exception e) {
            System.out.println("ML prediction failed: " + e.getMessage());
            return 50.0; // fallback demand
        }
    }
}