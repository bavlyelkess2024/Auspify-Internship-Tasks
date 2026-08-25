import java.io.*;
import java.util.*;

/**
 * Task 5 (Advanced) - Online Examination System
 * Auspify Technologies - Software Development Internship
 *
 * Features:
 *  - User Login (Admin & Student, with Student registration)
 *  - Online Tests (multiple-choice questions managed by Admin)
 *  - Automated Results (auto-grading)
 *  - Performance Reports
 */
public class OnlineExaminationSystem {

    static final String USERS_FILE = "users.txt";
    static final String QUESTIONS_FILE = "questions.txt";
    static final String RESULTS_FILE = "results.txt";
    static final String ADMIN_USERNAME = "admin";
    static final String ADMIN_PASSWORD = "admin123";

    static List<User> users = new ArrayList<>();
    static List<Question> questions = new ArrayList<>();
    static List<ResultRecord> results = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadUsers();
        loadQuestions();
        loadResults();

        System.out.println("===========================================");
        System.out.println(" ONLINE EXAMINATION SYSTEM - Auspify Tech  ");
        System.out.println("===========================================");

        boolean running = true;
        while (running) {
            System.out.println("\n1. Login as Admin");
            System.out.println("2. Login as Student");
            System.out.println("3. Register as Student");
            System.out.println("4. Exit");
            System.out.print("Choose option: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": adminLogin(); break;
                case "2": studentLogin(); break;
                case "3": registerStudent(); break;
                case "4":
                    saveAll();
                    System.out.println("Data saved. Goodbye!");
                    running = false;
                    break;
                default: System.out.println("Invalid choice.");
            }
        }
        sc.close();
    }

    // ---------------- AUTH ----------------

    static void registerStudent() {
        System.out.print("Choose Username: ");
        String username = sc.nextLine().trim();
        if (findUser(username) != null) {
            System.out.println("Username already taken.");
            return;
        }
        System.out.print("Choose Password: ");
        String password = sc.nextLine().trim();
        users.add(new User(username, password));
        saveUsers();
        System.out.println("Registration successful! You can now log in.");
    }

    static void adminLogin() {
        System.out.print("Admin Username: ");
        String u = sc.nextLine().trim();
        System.out.print("Admin Password: ");
        String p = sc.nextLine().trim();
        if (u.equals(ADMIN_USERNAME) && p.equals(ADMIN_PASSWORD)) {
            System.out.println("Admin login successful!");
            adminMenu();
        } else {
            System.out.println("Invalid admin credentials.");
        }
    }

    static void studentLogin() {
        System.out.print("Username: ");
        String u = sc.nextLine().trim();
        System.out.print("Password: ");
        String p = sc.nextLine().trim();
        User user = findUser(u);
        if (user == null || !user.password.equals(p)) {
            System.out.println("Invalid credentials.");
            return;
        }
        System.out.println("Login successful! Welcome, " + u);
        studentMenu(user);
    }

    static User findUser(String username) {
        for (User u : users) if (u.username.equalsIgnoreCase(username)) return u;
        return null;
    }

    // ---------------- ADMIN ----------------

    static void adminMenu() {
        boolean inAdmin = true;
        while (inAdmin) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. Add Question");
            System.out.println("2. View All Questions");
            System.out.println("3. Delete Question");
            System.out.println("4. View Performance Reports (All Students)");
            System.out.println("5. Logout");
            System.out.print("Choose option: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": addQuestion(); break;
                case "2": viewQuestions(); break;
                case "3": deleteQuestion(); break;
                case "4": performanceReport(); break;
                case "5": inAdmin = false; break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    static void addQuestion() {
        System.out.print("Enter Question ID: ");
        String id = sc.nextLine().trim();
        System.out.print("Enter Question Text: ");
        String text = sc.nextLine().trim();
        System.out.print("Option A: ");
        String a = sc.nextLine().trim();
        System.out.print("Option B: ");
        String b = sc.nextLine().trim();
        System.out.print("Option C: ");
        String c = sc.nextLine().trim();
        System.out.print("Option D: ");
        String d = sc.nextLine().trim();
        System.out.print("Correct Answer (A/B/C/D): ");
        String correct = sc.nextLine().trim().toUpperCase();
        if (!Arrays.asList("A", "B", "C", "D").contains(correct)) {
            System.out.println("Invalid correct answer. Question not added.");
            return;
        }
        questions.add(new Question(id, text, a, b, c, d, correct));
        saveQuestions();
        System.out.println("Question added successfully!");
    }

    static void viewQuestions() {
        if (questions.isEmpty()) { System.out.println("No questions available."); return; }
        for (Question q : questions) {
            System.out.println("\n[" + q.id + "] " + q.text);
            System.out.println("  A) " + q.optA + "   B) " + q.optB);
            System.out.println("  C) " + q.optC + "   D) " + q.optD);
            System.out.println("  Correct: " + q.correct);
        }
    }

    static void deleteQuestion() {
        System.out.print("Enter Question ID to delete: ");
        String id = sc.nextLine().trim();
        Question q = findQuestion(id);
        if (q == null) { System.out.println("Question not found."); return; }
        questions.remove(q);
        saveQuestions();
        System.out.println("Question deleted.");
    }

    static Question findQuestion(String id) {
        for (Question q : questions) if (q.id.equalsIgnoreCase(id)) return q;
        return null;
    }

    static void performanceReport() {
        if (results.isEmpty()) { System.out.println("No exam attempts yet."); return; }
        System.out.println("\n===== PERFORMANCE REPORT (ALL STUDENTS) =====");
        System.out.printf("%-15s %-10s %-10s %-10s%n", "Username", "Score", "Total", "Percent");
        for (ResultRecord r : results) {
            double pct = r.total == 0 ? 0 : (r.score * 100.0 / r.total);
            System.out.printf("%-15s %-10d %-10d %-10.1f%n", r.username, r.score, r.total, pct);
        }
    }

    // ---------------- STUDENT ----------------

    static void studentMenu(User user) {
        boolean inStudent = true;
        while (inStudent) {
            System.out.println("\n--- STUDENT MENU ---");
            System.out.println("1. Take Exam");
            System.out.println("2. View My Past Results");
            System.out.println("3. Logout");
            System.out.print("Choose option: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": takeExam(user); break;
                case "2": viewMyResults(user); break;
                case "3": inStudent = false; break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    static void takeExam(User user) {
        if (questions.isEmpty()) {
            System.out.println("No exam questions available yet. Please check back later.");
            return;
        }
        System.out.println("\n===== EXAM STARTED (" + questions.size() + " questions) =====");
        int score = 0;
        for (Question q : questions) {
            System.out.println("\n" + q.text);
            System.out.println("  A) " + q.optA);
            System.out.println("  B) " + q.optB);
            System.out.println("  C) " + q.optC);
            System.out.println("  D) " + q.optD);
            System.out.print("Your answer (A/B/C/D): ");
            String ans = sc.nextLine().trim().toUpperCase();
            if (ans.equals(q.correct)) {
                score++;
            }
        }
        int total = questions.size();
        double pct = (score * 100.0) / total;
        System.out.println("\n===== EXAM COMPLETE =====");
        System.out.println("Score: " + score + "/" + total + " (" + String.format("%.1f", pct) + "%)");

        results.add(new ResultRecord(user.username, score, total));
        saveResults();
    }

    static void viewMyResults(User user) {
        boolean any = false;
        System.out.println("\n===== YOUR EXAM HISTORY =====");
        for (ResultRecord r : results) {
            if (r.username.equalsIgnoreCase(user.username)) {
                double pct = r.total == 0 ? 0 : (r.score * 100.0 / r.total);
                System.out.printf("Score: %d/%d (%.1f%%)%n", r.score, r.total, pct);
                any = true;
            }
        }
        if (!any) System.out.println("No attempts yet.");
    }

    // ---------------- PERSISTENCE ----------------

    static void saveAll() {
        saveUsers();
        saveQuestions();
        saveResults();
    }

    static void saveUsers() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User u : users) pw.println(u.username + "|" + u.password);
        } catch (IOException e) { System.out.println("Error saving users: " + e.getMessage()); }
    }

    static void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length == 2) users.add(new User(p[0], p[1]));
            }
        } catch (IOException e) { System.out.println("Error loading users: " + e.getMessage()); }
    }

    static void saveQuestions() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(QUESTIONS_FILE))) {
            for (Question q : questions) {
                pw.println(q.id + "|" + q.text + "|" + q.optA + "|" + q.optB + "|" +
                        q.optC + "|" + q.optD + "|" + q.correct);
            }
        } catch (IOException e) { System.out.println("Error saving questions: " + e.getMessage()); }
    }

    static void loadQuestions() {
        File file = new File(QUESTIONS_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length == 7) {
                    questions.add(new Question(p[0], p[1], p[2], p[3], p[4], p[5], p[6]));
                }
            }
        } catch (IOException e) { System.out.println("Error loading questions: " + e.getMessage()); }
    }

    static void saveResults() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(RESULTS_FILE))) {
            for (ResultRecord r : results) pw.println(r.username + "|" + r.score + "|" + r.total);
        } catch (IOException e) { System.out.println("Error saving results: " + e.getMessage()); }
    }

    static void loadResults() {
        File file = new File(RESULTS_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length == 3) {
                    results.add(new ResultRecord(p[0], Integer.parseInt(p[1]), Integer.parseInt(p[2])));
                }
            }
        } catch (IOException e) { System.out.println("Error loading results: " + e.getMessage()); }
    }

    // ---------------- MODELS ----------------

    static class User {
        String username, password;
        User(String username, String password) { this.username = username; this.password = password; }
    }

    static class Question {
        String id, text, optA, optB, optC, optD, correct;
        Question(String id, String text, String optA, String optB, String optC, String optD, String correct) {
            this.id = id; this.text = text; this.optA = optA; this.optB = optB;
            this.optC = optC; this.optD = optD; this.correct = correct;
        }
    }

    static class ResultRecord {
        String username;
        int score, total;
        ResultRecord(String username, int score, int total) {
            this.username = username; this.score = score; this.total = total;
        }
    }
}
