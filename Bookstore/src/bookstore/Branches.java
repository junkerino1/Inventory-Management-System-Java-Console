package bookstore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Branches {
    private static final String BRANCHES_FILE = "branches.txt";
    private static List<Branches> branches = new ArrayList<>();
    
    private String branchID;
    private String branchName;
    private String branchAddress;
    
    // Constructor for Branches class
    public Branches(String branchID, String branchName, String branchAddress) {
        this.branchID = branchID;
        this.branchName = branchName;
        this.branchAddress = branchAddress;
    }

    // Load branches from the file
    public static void loadBranches() {
        branches.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(BRANCHES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    // Create a new Branches object and add it to the list
                    Branches branch = new Branches(parts[0], parts[1], parts[2]);
                    branches.add(branch);
                } else {
                    System.err.println("Invalid line format: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading branches file: " + e.getMessage());
        }
    }

    // Getters
    public String getBranchID() {
        return this.branchID;
    }

    public String getBranchName() {
        return this.branchName;
    }

    public String getBranchAddress() {
        return this.branchAddress;
    }

    public static String getBranches(String branchName) {
        for (Branches branch : branches) {
            if (branch.getBranchName().equals(branchName)) {
                return branch.getBranchAddress();
            }
        }
        return null; // or throw an exception if branch not found
    }
    
    // Setters
    public void setBranchID(String branchID) {
        this.branchID = branchID;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

    // Return list of Branches objects
    public static List<Branches> getBranches() {
        return branches;
    }

    // Check if the branch name is valid
    public static boolean isValidBranchName(String branchName) {
        for (Branches branch : branches) {
            if (branch.getBranchName().equalsIgnoreCase(branchName)) {
                return true;
            }
        }
        return false;
    }
}
