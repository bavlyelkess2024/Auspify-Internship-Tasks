import java.io.*;
import java.util.*;

/**
 * Task 3 (Medium) - Inventory Management System
 * Auspify Technologies - Software Development Internship
 *
 * Features:
 *  - Inventory Tracking
 *  - Stock Updates (add/remove stock)
 *  - Search & Filter (by name/category, low-stock)
 *  - Reports Dashboard
 */
public class InventoryManagementSystem {

    static final String DATA_FILE = "inventory.txt";
    static final int LOW_STOCK_THRESHOLD = 5;
    static List<Item> items = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadData();
        boolean running = true;

        System.out.println("===========================================");
        System.out.println(" INVENTORY MANAGEMENT SYSTEM - Auspify Tech ");
        System.out.println("===========================================");

        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": addItem(); break;
                case "2": viewAllItems(); break;
                case "3": updateStock(); break;
                case "4": deleteItem(); break;
                case "5": searchAndFilter(); break;
                case "6": reportsDashboard(); break;
                case "7":
                    saveData();
                    System.out.println("Data saved. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.\n");
            }
        }
        sc.close();
    }

    static void printMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1. Add Inventory Item");
        System.out.println("2. View All Items");
        System.out.println("3. Update Stock (add/remove quantity)");
        System.out.println("4. Delete Item");
        System.out.println("5. Search & Filter");
        System.out.println("6. Reports Dashboard");
        System.out.println("7. Save & Exit");
        System.out.print("Enter your choice: ");
    }

    static void addItem() {
        System.out.print("Enter Item ID: ");
        String id = sc.nextLine().trim();
        if (findById(id) != null) {
            System.out.println("Error: Item ID already exists.");
            return;
        }
        System.out.print("Enter Item Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter Category: ");
        String category = sc.nextLine().trim();
        int qty; double price;
        try {
            System.out.print("Enter Quantity: ");
            qty = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Enter Unit Price: ");
            price = Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number entered. Item not added.");
            return;
        }
        items.add(new Item(id, name, category, qty, price));
        saveData();
        System.out.println("Item added successfully!");
    }

    static void viewAllItems() {
        if (items.isEmpty()) {
            System.out.println("No inventory items found.");
            return;
        }
        System.out.println("\n" + repeat("-", 100));
        System.out.printf("%-8s %-20s %-15s %-10s %-10s %-10s%n",
                "ID", "Name", "Category", "Qty", "Price", "Status");
        System.out.println(repeat("-", 100));
        for (Item i : items) {
            String status = i.quantity <= LOW_STOCK_THRESHOLD ? "LOW STOCK" : "OK";
            System.out.printf("%-8s %-20s %-15s %-10d %-10.2f %-10s%n",
                    i.id, i.name, i.category, i.quantity, i.price, status);
        }
        System.out.println(repeat("-", 100));
        System.out.println("Total Items: " + items.size());
    }

    static void updateStock() {
        System.out.print("Enter Item ID: ");
        String id = sc.nextLine().trim();
        Item item = findById(id);
        if (item == null) { System.out.println("Item not found."); return; }

        System.out.println("Current Quantity: " + item.quantity);
        System.out.print("Enter change in quantity (use negative to remove stock, e.g. -5): ");
        try {
            int delta = Integer.parseInt(sc.nextLine().trim());
            int newQty = item.quantity + delta;
            if (newQty < 0) {
                System.out.println("Error: Resulting quantity cannot be negative.");
                return;
            }
            item.quantity = newQty;
            saveData();
            System.out.println("Stock updated. New quantity: " + item.quantity);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }
    }

    static void deleteItem() {
        System.out.print("Enter Item ID to delete: ");
        String id = sc.nextLine().trim();
        Item item = findById(id);
        if (item == null) { System.out.println("Item not found."); return; }
        items.remove(item);
        saveData();
        System.out.println("Item deleted successfully!");
    }

    static void searchAndFilter() {
        System.out.println("\n1. Search by Name/Category keyword");
        System.out.println("2. Filter Low Stock Items");
        System.out.print("Choose option: ");
        String opt = sc.nextLine().trim();

        if (opt.equals("1")) {
            System.out.print("Enter keyword: ");
            String kw = sc.nextLine().trim().toLowerCase();
            List<Item> results = new ArrayList<>();
            for (Item i : items) {
                if (i.name.toLowerCase().contains(kw) || i.category.toLowerCase().contains(kw)) {
                    results.add(i);
                }
            }
            printItemList(results);
        } else if (opt.equals("2")) {
            List<Item> lowStock = new ArrayList<>();
            for (Item i : items) if (i.quantity <= LOW_STOCK_THRESHOLD) lowStock.add(i);
            printItemList(lowStock);
        } else {
            System.out.println("Invalid option.");
        }
    }

    static void printItemList(List<Item> list) {
        if (list.isEmpty()) {
            System.out.println("No items found.");
            return;
        }
        for (Item i : list) {
            System.out.println(i.id + " | " + i.name + " | " + i.category +
                    " | Qty: " + i.quantity + " | Price: " + i.price);
        }
    }

    static void reportsDashboard() {
        System.out.println("\n===== INVENTORY REPORT DASHBOARD =====");
        int totalItems = items.size();
        int totalQty = 0;
        double totalValue = 0;
        int lowStockCount = 0;
        Map<String, Integer> categoryCounts = new TreeMap<>();

        for (Item i : items) {
            totalQty += i.quantity;
            totalValue += i.quantity * i.price;
            if (i.quantity <= LOW_STOCK_THRESHOLD) lowStockCount++;
            categoryCounts.merge(i.category, 1, Integer::sum);
        }

        System.out.println("Total Distinct Items: " + totalItems);
        System.out.println("Total Units in Stock: " + totalQty);
        System.out.printf("Total Inventory Value: %.2f%n", totalValue);
        System.out.println("Low Stock Items (<= " + LOW_STOCK_THRESHOLD + "): " + lowStockCount);

        System.out.println("\nItems by Category:");
        for (Map.Entry<String, Integer> e : categoryCounts.entrySet()) {
            System.out.println("- " + e.getKey() + ": " + e.getValue());
        }

        if (lowStockCount > 0) {
            System.out.println("\nLow Stock Alerts:");
            for (Item i : items) {
                if (i.quantity <= LOW_STOCK_THRESHOLD) {
                    System.out.println("- " + i.name + " (Qty: " + i.quantity + ")");
                }
            }
        }
    }

    static Item findById(String id) {
        for (Item i : items) if (i.id.equalsIgnoreCase(id)) return i;
        return null;
    }

    static void saveData() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Item i : items) {
                pw.println(i.id + "|" + i.name + "|" + i.category + "|" + i.quantity + "|" + i.price);
            }
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    static void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length == 5) {
                    items.add(new Item(p[0], p[1], p[2], Integer.parseInt(p[3]), Double.parseDouble(p[4])));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    static String repeat(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(s);
        return sb.toString();
    }

    static class Item {
        String id, name, category;
        int quantity;
        double price;
        Item(String id, String name, String category, int quantity, double price) {
            this.id = id; this.name = name; this.category = category;
            this.quantity = quantity; this.price = price;
        }
    }
}
