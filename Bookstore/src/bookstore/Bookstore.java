package bookstore;

import java.util.Scanner;

public class Bookstore {

    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        while (true) {
            clearScreen();
            System.out.println("Bookstore Management System");
            System.out.println("---------------------------");
            System.out.println("1. Manage Inventory");
            System.out.println("2. Inbound Transactions");
            System.out.println("3. Outbound Transactions");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    clearScreen();
                    manageInventory();
                    break;
                case 2:
                    clearScreen();
                    handleInboundTransaction();
                    break;
                case 3:
                    clearScreen();
                    handleOutboundTransaction();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option, please try again.");
            }

        }
    }

    private static void manageInventory() {
        Inventory inventory = new Inventory();
        Report report = new Report();

        inventory.loadProducts();
        while (true) {
            clearScreen();
            System.out.println("Inventory Management");
            System.out.println("--------------------");
            System.out.println("1. Add Product");
            System.out.println("2. Edit Product");
            System.out.println("3. Delete Product");
            System.out.println("4. View Products");
            System.out.println("5. Inventory Report");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    clearScreen();
                    System.out.println("Add Product");
                    System.out.println("-----------");
                    inventory.addProduct();
                    System.out.println("Press enter to continue...");
                    scanner.nextLine();
                    break;
                case 2:
                    clearScreen();
                    System.out.println("Edit Product");
                    System.out.println("------------");
                    inventory.editProduct();
                    System.out.println("Press enter to continue...");
                    scanner.nextLine();
                    break;
                case 3:
                    clearScreen();
                    System.out.println("Delete Product");
                    System.out.println( "--------------");
                    inventory.deleteProduct();
                    System.out.println("Press enter to continue...");
                    scanner.nextLine();
                    break;
                case 4:
                    clearScreen();
                    System.out.println("Products in Inventory");
                    System.out.println("----------------------");
                    inventory.viewProducts();
                    System.out.println("Press enter to continue...");
                    scanner.nextLine();
                    break;
                case 5: 
                    clearScreen();
                    report.generateInventoryReport();
                    System.out.println("Press enter to continue...");
                    scanner.nextLine();
                case 0:
                    return;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }

    private static void handleInboundTransaction() {

        InboundTransaction inboundTransaction = new InboundTransaction();
        Report report = new Report();

        while (true) {
            clearScreen();
            System.out.println("Inbound Transactions");
            System.out.println("--------------------");
            System.out.println("1. Place a Purchase Order");
            System.out.println("2. Receive Stock");
            System.out.println("3. Check Low Stock");
            System.out.println("4. Generate Report");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose an option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    clearScreen();
                    System.out.println("Place a Purchase Order");
                    System.out.println("----------------------");
                    inboundTransaction.placePurchaseOrder();
                    System.out.println("Press enter to continue...");
                    scanner.nextLine();
                    break;
                case 2:
                    clearScreen();
                    System.out.println("Receive Stock");
                    System.out.println("-------------");
                    inboundTransaction.receiveStock();
                    System.out.println("Press enter to continue...");
                    scanner.nextLine();
                    break;
                case 3:
                    clearScreen();
                    report.generateLowStockReport();
                    System.out.println("Press enter to continue...");
                    scanner.nextLine();
                    break;
                case 4:
                    clearScreen();
                    report.generateInboundReport();
                    System.out.println("Press enter to continue...");
                    scanner.nextLine();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }

    private static void handleOutboundTransaction() {
        OutboundTransaction outboundTransaction = new OutboundTransaction();

        while (true) {
            clearScreen();
            System.out.println("Outbound Transactions");
            System.out.println("---------------------");
            System.out.println("1. Create a Dispatch Order");
            System.out.println("2. Check Dispatch Orders");
            System.out.println("3. Check Dispatch By ID");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    clearScreen();
                    outboundTransaction.placeDispatchOrder();
                    break;
                case 2:
                    clearScreen();
                    outboundTransaction.checkDispatchOrders();
                    break;
                case 3:
                    clearScreen();
                    outboundTransaction.checkDispatchByID();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }

    public static void clearScreen() {
        for (int i = 0; i < 20; i++) {
            System.out.println("");
        }
    }
}
