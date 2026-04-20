import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import io.github.bonigarcia.wdm.WebDriverManager;

public class AmazonScraper implements PriceScraper {
    @Override
    public double scrapePrice(String productName) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://www.amazon.in/s?k=" + productName.replace(" ", "+"));
            // Wait for page load, simple sleep for demo
            Thread.sleep(2000);
            WebElement priceElement = driver.findElement(By.cssSelector(".a-price-whole"));
            String priceText = priceElement.getText().replace(",", "");
            return Double.parseDouble(priceText);
        } catch (Exception e) {
            return -1;
        } finally {
            driver.quit();
        }
    }
}