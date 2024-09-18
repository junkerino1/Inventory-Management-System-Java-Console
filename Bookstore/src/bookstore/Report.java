package bookstore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Report {
    private List<String[]> inventoryData = new ArrayList<>();
    private List<String[]> purchaseData = new ArrayList<>();
    private DecimalFormat df = new DecimalFormat("0.00");

    // Constructor: Loads data from text files when a Report object is created
    public Report() {
        loadInventoryData();
        loadPurchaseData();
    }

    // Getter for inventoryData
    public List<String[]> getInventoryData() {
        return inventoryData;
    }

    // Setter for inventoryData
    public void setInventoryData(List<String[]> inventoryData) {
        this.inventoryData = inventoryData;
    }

    // Load inventory.txt data into inventoryData list
    private void loadInventoryData() {
        try (BufferedReader br = new BufferedReader(new FileReader("inventory.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                inventoryData.add(line.split(","));
            }
        } catch (IOException e) {
            System.out.println("Error reading inventory.txt: " + e.getMessage());
        }
    }

    // Load purchase.txt data into purchaseData list
    private void loadPurchaseData() {
        try (BufferedReader br = new BufferedReader(new FileReader("purchase.txt"))) {
            String line;
            String purchaseOrderID = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Purchase Order ID:")) {
                    purchaseOrderID = line.split(":")[1].trim(); // Store the order ID
                } else if (!line.contains("-----") && !line.startsWith("Total")) {
                    // Format: ProductID,ProductName,Quantity,PricePerUnit,TotalPrice,OrderID
                    String[] purchaseDetail = line.split(",");
                    purchaseData.add(new String[]{purchaseOrderID, purchaseDetail[0], purchaseDetail[1], purchaseDetail[2], purchaseDetail[3], purchaseDetail[4]});
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading purchase.txt: " + e.getMessage());
        }
    }

    // 1. Inventory Report
    public void generateInventoryReport() {
        int totalBooks = 0, totalStationery = 0;
        double totalStockCost = 0.0, bookStockCost = 0.0, stationeryStockCost = 0.0;
        int totalBookQuantity = 0, totalStationeryQuantity = 0;

        System.out.println("----------- Inventory Report -----------");
        for (String[] product : inventoryData) {
            String productID = product[0];
            String productName = product[1];
            double price = Double.parseDouble(product[2]);
            int quantity = Integer.parseInt(product[3]);

            double stockCost = price * quantity;
            totalStockCost += stockCost;

            if (productID.startsWith("B")) {
                totalBooks++;
                bookStockCost += stockCost;
                totalBookQuantity += quantity;
            } else if (productID.startsWith("S")) {
                totalStationery++;
                stationeryStockCost += stockCost;
                totalStationeryQuantity += quantity;
            }
        }

        // Display report
        System.out.println("Total number of products: " + (totalBooks + totalStationery));
        System.out.println("Total number of book products: " + totalBooks);
        System.out.println("Total number of stationery products: " + totalStationery);
        System.out.println("Total stock cost: RM" + df.format(totalStockCost));
        System.out.println("Total stock cost for books: RM" + df.format(bookStockCost));
        System.out.println("Total stock cost for stationery: RM" + df.format(stationeryStockCost));
        System.out.println("Total quantity of books: " + totalBookQuantity);
        System.out.println("Total quantity of stationery: " + totalStationeryQuantity);
        System.out.println("---------------------------------------");
    }

    // 2. Low Stock Report
    public void generateLowStockReport() {
        System.out.println("----------- Low Stock Report -----------");
        System.out.printf("%-15s %-30s %-10s\n", "Product ID", "Product Name", "Quantity");

        for (String[] product : inventoryData) {
            int quantity = Integer.parseInt(product[3]);

            if (quantity < 50) {
                System.out.printf("%-15s %-30s %-10d\n", product[0], product[1], quantity);
            }
        }
        System.out.println("---------------------------------------");
    }

    // 3. Inbound Report
    public void generateInboundReport() {
        System.out.println("----------- Inbound Report -----------");
        System.out.printf("%-15s %-15s %-30s %-10s %-10s %-10s\n", "Order ID", "Product ID", "Product Name", "Quantity", "Unit Price", "Total Price");

        for (String[] purchase : purchaseData) {
            String orderID = purchase[0];
            String productID = purchase[1];
            String productName = purchase[2];
            String quantity = purchase[3];
            String unitPrice = purchase[4];
            String totalPrice = purchase[5];

            System.out.printf("%-15s %-15s %-30s %-10s %-10s %-10s\n", orderID, productID, productName, quantity, unitPrice, totalPrice);
        }
        System.out.println("--------------------------------------");
    }

}
