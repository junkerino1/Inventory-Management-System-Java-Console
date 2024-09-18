package bookstore;

import java.io.*;
import java.util.*;

public class OutboundTransaction extends Transaction {

    private String transactionID;
    private List<String[]> products = new ArrayList<>();
    private static final String DISPATCH_FILE = "dispatch.txt";
    private static final String INVENTORY_FILE = "inventory.txt";

    public OutboundTransaction() {
        this.transactionID = generateTransactionID();
    }

    // Method to generate transaction ID
    @Override
    protected String generateTransactionID() {
        int randomID = (int) (Math.random() * 1000);
        return String.format("DO%03d", randomID);
    }

    public void placeDispatchOrder() {
        products.clear(); // Clear previous products
        Scanner scanner = new Scanner(System.in);
        boolean addingMore = true;

        Branches.loadBranches(); // Load branches from file
        displayBranches(); // Display available branches

        // Get a valid branch name from the user
        String branchName = getValidBranchName(scanner);
        String branchAddress = Branches.getBranches(branchName);

        // Loop to add products to the dispatch order
        while (addingMore) {
            System.out.print("Enter Product ID: ");
            String productID = scanner.nextLine().trim();

            if (!checkProductExistence(productID)) {
                System.out.println("Product ID does not exist. Please check again.");
                continue;
            }

            String[] productDetails = getProductDetails(productID);

            System.out.println("");

            // Loop for quantity input, check if stock is sufficient
            while (true) {
                System.out.print("Enter Quantity: ");
                int quantity = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (quantity > Integer.parseInt(productDetails[3])) {
                    System.out.println("Insufficient stock. Please enter a valid quantity.");
                } else {
                    products.add(new String[]{productID, productDetails[1], String.valueOf(quantity)});
                    break;
                }
            }

            System.out.print("Add another product? (yes/no): ");
            String response = scanner.nextLine().trim();
            if (!response.equalsIgnoreCase("yes")) {
                addingMore = false;
            }
        }

        displayOrderSummary(branchName, branchAddress); // Display summary of the order
        System.out.print("Confirm dispatch order? (yes/no): ");
        String confirmResponse = scanner.nextLine().trim();

        if (confirmResponse.equalsIgnoreCase("yes")) {
            saveOrderToFile(branchName, branchAddress);
            deductStockFromInventory();
            System.out.println("Dispatch order has been placed and inventory updated.");
        } else {
            System.out.println("Dispatch order canceled.");
        }
    }

    // Get product details from the inventory file
    private String[] getProductDetails(String productID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(INVENTORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(productID)) {
                    return parts;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading inventory file: " + e.getMessage());
        }
        return null;
    }

    // Display the order summary before confirming
    public void displayOrderSummary(String branchName, String branchAddress) {
        System.out.println("\nDispatch Order Confirmation:");
        System.out.println("Dispatch Order ID: " + transactionID);
        System.out.println("Branch Name: " + branchName);
        System.out.println("Branch Address: " + branchAddress);
        System.out.println("------------------------------------------------");
        System.out.printf("%-15s %-20s %-10s%n", "Product ID", "Product Name", "Quantity");
        System.out.println("------------------------------------------------");
        for (String[] product : products) {
            System.out.printf("%-15s %-20s %-10s%n", product[0], product[1], product[2]);
        }
        System.out.println("------------------------------------------------");
    }

    // Save the dispatch order to a file
    public void saveOrderToFile(String branchName, String branchAddress) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DISPATCH_FILE, true))) {
            writer.write("Dispatch ID: " + transactionID);
            writer.newLine();
            writer.write("Branch Name: " + branchName);
            writer.newLine();
            writer.write("Branch Address: " + branchAddress);
            writer.newLine();
            for (String[] product : products) {
                writer.write(product[0] + "," + product[1] + "," + product[2]);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving dispatch: " + e.getMessage());
        }
    }

    // Deduct stock from inventory based on the dispatch order
    private void deductStockFromInventory() {
        List<String[]> updatedInventory = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(INVENTORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String productID = parts[0];
                int currentQuantity = Integer.parseInt(parts[3]);

                for (String[] product : products) {
                    if (product[0].equals(productID)) {
                        int dispatchQuantity = Integer.parseInt(product[2]);
                        if (currentQuantity >= dispatchQuantity) {
                            currentQuantity -= dispatchQuantity;
                        } else {
                            System.out.println("Insufficient stock for product ID: " + productID);
                        }
                        break;
                    }
                }

                updatedInventory.add(new String[]{parts[0], parts[1], parts[2], String.valueOf(currentQuantity), parts[4]});
            }
        } catch (IOException e) {
            System.out.println("Error reading inventory file: " + e.getMessage());
        }

        // Write updated inventory back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(INVENTORY_FILE))) {
            for (String[] product : updatedInventory) {
                writer.write(String.join(",", product));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to inventory file: " + e.getMessage());
        }
    }

    @Override
    // Check if a product exists in the inventory
    public boolean checkProductExistence(String productID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(INVENTORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(productID)) {
                    System.out.println("-------------------------------------------------------");
                    System.out.printf("%-10s %-30s %-10s%n", "ProdID", "ProdName", "Inventory Qty");
                    System.out.println("-------------------------------------------------------");
                    System.out.printf("%-10s %-30s %-10s%n", parts[0], parts[1], parts[3]);
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading inventory file: " + e.getMessage());
        }
        return false;
    }

    // Display available branches
    private void displayBranches() {
        System.out.println("Available Branches:");
        for (Branches branch : Branches.getBranches()) {
            System.out.println("- " + branch.getBranchName());
        }
    }

    // Get a valid branch name from the user
    private String getValidBranchName(Scanner scanner) {
        String branchName;
        while (true) {
            System.out.print("Enter Branch name: ");
            branchName = scanner.nextLine();
            if (Branches.isValidBranchName(branchName)) {
                break;
            } else {
                System.out.println("Invalid branch name. Please try again.");
            }
        }
        return branchName;
    }

    // Check dispatch by ID
    public static void checkDispatchByID() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Dispatch Order ID: ");
        String dispatchID = scanner.nextLine().trim();

        try (BufferedReader reader = new BufferedReader(new FileReader(DISPATCH_FILE))) {
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                // Check if the current line starts with the Dispatch ID we are searching for
                if (line.trim().startsWith("Dispatch ID: " + dispatchID)) {
                    found = true;
                    System.out.println("\nDispatch Order Found:");
                    System.out.println(line); // Print Dispatch ID
                    System.out.println(reader.readLine()); // Print Branch Name
                    System.out.println(reader.readLine()); // Print Branch Address

                    System.out.println("------------------------------------------------");
                    System.out.printf("%-15s %-20s %-10s%n", "Product ID", "Product Name", "Quantity");
                    System.out.println("------------------------------------------------");

                    // Read and print product details until a new Dispatch ID or end of file
                    while ((line = reader.readLine()) != null && !line.startsWith("Dispatch ID:")) {
                        if (!line.trim().isEmpty()) {
                            String[] parts = line.split(",");
                            if (parts.length == 3) {
                                System.out.printf("%-15s %-20s %-10s%n", parts[0].trim(), parts[1].trim(), parts[2].trim());
                            }
                        }
                    }
                    System.out.println("------------------------------------------------");
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    break;
                }
            }

            if (!found) {
                System.out.println("Dispatch ID not found.");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        } catch (IOException e) {
            System.out.println("Error reading dispatch file: " + e.getMessage());
        }
    }


    public static void checkDispatchOrders() {
        Scanner scanner = new Scanner(System.in);
    
        try (BufferedReader reader = new BufferedReader(new FileReader(DISPATCH_FILE))) {
            String line;
            List<String[]> dispatches = new ArrayList<>();
            String currentDispatchID = null;
            String currentBranchName = null;
    
            while ((line = reader.readLine()) != null) {
                line = line.trim();
    
                if (line.startsWith("Dispatch ID:")) {
                    // Add the previous dispatch if it exists
                    if (currentDispatchID != null && currentBranchName != null) {
                        dispatches.add(new String[]{currentDispatchID, currentBranchName});
                    }
                    // Start a new dispatch record
                    currentDispatchID = line.split(":")[1].trim();
                } else if (line.startsWith("Branch Name:")) {
                    currentBranchName = line.split(":")[1].trim();
                }
            }
    
            // Add the last dispatch order to the list
            if (currentDispatchID != null && currentBranchName != null) {
                dispatches.add(new String[]{currentDispatchID, currentBranchName});
            }
    
            System.out.println("------------------------------------------------");
            System.out.printf("%-20s %-30s%n", "Dispatch ID", "Branch Name");
            System.out.println("------------------------------------------------");
    
            for (String[] dispatch : dispatches) {
                System.out.printf("%-20s %-30s%n", dispatch[0], dispatch[1]);
            }
    
            System.out.println("------------------------------------------------");
            System.out.println("Press enter to continue...");
            scanner.nextLine();
    
        } catch (IOException e) {
            System.out.println("Error reading dispatch file: " + e.getMessage());
        }
    }
}
