import java.io.*;
import java.util.*;

/**
 * Task 2 (Easy) - Library Management System
 * Auspify Technologies - Software Development Internship
 *
 * Features:
 *  - Book Management (Add/Update/Delete)
 *  - Issue & Return Books
 *  - Search Functionality
 *  - Reports Generation
 */
public class LibraryManagementSystem {

    static final String BOOKS_FILE = "books.txt";
    static final String TRANSACTIONS_FILE = "transactions.txt";
    static List<Book> books = new ArrayList<>();
    static List<Transaction> transactions = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadBooks();
        loadTransactions();
        boolean running = true;

        System.out.println("=========================================");
        System.out.println(" LIBRARY MANAGEMENT SYSTEM - Auspify Tech ");
        System.out.println("=========================================");

        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": addBook(); break;
                case "2": viewAllBooks(); break;
                case "3": updateBook(); break;
                case "4": deleteBook(); break;
                case "5": issueBook(); break;
                case "6": returnBook(); break;
                case "7": searchBooks(); break;
                case "8": generateReport(); break;
                case "9":
                    saveBooks();
                    saveTransactions();
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
        System.out.println("1. Add Book");
        System.out.println("2. View All Books");
        System.out.println("3. Update Book");
        System.out.println("4. Delete Book");
        System.out.println("5. Issue Book");
        System.out.println("6. Return Book");
        System.out.println("7. Search Books (by title/author)");
        System.out.println("8. Generate Reports");
        System.out.println("9. Save & Exit");
        System.out.print("Enter your choice: ");
    }

    static void addBook() {
        System.out.print("Enter Book ID: ");
        String id = sc.nextLine().trim();
        if (findBookById(id) != null) {
            System.out.println("Error: Book ID already exists.");
            return;
        }
        System.out.print("Enter Title: ");
        String title = sc.nextLine().trim();
        System.out.print("Enter Author: ");
        String author = sc.nextLine().trim();
        int qty;
        try {
            System.out.print("Enter Quantity: ");
            qty = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity. Defaulting to 1.");
            qty = 1;
        }
        books.add(new Book(id, title, author, qty, qty));
        saveBooks();
        System.out.println("Book added successfully!");
    }

    static void viewAllBooks() {
        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }
        System.out.println("\n" + repeat("-", 95));
        System.out.printf("%-8s %-25s %-18s %-8s %-10s%n", "ID", "Title", "Author", "Total", "Available");
        System.out.println(repeat("-", 95));
        for (Book b : books) {
            System.out.printf("%-8s %-25s %-18s %-8d %-10d%n",
                    b.id, b.title, b.author, b.totalQty, b.availableQty);
        }
        System.out.println(repeat("-", 95));
        System.out.println("Total Titles: " + books.size());
    }

    static void updateBook() {
        System.out.print("Enter Book ID to update: ");
        String id = sc.nextLine().trim();
        Book b = findBookById(id);
        if (b == null) { System.out.println("Book not found."); return; }

        System.out.print("Enter new Title (leave blank to keep '" + b.title + "'): ");
        String title = sc.nextLine().trim();
        if (!title.isEmpty()) b.title = title;

        System.out.print("Enter new Author (leave blank to keep '" + b.author + "'): ");
        String author = sc.nextLine().trim();
        if (!author.isEmpty()) b.author = author;

        System.out.print("Enter new Total Quantity (leave blank to keep '" + b.totalQty + "'): ");
        String qtyStr = sc.nextLine().trim();
        if (!qtyStr.isEmpty()) {
            try {
                int newTotal = Integer.parseInt(qtyStr);
                int diff = newTotal - b.totalQty;
                b.totalQty = newTotal;
                b.availableQty = Math.max(0, b.availableQty + diff);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, keeping old value.");
            }
        }
        saveBooks();
        System.out.println("Book updated successfully!");
    }

    static void deleteBook() {
        System.out.print("Enter Book ID to delete: ");
        String id = sc.nextLine().trim();
        Book b = findBookById(id);
        if (b == null) { System.out.println("Book not found."); return; }
        books.remove(b);
        saveBooks();
        System.out.println("Book deleted successfully!");
    }

    static void issueBook() {
        System.out.print("Enter Book ID to issue: ");
        String id = sc.nextLine().trim();
        Book b = findBookById(id);
        if (b == null) { System.out.println("Book not found."); return; }
        if (b.availableQty <= 0) {
            System.out.println("No copies available for issue.");
            return;
        }
        System.out.print("Enter Member Name: ");
        String member = sc.nextLine().trim();
        b.availableQty--;
        transactions.add(new Transaction(id, b.title, member, "ISSUED"));
        saveBooks();
        saveTransactions();
        System.out.println("Book issued to " + member + " successfully!");
    }

    static void returnBook() {
        System.out.print("Enter Book ID to return: ");
        String id = sc.nextLine().trim();
        Book b = findBookById(id);
        if (b == null) { System.out.println("Book not found."); return; }
        System.out.print("Enter Member Name: ");
        String member = sc.nextLine().trim();

        Transaction openTxn = null;
        for (Transaction t : transactions) {
            if (t.bookId.equals(id) && t.member.equalsIgnoreCase(member) && t.status.equals("ISSUED")) {
                openTxn = t;
                break;
            }
        }
        if (openTxn == null) {
            System.out.println("No matching active issue record found for this member/book.");
            return;
        }
        openTxn.status = "RETURNED";
        if (b.availableQty < b.totalQty) b.availableQty++;
        saveBooks();
        saveTransactions();
        System.out.println("Book returned successfully!");
    }

    static void searchBooks() {
        System.out.print("Enter title or author keyword: ");
        String keyword = sc.nextLine().trim().toLowerCase();
        List<Book> results = new ArrayList<>();
        for (Book b : books) {
            if (b.title.toLowerCase().contains(keyword) || b.author.toLowerCase().contains(keyword)) {
                results.add(b);
            }
        }
        if (results.isEmpty()) {
            System.out.println("No matching books found.");
            return;
        }
        System.out.println("\nSearch Results:");
        for (Book b : results) {
            System.out.println(b.id + " | " + b.title + " by " + b.author +
                    " | Available: " + b.availableQty + "/" + b.totalQty);
        }
    }

    static void generateReport() {
        System.out.println("\n===== LIBRARY REPORT =====");
        int totalTitles = books.size();
        int totalCopies = 0, totalAvailable = 0;
        for (Book b : books) {
            totalCopies += b.totalQty;
            totalAvailable += b.availableQty;
        }
        int issuedCount = 0;
        for (Transaction t : transactions) if (t.status.equals("ISSUED")) issuedCount++;

        System.out.println("Total Titles: " + totalTitles);
        System.out.println("Total Copies: " + totalCopies);
        System.out.println("Available Copies: " + totalAvailable);
        System.out.println("Currently Issued: " + issuedCount);

        System.out.println("\nCurrently Issued Books:");
        boolean any = false;
        for (Transaction t : transactions) {
            if (t.status.equals("ISSUED")) {
                System.out.println("- " + t.title + " -> " + t.member);
                any = true;
            }
        }
        if (!any) System.out.println("(none)");
    }

    static Book findBookById(String id) {
        for (Book b : books) if (b.id.equalsIgnoreCase(id)) return b;
        return null;
    }

    static void saveBooks() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BOOKS_FILE))) {
            for (Book b : books) {
                pw.println(b.id + "|" + b.title + "|" + b.author + "|" + b.totalQty + "|" + b.availableQty);
            }
        } catch (IOException e) {
            System.out.println("Error saving books: " + e.getMessage());
        }
    }

    static void loadBooks() {
        File file = new File(BOOKS_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length == 5) {
                    books.add(new Book(p[0], p[1], p[2], Integer.parseInt(p[3]), Integer.parseInt(p[4])));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading books: " + e.getMessage());
        }
    }

    static void saveTransactions() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(TRANSACTIONS_FILE))) {
            for (Transaction t : transactions) {
                pw.println(t.bookId + "|" + t.title + "|" + t.member + "|" + t.status);
            }
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }

    static void loadTransactions() {
        File file = new File(TRANSACTIONS_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length == 4) {
                    transactions.add(new Transaction(p[0], p[1], p[2], p[3]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
    }

    static String repeat(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(s);
        return sb.toString();
    }

    static class Book {
        String id, title, author;
        int totalQty, availableQty;
        Book(String id, String title, String author, int totalQty, int availableQty) {
            this.id = id; this.title = title; this.author = author;
            this.totalQty = totalQty; this.availableQty = availableQty;
        }
    }

    static class Transaction {
        String bookId, title, member, status;
        Transaction(String bookId, String title, String member, String status) {
            this.bookId = bookId; this.title = title; this.member = member; this.status = status;
        }
    }
}
