public class Product {
    private String name;
    private int stock;
    private int sales;
    private int demand;
    private double price;

    public Product(String name, int stock, int sales, int demand, double price) {
        this.name = name;
        this.stock = stock;
        this.sales = sales;
        this.demand = demand;
        this.price = price;
    }

    // Add getters if needed
    public String getName() { return name; }
    public int getStock() { return stock; }
    public int getSales() { return sales; }
    public int getDemand() { return demand; }
    public double getPrice() { return price; }
}