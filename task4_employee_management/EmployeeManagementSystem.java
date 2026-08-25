import java.io.*;
import java.util.*;

/**
 * Task 4 (Medium) - Employee Management System
 * Auspify Technologies - Software Development Internship
 *
 * Features:
 *  - Employee Records
 *  - Department Management
 *  - Search Employees
 *  - Reports Generation
 */
public class EmployeeManagementSystem {

    static final String DATA_FILE = "employees.txt";
    static List<Employee> employees = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadData();
        boolean running = true;

        System.out.println("===========================================");
        System.out.println(" EMPLOYEE MANAGEMENT SYSTEM - Auspify Tech ");
        System.out.println("===========================================");

        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": addEmployee(); break;
                case "2": viewAllEmployees(); break;
                case "3": updateEmployee(); break;
                case "4": deleteEmployee(); break;
                case "5": searchByDepartment(); break;
                case "6": searchByName(); break;
                case "7": generateReports(); break;
                case "8":
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
        System.out.println("1. Add Employee");
        System.out.println("2. View All Employees");
        System.out.println("3. Update Employee");
        System.out.println("4. Delete Employee");
        System.out.println("5. Search by Department");
        System.out.println("6. Search by Name");
        System.out.println("7. Generate Reports");
        System.out.println("8. Save & Exit");
        System.out.print("Enter your choice: ");
    }

    static void addEmployee() {
        System.out.print("Enter Employee ID: ");
        String id = sc.nextLine().trim();
        if (findById(id) != null) {
            System.out.println("Error: Employee ID already exists.");
            return;
        }
        System.out.print("Enter Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter Department: ");
        String dept = sc.nextLine().trim();
        System.out.print("Enter Position: ");
        String position = sc.nextLine().trim();
        double salary;
        try {
            System.out.print("Enter Salary: ");
            salary = Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid salary. Employee not added.");
            return;
        }
        employees.add(new Employee(id, name, dept, position, salary));
        saveData();
        System.out.println("Employee added successfully!");
    }

    static void viewAllEmployees() {
        if (employees.isEmpty()) {
            System.out.println("No employee records found.");
            return;
        }
        System.out.println("\n" + repeat("-", 100));
        System.out.printf("%-8s %-20s %-15s %-18s %-10s%n", "ID", "Name", "Department", "Position", "Salary");
        System.out.println(repeat("-", 100));
        for (Employee e : employees) {
            System.out.printf("%-8s %-20s %-15s %-18s %-10.2f%n",
                    e.id, e.name, e.department, e.position, e.salary);
        }
        System.out.println(repeat("-", 100));
        System.out.println("Total Employees: " + employees.size());
    }

    static void updateEmployee() {
        System.out.print("Enter Employee ID to update: ");
        String id = sc.nextLine().trim();
        Employee e = findById(id);
        if (e == null) { System.out.println("Employee not found."); return; }

        System.out.print("Enter new Name (leave blank to keep '" + e.name + "'): ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) e.name = name;

        System.out.print("Enter new Department (leave blank to keep '" + e.department + "'): ");
        String dept = sc.nextLine().trim();
        if (!dept.isEmpty()) e.department = dept;

        System.out.print("Enter new Position (leave blank to keep '" + e.position + "'): ");
        String pos = sc.nextLine().trim();
        if (!pos.isEmpty()) e.position = pos;

        System.out.print("Enter new Salary (leave blank to keep '" + e.salary + "'): ");
        String salStr = sc.nextLine().trim();
        if (!salStr.isEmpty()) {
            try { e.salary = Double.parseDouble(salStr); }
            catch (NumberFormatException ex) { System.out.println("Invalid salary, keeping old value."); }
        }
        saveData();
        System.out.println("Employee updated successfully!");
    }

    static void deleteEmployee() {
        System.out.print("Enter Employee ID to delete: ");
        String id = sc.nextLine().trim();
        Employee e = findById(id);
        if (e == null) { System.out.println("Employee not found."); return; }
        employees.remove(e);
        saveData();
        System.out.println("Employee deleted successfully!");
    }

    static void searchByDepartment() {
        System.out.print("Enter Department: ");
        String dept = sc.nextLine().trim().toLowerCase();
        List<Employee> results = new ArrayList<>();
        for (Employee e : employees) {
            if (e.department.toLowerCase().contains(dept)) results.add(e);
        }
        printEmployeeList(results);
    }

    static void searchByName() {
        System.out.print("Enter Name keyword: ");
        String kw = sc.nextLine().trim().toLowerCase();
        List<Employee> results = new ArrayList<>();
        for (Employee e : employees) {
            if (e.name.toLowerCase().contains(kw)) results.add(e);
        }
        printEmployeeList(results);
    }

    static void printEmployeeList(List<Employee> list) {
        if (list.isEmpty()) {
            System.out.println("No matching employees found.");
            return;
        }
        for (Employee e : list) {
            System.out.println(e.id + " | " + e.name + " | " + e.department +
                    " | " + e.position + " | Salary: " + e.salary);
        }
    }

    static void generateReports() {
        System.out.println("\n===== EMPLOYEE REPORT =====");
        if (employees.isEmpty()) {
            System.out.println("No data available.");
            return;
        }
        double totalSalary = 0;
        Map<String, Integer> deptCounts = new TreeMap<>();
        Map<String, Double> deptSalary = new TreeMap<>();

        for (Employee e : employees) {
            totalSalary += e.salary;
            deptCounts.merge(e.department, 1, Integer::sum);
            deptSalary.merge(e.department, e.salary, Double::sum);
        }

        System.out.println("Total Employees: " + employees.size());
        System.out.printf("Total Salary Payout: %.2f%n", totalSalary);
        System.out.printf("Average Salary: %.2f%n", totalSalary / employees.size());

        System.out.println("\nEmployees & Salary by Department:");
        for (String dept : deptCounts.keySet()) {
            System.out.printf("- %s: %d employee(s), Total Salary: %.2f%n",
                    dept, deptCounts.get(dept), deptSalary.get(dept));
        }
    }

    static Employee findById(String id) {
        for (Employee e : employees) if (e.id.equalsIgnoreCase(id)) return e;
        return null;
    }

    static void saveData() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Employee e : employees) {
                pw.println(e.id + "|" + e.name + "|" + e.department + "|" + e.position + "|" + e.salary);
            }
        } catch (IOException ex) {
            System.out.println("Error saving data: " + ex.getMessage());
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
                    employees.add(new Employee(p[0], p[1], p[2], p[3], Double.parseDouble(p[4])));
                }
            }
        } catch (IOException ex) {
            System.out.println("Error loading data: " + ex.getMessage());
        }
    }

    static String repeat(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(s);
        return sb.toString();
    }

    static class Employee {
        String id, name, department, position;
        double salary;
        Employee(String id, String name, String department, String position, double salary) {
            this.id = id; this.name = name; this.department = department;
            this.position = position; this.salary = salary;
        }
    }
}
