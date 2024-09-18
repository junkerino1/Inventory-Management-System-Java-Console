package bookstore;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Inventory {

    private static List<Book> books;
    private static List<Stationery> stationeries;
    private static final String FILE_NAME = "inventory.txt";
    private static final Pattern BOOK_REGEX = Pattern.compile("B\\d{3}");
    private static final Pattern STATIONERY_REGEX = Pattern.compile("S\\d{3}");

    public Inventory() {
        this.books = new ArrayList<>();
        this.stationeries = new ArrayList<>();
        loadProducts();
    }

    protected void loadProducts() {
        books.clear();
        stationeries.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String productID = data[0].trim();
                String name = data[1].trim();
                double price = Double.parseDouble(data[2].trim());
                int quantity = Integer.parseInt(data[3].trim());
                String authorOrBrand = data[4].trim();

                // Identify type of product by checking the first letter of productID
                if (productID.startsWith("B")) {
                    books.add(new Book(productID, name, price, quantity, authorOrBrand));
                } else if (productID.startsWith("S")) {
                    stationeries.add(new Stationery(productID, name, price, quantity, authorOrBrand));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // Save products to file
    private void saveProducts() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Book book : books) {
                writer.write(book.toCSV());
                writer.newLine();
            }
            for (Stationery stationery : stationeries) {
                writer.write(stationery.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    // Add new product
    public void addProduct() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter product type (Book/Stationery): ");
        String type = scanner.nextLine().trim();

        if (type.equalsIgnoreCase("Book")) {
            addBook(scanner);
        } else if (type.equalsIgnoreCase("Stationery")) {
            addStationery(scanner);
        } else {
            System.out.println("Invalid product type.");
        }
        saveProducts();
    }

    public void addBook(Scanner scanner) {
        System.out.print("Enter Book ID (e.g., B001): ");
        String productID = scanner.nextLine().trim();
        if (!BOOK_REGEX.matcher(productID).matches()) {
            System.out.println("Invalid Book ID format.");
            return;
        }
        System.out.print("Enter Book name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Book price: ");
        double price = scanner.nextDouble();
        int quantity = 0;
        scanner.nextLine(); // Consume newline
        System.out.print("Enter Book author: ");
        String author = scanner.nextLine().trim();

        books.add(new Book(productID, name, price, quantity, author));
        System.out.println("Book added to inventory: " + name);
    }

    private void addStationery(Scanner scanner) {
        System.out.print("Enter Stationery ID (e.g., S001): ");
        String productID = scanner.nextLine().trim();
        if (!STATIONERY_REGEX.matcher(productID).matches()) {
            System.out.println("Invalid Stationery ID format.");
            return;
        }
        System.out.print("Enter Stationery name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Stationery price: ");
        double price = scanner.nextDouble();
        int quantity = 0;
        scanner.nextLine(); // Consume newline
        System.out.print("Enter Stationery brand: ");
        String brand = scanner.nextLine().trim();
        stationeries.add(new Stationery(productID, name, price, quantity, brand));
        System.out.println("Stationery added to inventory: " + name);
    }

    // Edit product
    public void editProduct() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter product type to edit (Book/Stationery): ");
        String type = scanner.nextLine().trim();

        if (type.equalsIgnoreCase("Book")) {
            editBook(scanner);
        } else if (type.equalsIgnoreCase("Stationery")) {
            editStationery(scanner);
        } else {
            System.out.println("Invalid product type.");
        }
        saveProducts();
    }

    private void editBook(Scanner scanner) {
        System.out.print("Enter Book ID to edit: ");
        String productID = scanner.nextLine().trim();
        Book bookToEdit = findBookByID(productID);

        if (bookToEdit != null) {
            System.out.println("Editing Book: " + bookToEdit.getName());
            System.out.print("Enter new name (leave blank to keep current): ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) {
                bookToEdit.setName(name);
            }

            System.out.print("Enter new price (leave blank to keep current): ");
            String priceInput = scanner.nextLine().trim();
            if (!priceInput.isEmpty()) {
                bookToEdit.setPrice(Double.parseDouble(priceInput));
            }

            String quantityInput = "";

            System.out.print("Enter new author (leave blank to keep current): ");
            String author = scanner.nextLine().trim();
            if (!author.isEmpty()) {
                bookToEdit.setAuthor(author);
            }

            System.out.println("Book updated successfully.");
        } else {
            System.out.println("Book with ID " + productID + " not found.");
        }
    }

    private void editStationery(Scanner scanner) {
        System.out.print("Enter Stationery ID to edit: ");
        String productID = scanner.nextLine().trim();
        Stationery stationeryToEdit = findStationeryByID(productID);

        if (stationeryToEdit != null) {
            System.out.println("Editing Stationery: " + stationeryToEdit.getName());
            System.out.print("Enter new name (leave blank to keep current): ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) {
                stationeryToEdit.setName(name);
            }

            System.out.print("Enter new price (leave blank to keep current): ");
            String priceInput = scanner.nextLine().trim();
            if (!priceInput.isEmpty()) {
                stationeryToEdit.setPrice(Double.parseDouble(priceInput));
            }

            String quantityInput = "";

            System.out.print("Enter new brand (leave blank to keep current): ");
            String brand = scanner.nextLine().trim();
            if (!brand.isEmpty()) {
                stationeryToEdit.setBrand(brand);
            }

            System.out.println("Stationery updated successfully.");
        } else {
            System.out.println("Stationery with ID " + productID + " not found.");
        }
    }

    private Book findBookByID(String productID) {
        for (Book book : books) {
            if (book.getProductID().equals(productID)) {
                return book;
            }
        }
        return null;
    }

    private Stationery findStationeryByID(String productID) {
        for (Stationery stationery : stationeries) {
            if (stationery.getProductID().equals(productID)) {
                return stationery;
            }
        }
        return null;
    }

    // Delete product
    public void deleteProduct() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter product type to delete (Book/Stationery): ");
        String type = scanner.nextLine().trim();

        if (type.equalsIgnoreCase("Book")) {
            deleteBook(scanner);
        } else if (type.equalsIgnoreCase("Stationery")) {
            deleteStationery(scanner);
        } else {
            System.out.println("Invalid product type.");
        }
        saveProducts();
    }

    private void deleteBook(Scanner scanner) {
        System.out.print("Enter Book ID to delete: ");
        String productID = scanner.nextLine().trim();
        Book bookToDelete = findBookByID(productID);

        if (bookToDelete != null) {
            System.out.println("Product found:");
            System.out.println("--------------------------------------------------------------");
            System.out.printf("%-10s %-30s %-10s %-10s%n", "ProdID", "Name", "Quantity", "Price");
            System.out.println("--------------------------------------------------------------");
            System.out.printf("%-10s %-30s %-10s RM%-9.2f%n", bookToDelete.getProductID(), bookToDelete.getName(), bookToDelete.getQuantity(), bookToDelete.getPrice());
            System.out.print("Are you sure you want to delete this book? (yes/no): ");
            String confirmation = scanner.nextLine().trim();

            if (confirmation.equalsIgnoreCase("yes")) {
                books.remove(bookToDelete);
                System.out.println("Book deleted successfully.");
            } else {
                System.out.println("Book deletion canceled.");
            }
        } else {
            System.out.println("Book with ID " + productID + " not found.");
        }
    }

    private void deleteStationery(Scanner scanner) {
        System.out.print("Enter Stationery ID to delete: ");
        String productID = scanner.nextLine().trim();
        Stationery stationeryToDelete = findStationeryByID(productID);

        if (stationeryToDelete != null) {
            System.out.println("Product found:");
            System.out.println("-----------------------------------------------------------");
            System.out.printf("%-10s %-30s %-10s %-10s%n", "ProdID", "Name", "Quantity", "Price");
            System.out.println("-----------------------------------------------------------");
            System.out.printf("%-10s %-30s %-10s RM%-9.2f%n", stationeryToDelete.getProductID(), stationeryToDelete.getName(), stationeryToDelete.getQuantity(), stationeryToDelete.getPrice());
            System.out.print("Are you sure you want to delete this stationery? (yes/no): ");
            String confirmation = scanner.nextLine().trim();

            if (confirmation.equalsIgnoreCase("yes")) {
                stationeries.remove(stationeryToDelete);
                System.out.println("Stationery deleted successfully.");
            } else {
                System.out.println("Stationery deletion canceled.");
            }
        } else {
            System.out.println("Stationery with ID " + productID + " not found.");
        }
    }

    // View products
    public void viewProducts() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter view type (Books/Stationery/All): ");
        String viewType = scanner.nextLine().trim();

        switch (viewType.toLowerCase()) {
            case "books":
                System.out.println("---------------------------------------------------------------------------------");
                System.out.printf("%-10s %-30s %-10s %-10s %-10s%n", "ProdID", "Name", "Quantity", "Price", "Author");
                System.out.println("---------------------------------------------------------------------------------");
                viewBooks();
                break;
            case "stationery":
                System.out.println("---------------------------------------------------------------------------------");
                System.out.printf("%-10s %-30s %-10s %-10s %-10s%n", "ProdID", "Name", "Quantity", "Price", "Brand");
                System.out.println("---------------------------------------------------------------------------------");
                viewStationeries();
                break;
            case "all":
                System.out.println("---------------------------------------------------------------------------------");
                System.out.printf("%-10s %-30s %-10s %-10s %-10s%n", "ProdID", "Name", "Quantity", "Price", "Author/Brand");
                System.out.println("---------------------------------------------------------------------------------");
                viewAllProducts();
                break;
            default:
                System.out.println("Invalid view type.");
                break;
        }
    }

    private void viewBooks() {
        for (Book book : books) {
            System.out.printf("%-10s %-30s %-10s RM%-9.2f %-10s%n", book.getProductID(), book.getName(), book.getQuantity(), book.getPrice(), book.getAuthor());
        }
    }

    private void viewStationeries() {
        for (Stationery stationery : stationeries) {
            System.out.printf("%-10s %-30s %-10s RM%-9.2f %-10s%n", stationery.getProductID(), stationery.getName(), stationery.getQuantity(), stationery.getPrice(), stationery.getBrand());
        }
    }

    private void viewAllProducts() {
        viewBooks();
        viewStationeries();
    }
}
