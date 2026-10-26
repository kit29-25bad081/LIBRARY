# Digital Library Management System

A complete college academic desktop application built with **Java Swing**, **Core Java**, **JDBC**, and **MySQL**.

---

## 1. Project Introduction

The **Digital Library Management System** is a full-featured Java desktop application designed for educational institutions, colleges, and libraries. It streamlines library catalog administration, student/member management, multi-criteria e-book searching, 14-day borrowing issuance, and return handling with atomic transaction management.

The system adheres strictly to a 4-tier desktop enterprise architecture:
- **Presentation Layer**: Java Swing Desktop GUI (JFrame, JPanel, JTable, modern styling)
- **Service Layer**: Core Java business logic, validation, and transaction coordination
- **Data Access Layer (DAO)**: Pure JDBC database access using `PreparedStatement`
- **Database Layer**: MySQL Relational Database (`digital_library`)

> **Note**: This application is a 100% native Java desktop GUI window application. It does not use web frameworks, HTML/CSS/JavaScript, React, Node.js, Spring Boot, or local web servers.

---

## 2. Technologies Used

| Layer / Component | Technology | Version / Specification |
|---|---|---|
| **Frontend GUI** | Java Swing & AWT | Standard Java SE (JFrame, JTable, Layout Managers) |
| **Backend Logic** | Core Java | JDK 17+ (Temurin / OpenJDK / Oracle JDK) |
| **Database** | MySQL Server | MySQL 8.x / MariaDB |
| **Connectivity** | JDBC (Java Database Connectivity) | MySQL Connector/J 8.3.0 |
| **Build & Management** | Apache Maven | Maven 3.9+ |
| **Operating System** | Windows / Linux / macOS | Desktop Platform |

---

## 3. Project Architecture

The application strictly enforces separation of concerns. The GUI **never** contains direct SQL statements:

```
+-------------------------------------------------------------------+
|                        JAVA SWING FRONTEND                        |
|   (LoginFrame, DashboardFrame, UserManagementFrame,               |
|    EbookManagementFrame, SearchFrame, IssueBookFrame,             |
|    ReturnBookFrame, IssueHistoryFrame)                            |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                         SERVICE LAYER                             |
|   (AuthService, UserService, EbookService, IssueService)          |
|   - Business rules & constraints                                  |
|   - Data validation (unique emails, unique ISBN, phone format)    |
|   - 14-day due date calculation                                   |
|   - Atomic JDBC Transactions (commit & rollback)                  |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                       DATA ACCESS LAYER (DAO)                     |
|   (AdminDAO, UserDAO, EbookDAO, IssueDAO)                         |
|   - Parameterized SQL execution via PreparedStatement             |
|   - ResultSet mapping to Model objects                            |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                             JDBC LAYER                            |
|   (DBConnection.java using MySQL Connector/J Driver)              |
|   - Try-with-resources connection management                      |
|   - SQLException handling                                         |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                          MYSQL DATABASE                           |
|   (`digital_library`: admins, users, ebooks, issue_records)       |
+-------------------------------------------------------------------+
```

---

## 4. JDK Requirement

- **Requirement**: **JDK 17 or later** (Java 17 LTS, Java 21, etc.).
- Verify your Java version by opening a terminal / command prompt:
  ```bash
  java -version
  javac -version
  ```
  Expected output: `openjdk version "17..."` or later.

---

## 5. MySQL Installation & Database Setup

### Step 5.1: Install MySQL Server
If MySQL is not installed on your system:
- Download and install **MySQL Community Server (8.x)** from [mysql.com](https://dev.mysql.com/downloads/installer/) or install via **XAMPP / WampServer**.
- Alternatively on Windows using winget:
  ```powershell
  winget install Oracle.MySQL
  ```

### Step 5.2: Execute `database.sql`
You can import the database script in any of the following ways:

#### Option A: Using MySQL Workbench
1. Open **MySQL Workbench** and connect to your local MySQL instance.
2. Go to **File -> Open SQL Script...** and select `database.sql` from this project folder.
3. Click the **Execute** lightning bolt icon (or press `Ctrl + Shift + Enter`).
4. Refresh the schema navigator; you will see `digital_library` with the four tables and sample data.

`database.sql` creates only missing tables and preserves existing table data. Its sample records are inserted only when their keys do not already exist; the default `admin` password is set to `admin123`.

#### Option B: Using MySQL Command Line Client
```bash
mysql -u root -p < database.sql
```

#### Option C: Using phpMyAdmin (if using XAMPP/WAMP)
1. Open `http://localhost/phpmyadmin` in your browser.
2. Click **Import** tab.
3. Browse and select `database.sql`, then click **Import / Go**.

---

## 6. How to Change MySQL Username and Password

The database connection parameters are configurable through multiple simple methods:

### Method 1: Edit `db.properties` (Recommended - No Recompilation Needed)
Open the `db.properties` file located in the project root:
```properties
db.url=jdbc:mysql://localhost:3306/digital_library?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=your_mysql_password_here
```

### Method 2: Change Directly in the GUI (At Runtime)
When the application starts at the **Login Window**, click the link:
> **"Configure Database Connection"**

A dialog will appear allowing you to enter:
- MySQL Host (`localhost`)
- MySQL Port (`3306`)
- Database Name (`digital_library`)
- Username (e.g., `root`)
- Password (e.g., `root` or your custom password)

Click **OK**; the application tests the connection immediately and updates `db.properties`.

### Method 3: In Java Code (`DBConnection.java`)
Open [DBConnection.java](file:///src/com/digitallibrary/database/DBConnection.java):
```java
private static String dbUrl = "jdbc:mysql://localhost:3306/digital_library?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
private static String dbUser = "root";
private static String dbPassword = "root";
```

---

## 7. Default Login Credentials

| Role | Username | Password |
|---|---|---|
| **System Administrator** | `admin` | `admin123` |

*(Verified in MySQL table `admins` via JDBC)*

---

## 8. Sample Data Included in `database.sql`

- **Admins**: 1 default admin (`admin` / `admin123`)
- **Users**: 5 registered college students and faculty members (Aarav Sharma, Priya Patel, Rohan Verma, Sneha Mukherjee, Vikram Malhotra)
- **E-books**: 10 computer science & engineering textbooks with unique ISBNs:
  1. *Effective Java (3rd Edition)* - Joshua Bloch
  2. *Clean Code: A Handbook of Agile Software Craftsmanship* - Robert C. Martin
  3. *Introduction to Algorithms (4th Edition)* - Thomas H. Cormen (CLRS)
  4. *Design Patterns: Elements of Reusable Object-Oriented Software* - Gang of Four
  5. *Database System Concepts (7th Edition)* - Abraham Silberschatz
  6. *Operating System Concepts (10th Edition)* - Abraham Silberschatz
  7. *Computer Networking: A Top-Down Approach (8th Edition)* - James Kurose
  8. *Artificial Intelligence: A Modern Approach (4th Edition)* - Stuart Russell
  9. *Core Java Volume I--Fundamentals (12th Edition)* - Cay S. Horstmann
  10. *Head First Java: A Brain-Friendly Guide (3rd Edition)* - Kathy Sierra & Bert Bates

---

## 9. How to Compile and Run the Project

### Method 1: Using the Batch Files (Windows Quick Start)
- Double click **`compile.bat`** to build the application.
- Double click **`run.bat`** to launch the Java Swing GUI window.

### Method 2: Using Maven
Run the following command from the project root:
```bash
mvn clean compile exec:java
```

Or package and run the executable JAR:
```bash
mvn clean package -DskipTests
java -jar target/digital-library-management-system-1.0.0.jar
```

### Method 3: Running Directly via Java (Standard Classpath)
```bash
# Compile
javac -cp "lib/*" -d target/classes src/com/digitallibrary/**/*.java src/com/digitallibrary/*.java

# Run
java -cp "target/classes;lib/*" com.digitallibrary.Main
```

### Method 4: Running from IDE (VS Code / IntelliJ IDEA / Eclipse)
1. Open this folder in your IDE.
2. Navigate to `src/com/digitallibrary/Main.java`.
3. Click the **Run** button above `public static void main(String[] args)`.

---

## 10. Key Features and Modules

### 1. Admin Login (`LoginFrame.java`)
- Modern, centered dialog window.
- Validates credentials against MySQL `admins` table using `PreparedStatement`.
- Enter key navigation between Username -> Password -> Login.
- Embedded database connectivity test and configuration dialog.

### 2. Dashboard (`DashboardFrame.java`)
- Real-time stat cards displaying live database metrics:
  - **Total Users**
  - **Total E-books**
  - **Available E-books**
  - **Issued E-books**
- 8 Navigation module buttons with modern hover animations:
  - User Management
  - E-book Management
  - Search E-book
  - Issue E-book
  - Return E-book
  - Issue History
  - Logout
  - Exit

### 3. User Management (`UserManagementFrame.java`)
- Register new users, update user records, delete users.
- Live `JTable` view of all users with single-click selection to populate the editing form.
- Search users by Name, Email, or Phone.
- **Validations**: Name required, valid email syntax, email uniqueness, phone minimum digits.
- **Integrity**: Cannot delete users who currently hold active unreturned e-books.

### 4. E-book Catalog Management (`EbookManagementFrame.java`)
- Add, update, delete, and view digital publications.
- Fields: E-book ID, Title, Author, Category (dropdown), ISBN (unique), Publisher, Publication Year, URL, Available status.
- `JTable` catalog browser with auto-selection binding.

### 5. Multi-Criteria Catalog Search (`SearchFrame.java`)
- Query books by Title, Author, Category, and ISBN simultaneously.
- Checkbox to filter **"Show only available e-books"**.
- "Issue Selected Book" shortcut button to launch the Issue window with the chosen e-book ID prefilled.

### 6. Issue E-book (`IssueBookFrame.java`)
- Fields: User ID, E-book ID, Issue Date (today), Due Date (today + 14 days).
- Automatic calculation of exact 14-calendar-day borrowing period.
- Pre-validation verification:
  - User exists in MySQL
  - E-book exists in MySQL
  - E-book is available (`available == true`)
- **JDBC Atomic Transaction**:
  - `conn.setAutoCommit(false)`
  - Insert record into `issue_records`
  - Update `ebooks` availability to `FALSE`
  - `conn.commit()`
  - On failure: `conn.rollback()`

### 7. Return E-book (`ReturnBookFrame.java`)
- Enter Issue ID and click "Look Up Issue".
- Displays borrower name, email, book title, issue date, and due date.
- **Double Return Prevention**: Blocks returning any record already marked as `RETURNED`.
- **JDBC Atomic Transaction**:
  - `conn.setAutoCommit(false)`
  - Update `issue_records` with return date and status `RETURNED`
  - Update `ebooks` availability to `TRUE`
  - `conn.commit()`
  - On failure: `conn.rollback()`

### 8. Issue History & Audit Log (`IssueHistoryFrame.java`)
- Table displaying: Issue ID, User Name, E-book Title, Issue Date, Due Date, Return Date, Status.
- Filter options:
  - **All Records**
  - **Currently Issued (Active)**
  - **Returned**
- Dynamic Overdue tracking: highlights in red if today's date has passed the due date without being returned.
- Real-time client-side filter by User Name or E-book Title.

---

## 11. Project Directory Structure

```
LIBRARY/
├── src/
│   └── com/digitallibrary/
│       ├── Main.java                          # Main entry point (Launches Swing UI)
│       │
│       ├── database/
│       │   └── DBConnection.java              # JDBC Connection & config manager
│       │
│       ├── model/
│       │   ├── Admin.java                     # Admin entity
│       │   ├── User.java                      # Library user entity
│       │   ├── Ebook.java                     # E-book catalog entity
│       │   └── IssueRecord.java               # Borrowing transaction entity
│       │
│       ├── dao/
│       │   ├── AdminDAO.java                  # Admin database operations
│       │   ├── UserDAO.java                   # User CRUD & search
│       │   ├── EbookDAO.java                  # E-book CRUD & queries
│       │   └── IssueDAO.java                  # Issue/Return transactional DAO
│       │
│       ├── service/
│       │   ├── AuthService.java               # Admin auth logic
│       │   ├── UserService.java               # User validation & rules
│       │   ├── EbookService.java              # E-book validation & rules
│       │   └── IssueService.java              # 14-day rule & JDBC transactions
│       │
│       └── gui/
│           ├── UIUtils.java                   # UI design system & helper styles
│           ├── LoginFrame.java                # Swing login window
│           ├── DashboardFrame.java            # Main statistics & action dashboard
│           ├── UserManagementFrame.java       # User management screen
│           ├── EbookManagementFrame.java      # E-book management screen
│           ├── SearchFrame.java               # Search catalog screen
│           ├── IssueBookFrame.java            # Issue e-book screen
│           ├── ReturnBookFrame.java           # Return e-book screen
│           └── IssueHistoryFrame.java         # Issue history & audit trail screen
│
├── lib/
│   └── mysql-connector-j-8.3.0.jar            # MySQL JDBC driver jar
├── target/
│   └── digital-library-management-system-1.0.0.jar
├── database.sql                               # Complete MySQL database creation script
├── db.properties                              # Database connection configuration
├── pom.xml                                    # Maven build configuration
├── run.bat                                    # Double-click launcher for Windows
├── compile.bat                                # Double-click compiler for Windows
└── README.md                                  # Complete project documentation
```

---

## 12. Verification & Testing Checklist

- [x] **Swing Frontend**: Pure Java Swing desktop GUI window (no web, no React/Node.js, no browser).
- [x] **Core Java Backend**: Modular architecture with Models, DAOs, and Services.
- [x] **JDBC Connectivity**: Driver `com.mysql.cj.jdbc.Driver` loaded, `PreparedStatement` used in all queries.
- [x] **No SQL in GUI**: All database access delegated through Service and DAO layers.
- [x] **Atomic Transactions**: `setAutoCommit(false)`, `commit()`, and `rollback()` strictly implemented for Issue and Return.
- [x] **14-day Due Date**: Automatic `LocalDate.now().plusDays(14)` calculation.
- [x] **Validation**: Unique email enforcement, unique ISBN enforcement, phone validation.
- [x] **Double Return Prevention**: Cannot return an already returned book.
- [x] **Clean compilation**: 0 compilation errors across all source files on JDK 17.
