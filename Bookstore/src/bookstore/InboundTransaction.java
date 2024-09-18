package bookstore;

import java.io.*;
import java.util.*;

public class InboundTransaction extends Transaction {

    private static final String ORDERS_DIR = "Purchase Order\\";
    private static final String FILE_NAME = "inventory.txt";
    private static List<String[]> productsToReceive = new ArrayList<>();
    private List<String[]> products = new ArrayList<>();

    private String transactionID;
    private double totalAmount;

    public InboundTransaction() {
        this.transactionID = generateTransactionID();
        this.totalAmount = 0.0;
    }

    // Method to generate ID
    @Override
    protected String generateTransactionID() {
        int randomID = (int) (Math.random() * 1000);
        return String.format("PO%03d", randomID);
    }

    // Methods revolving around placing purchase order
    public void placePurchaseOrder() {

        products.clear();

        Scanner scanner = new Scanner(System.in);
        boolean addingMore = true;
        totalAmount = 0;

        while (addingMore) {
            System.out.print("Enter Product ID: ");
            String productID = scanner.nextLine().trim();

            if (!checkProductExistence(productID)) {
                System.out.println("Product ID does not exist. Please check again.");
                continue;
            }

            System.out.print("Enter Quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (quantity <= 0) {
                System.out.println("Invalid quantity. Please enter a positive number.");
                continue;
            }

            // Fetch product details (Product Name, Price per Unit)
            String[] productDetails = getProductDetails(productID);  // Example: {ProductID, Name, PricePerUnit}
            if (productDetails != null) {
                double pricePerUnit = Double.parseDouble(productDetails[2]);
                double totalPrice = pricePerUnit * quantity;

                // Add product details into products list
                products.add(new String[]{
                    // Format: Product ID, Product Name, Quantity, Price
                    productID, // 0: Product ID
                    productDetails[1], // 1: Product Name
                    String.valueOf(quantity), // 2: Quantity
                    String.format("%.2f", pricePerUnit), // 3: Price per Unit
                    String.format("%.2f", totalPrice) // 4: Total Price
                });

                totalAmount += totalPrice;
            } else {
                System.out.println("Product details could not be retrieved.");
            }

            System.out.print("Add another product? (yes/no): ");
            String response = scanner.nextLine().trim();
            if (!response.equalsIgnoreCase("yes")) {
                addingMore = false;
            }
        }

        displayOrderSummary();
        saveOrderToFile();
    }

    private void displayOrderSummary() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("--------------------------------------------------------------------------------------");
        System.out.printf("%-15s %-20s %-10s %-15s %-10s%n", "Product ID", "Product Name", "Quantity", "Price per Unit", "Total Price");
        System.out.println("--------------------------------------------------------------------------------------");

        for (String[] product : products) {
            System.out.printf("%-15s %-20s %-10s RM%-15s RM%-10s%n", product[0], product[1], product[2], product[3], product[4]);
        }

        System.out.println("--------------------------------------------------------------------------------------");
        System.out.printf("Total Amount: RM %.2f%n", totalAmount);

        System.out.println("Please select a payment method:");
        System.out.println("1. Credit/Debit Card");
        System.out.println("2. Online Banking");
        System.out.println("3. eWallet");
        System.out.print("Enter the number of your chosen payment method: ");
        int paymentMethod = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Payment successful! Your order (" + transactionID + ") has been placed.");
    }

    private String[] getProductDetails(String productID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(productID)) {
                    // Format: Product ID, Product Name, Price
                    return new String[]{parts[0], parts[1], parts[2]};

                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return null;
    }

    public void saveOrderToFile() {
        // Save to file named after transactionID
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDERS_DIR + transactionID + ".txt"))) {
            writer.write("Purchase Order ID: " + transactionID);
            writer.newLine();
            writer.write("--------------------------------------------------------------------------------------");
            writer.newLine();
            writer.write(String.format("%-15s %-20s %-10s %-15s %-10s", "Product ID", "Product Name", "Quantity", "Price per Unit", "Total Price"));
            writer.newLine();
            writer.write("--------------------------------------------------------------------------------------");
            writer.newLine();
            for (String[] product : products) {
                writer.write(String.format("%-15s %-20s %-10s %-15s %-10s", product[0], product[1], product[2], product[3], product[4]));
                writer.newLine();
            }
            writer.write("--------------------------------------------------------------------------------------");
            writer.newLine();
            writer.write(String.format("%-60s %s", "Total", String.format("%.2f", totalAmount)));
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving order to transaction file: " + e.getMessage());
        }

        // Save to purchase.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("purchase.txt", true))) { // Append mode
            writer.write("Purchase Order ID: " + transactionID);
            writer.newLine();
            writer.write("--------------------------------------------------------------------------------------");
            writer.newLine();
            for (String[] product : products) {
                writer.write(String.format("%s,%s,%s,%s,%s", product[0], product[1], product[2], product[3], product[4]));
                writer.newLine();
            }
            writer.write(String.format("Total:%s", String.format("%.2f", totalAmount)));
            writer.newLine();
            writer.write("--------------------------------------------------------------------------------------");
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving order to purchase.txt: " + e.getMessage());
        }
    }

    public boolean receiveStock() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Purchase Order ID: ");
        String purchaseOrder = scanner.nextLine().trim();
        String filePath = "purchase.txt";
        productsToReceive.clear(); // Clear the list to avoid accumulation of previous entries

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean orderFound = false;

            while ((line = reader.readLine()) != null) {
                if (line.contains("Purchase Order ID: " + purchaseOrder)) {
                    orderFound = true;
                    // Skip the separator line
                    reader.readLine();
                    continue;
                }

                if (orderFound) {
                    // Check for the end of the current order
                    if (line.startsWith("Purchase Order ID:") || line.trim().isEmpty()) {
                        break;
                    }

                    // Split line by comma
                    String[] parts = line.split(",");

                    // Check that the line contains the expected number of elements
                    if (parts.length >= 5) {
                        String productID = parts[0].trim();
                        String productName = parts[1].trim();
                        int quantity;
                        try {
                            quantity = Integer.parseInt(parts[2].trim());
                        } catch (NumberFormatException e) {
                            System.out.println("Error parsing quantity for product: " + productID);
                            continue;
                        }

                        // Add product details into the list
                        productsToReceive.add(new String[]{productID, productName, String.valueOf(quantity)});
                    }
                }
            }

            if (!orderFound) {
                System.out.println("Order not found in the file.");
                return false;
            }

        } catch (FileNotFoundException e) {
            System.out.println("Order file not found: " + filePath);
            return false;
        } catch (IOException e) {
            System.out.println("Error reading the order file.");
            return false;
        }

        displayProducts(); // Display the products to be received

        System.out.print("Confirm stock update (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes")) {
            updateStock();
            System.out.println("Stock has been updated successfully.");
        } else {
            System.out.println("Stock update was canceled.");
        }

        return false;
    }

    public static void updateStock() {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                boolean productFound = false;

                // Check if the current line contains a product ID that matches any in productsToReceive
                for (String[] product : productsToReceive) {
                    String productID = product[0];
                    if (parts[0].equals(productID)) {
                        // If product found, update the quantity
                        int currentQuantity = Integer.parseInt(parts[3]);
                        int receivedQuantity = Integer.parseInt(product[2]);
                        currentQuantity += receivedQuantity;

                        // Update the line with new quantity
                        line = String.join(",", parts[0], parts[1], parts[2], String.valueOf(currentQuantity), parts[4]);
                        productFound = true;
                        break;
                    }
                }

                // Add the line to the list whether or not it was updated
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading inventory file: " + e.getMessage());
            return;
        }

        // Write updated lines back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing inventory file: " + e.getMessage());
        }
    }

    private static void displayProducts() {
        System.out.println("Products to Receive:");
        System.out.println("--------------------------------------------------------------------------------------");
        System.out.printf("%-15s %-20s %-10s%n", "Product ID", "Product Name", "Quantity");
        System.out.println("--------------------------------------------------------------------------------------");

        for (String[] product : productsToReceive) {
            System.out.printf("%-15s %-20s %-10s%n", product[0], product[1], product[2]);
        }
    }
}
