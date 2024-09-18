package bookstore;

public class Book extends Product {

    private String author;

    public Book(String productID, String name, double price, int quantity, String author) {
        super(productID, name, price, quantity);
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toCSV() {
        return String.format("%s,%s,%.2f,%d,%s",
                getProductID(), getName(), getPrice(), getQuantity(), author);
    }

    @Override
    public String toString() {
        return String.format("Book ID: %s, Name: %s, Price: %.2f, Quantity: %d, Author: %s, Genre: %s",
                getProductID(), getName(), getPrice(), getQuantity(), author);
    }
}
