# Auspify Technologies — Software Development Internship
## Task List Submission (Tasks 1–6)

Each task is a self-contained Java console application with CRUD operations
and file-based data persistence (no external database/driver required).

| # | Task | Folder | Main Class |
|---|------|--------|------------|
| 1 | Student Management System (Easy) | task1_student_management | StudentManagementSystem |
| 2 | Library Management System (Easy) | task2_library_management | LibraryManagementSystem |
| 3 | Inventory Management System (Medium) | task3_inventory_management | InventoryManagementSystem |
| 4 | Employee Management System (Medium) | task4_employee_management | EmployeeManagementSystem |
| 5 | Online Examination System (Advanced) | task5_online_examination | OnlineExaminationSystem |
| 6 | Task Management App (Advanced) | task6_task_management | TaskManagementApp |

## How to Compile & Run

Each folder is independent. From inside a task folder:

```bash
javac <MainClassName>.java
java <MainClassName>
```

Example (Task 1):
```bash
cd task1_student_management
javac StudentManagementSystem.java
java StudentManagementSystem
```

Requires Java 11+ (Task 6 uses `java.time`).

## Notes on Task 5 & 6

The original PDF task list had the workflow/skills content for Task 5
("Online Examination System") and Task 6 ("Task Management App") swapped
(Task 5's listed steps described a product-catalog app, and Task 6's
described an exam system). These two apps were built to match their
**actual titles**, since that best reflects the internship's intent:

- **Task 5** — full exam system: student registration/login, admin login,
  admin-managed multiple-choice questions, auto-graded exam attempts, and
  performance reports.
- **Task 6** — full task/productivity manager: CRUD tasks with deadlines
  and priority, mark-complete, filter by status/priority, sort by deadline,
  and due-soon/overdue alerts.

## Data Persistence

Each app saves its data to a plain-text file (e.g. `students.txt`,
`books.txt`) in the same folder using a simple pipe-delimited format —
data survives between runs without needing a database server.

## Skills Demonstrated (per Internship's Skills List)

- CRUD Operations & Data Management (Task 1, 2)
- Database Integration / Software Architecture (Task 2)
- Business Logic Development / Data Analysis (Task 3)
- Software Development Lifecycle / Performance Optimization (Task 4)
- Authentication Systems / Data Validation (Task 5)
- Secure input handling, sorting/filtering, and system integration (Task 6)

## Submission Checklist (per Auspify guidelines)

- [x] Project Source Code — included in each task folder
- [ ] Screenshots of Completed Tasks — capture console runs when submitting
- [ ] GitHub Repository Link — push this folder to a public repo (optional)
- [ ] Project Demonstration Video — optional
