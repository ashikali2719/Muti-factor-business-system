import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import io.github.bonigarcia.wdm.WebDriverManager;

public class MeeshoScraper implements PriceScraper {
    @Override
    public double scrapePrice(String productName) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://www.meesho.com/search?q=" + productName.replace(" ", "+"));
            Thread.sleep(2000);
            WebElement priceElement = driver.findElement(By.cssSelector(".price"));
            String priceText = priceElement.getText().replace("₹", "").replace(",", "");
            return Double.parseDouble(priceText);
        } catch (Exception e) {
            return -1;
        } finally {
            driver.quit();
        }
    }
}