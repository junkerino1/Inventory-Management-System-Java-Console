package bookstore;

import java.io.*;
import java.util.*;

public abstract class Transaction {

    protected String transactionID;
    protected String branchName;
    protected List<String[]> products;

    private static final String ORDERS_DIR = "C:\\Users\\junka\\OneDrive\\Documents\\NetBeansProjects\\Inventory\\Purchase Orders\\";
    private static final String INVENTORY_FILE = "inventory.txt";

    public Transaction() {
        this.transactionID = generateTransactionID();
        this.branchName = branchName;
        this.products = new ArrayList<>();
    }

    // Generate a random Transaction ID
    protected String generateTransactionID() {
        int randomID = (int) (Math.random() * 1000);
        return String.format("TX%03d", randomID);
    }

    public boolean checkProductExistence(String productID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(INVENTORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(productID)) {
                    System.out.println("----------------------------------------------------------------------");
                    System.out.printf("%-10s %-30s %-10s %-10s%n", "Product ID", "Product Name", "Available Quantity", "Price");
                    System.out.println("----------------------------------------------------------------------");
                    System.out.printf("%-10s %-30s %-10s RM%-9s%n", parts[0], parts[1], parts[3], parts[2]);
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return false;
    }
}
