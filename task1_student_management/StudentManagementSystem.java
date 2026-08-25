import java.io.*;
import java.util.*;

/**
 * Task 1 (Easy) - Student Management System
 * Auspify Technologies - Software Development Internship
 *
 * Features:
 *  - Add Student Records
 *  - Update Details
 *  - Delete Records
 *  - View Student Information
 *  - Data persisted to students.txt (CRUD survives restarts)
 */
public class StudentManagementSystem {

    static final String DATA_FILE = "students.txt";
    static List<Student> students = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadData();
        boolean running = true;

        System.out.println("=========================================");
        System.out.println(" STUDENT MANAGEMENT SYSTEM - Auspify Tech ");
        System.out.println("=========================================");

        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": addStudent(); break;
                case "2": viewAllStudents(); break;
                case "3": updateStudent(); break;
                case "4": deleteStudent(); break;
                case "5": searchStudent(); break;
                case "6":
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
        System.out.println("1. Add Student Record");
        System.out.println("2. View All Students");
        System.out.println("3. Update Student Details");
        System.out.println("4. Delete Student Record");
        System.out.println("5. Search Student by ID");
        System.out.println("6. Save & Exit");
        System.out.print("Enter your choice: ");
    }

    static void addStudent() {
        try {
            System.out.print("Enter Student ID: ");
            String id = sc.nextLine().trim();
            if (findById(id) != null) {
                System.out.println("Error: Student ID already exists.");
                return;
            }
            System.out.print("Enter Name: ");
            String name = sc.nextLine().trim();
            System.out.print("Enter Age: ");
            int age = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Enter Grade/Class: ");
            String grade = sc.nextLine().trim();
            System.out.print("Enter Email: ");
            String email = sc.nextLine().trim();

            students.add(new Student(id, name, age, grade, email));
            saveData();
            System.out.println("Student added successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Error: Age must be a number.");
        }
    }

    static void viewAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }
        System.out.println("\n" + repeat("-", 90));
        System.out.printf("%-10s %-20s %-5s %-15s %-30s%n", "ID", "Name", "Age", "Grade", "Email");
        System.out.println(repeat("-", 90));
        for (Student s : students) {
            System.out.printf("%-10s %-20s %-5d %-15s %-30s%n",
                    s.id, s.name, s.age, s.grade, s.email);
        }
        System.out.println(repeat("-", 90));
        System.out.println("Total Students: " + students.size());
    }

    static void updateStudent() {
        System.out.print("Enter Student ID to update: ");
        String id = sc.nextLine().trim();
        Student s = findById(id);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        System.out.print("Enter new Name (leave blank to keep '" + s.name + "'): ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) s.name = name;

        System.out.print("Enter new Age (leave blank to keep '" + s.age + "'): ");
        String ageStr = sc.nextLine().trim();
        if (!ageStr.isEmpty()) {
            try { s.age = Integer.parseInt(ageStr); }
            catch (NumberFormatException e) { System.out.println("Invalid age, keeping old value."); }
        }

        System.out.print("Enter new Grade (leave blank to keep '" + s.grade + "'): ");
        String grade = sc.nextLine().trim();
        if (!grade.isEmpty()) s.grade = grade;

        System.out.print("Enter new Email (leave blank to keep '" + s.email + "'): ");
        String email = sc.nextLine().trim();
        if (!email.isEmpty()) s.email = email;

        saveData();
        System.out.println("Student updated successfully!");
    }

    static void deleteStudent() {
        System.out.print("Enter Student ID to delete: ");
        String id = sc.nextLine().trim();
        Student s = findById(id);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        students.remove(s);
        saveData();
        System.out.println("Student deleted successfully!");
    }

    static void searchStudent() {
        System.out.print("Enter Student ID to search: ");
        String id = sc.nextLine().trim();
        Student s = findById(id);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        System.out.println("\nStudent Found:");
        System.out.println("ID: " + s.id);
        System.out.println("Name: " + s.name);
        System.out.println("Age: " + s.age);
        System.out.println("Grade: " + s.grade);
        System.out.println("Email: " + s.email);
    }

    static Student findById(String id) {
        for (Student s : students) {
            if (s.id.equalsIgnoreCase(id)) return s;
        }
        return null;
    }

    static void saveData() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Student s : students) {
                pw.println(s.id + "|" + s.name + "|" + s.age + "|" + s.grade + "|" + s.email);
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
                String[] parts = line.split("\\|", -1);
                if (parts.length == 5) {
                    students.add(new Student(parts[0], parts[1],
                            Integer.parseInt(parts[2]), parts[3], parts[4]));
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

    static class Student {
        String id, name, grade, email;
        int age;

        Student(String id, String name, int age, String grade, String email) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.grade = grade;
            this.email = email;
        }
    }
}
