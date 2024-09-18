package bookstore;

public class Stationery extends Product {

    private String brand;

    public Stationery(String productID, String name, double price, int quantity, String brand) {
        super(productID, name, price, quantity);
        this.brand = brand;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Override
    public String toCSV() {
        return String.format("%s,%s,%.2f,%d,%s", getProductID(), getName(), getPrice(), getQuantity(), brand);
    }

    @Override
    public String toString() {
        return String.format("Stationery ID: %s, Name: %s, Price: %.2f, Quantity: %d, Brand: %s", getProductID(), getName(), getPrice(), getQuantity(), brand);
    }
}
