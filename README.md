# FinCore — Complete Java Swing + MySQL Project

This project provides a desktop banking UI, JDBC data access, a connection test, and a Maven-managed MySQL Connector/J dependency.

## Requirements
- JDK 17 or newer
- MySQL Server and MySQL Workbench
- IntelliJ IDEA (recommended; it can import Maven dependencies automatically)
- Internet once when Maven downloads MySQL Connector/J

## Step 1 — Create/check database
1. Start your local MySQL server (Windows Services: `MySQL80` or the installed MySQL service should be Running).
2. Open MySQL Workbench and connect to your local server.
3. Open `database.sql` and click the lightning bolt to execute it.
4. Run `USE bank; SHOW TABLES;` and confirm `accounts` and `transactions` appear.

If you already have a `bank` database with real/test data, take a backup before modifying the schema. The script uses `IF NOT EXISTS` and does not delete existing rows.

## Step 2 — Configure MySQL password once
1. In the project root, copy `db.properties.example` and rename the copy to `db.properties`.
2. Open `db.properties` and set `db.password` to the password for your local MySQL user. Keep `db.user=root` only if your MySQL user is `root`.
3. Save. Do not upload `db.properties` to GitHub; it is ignored by `.gitignore`.

Example (replace the placeholder locally):

```properties
db.url=jdbc:mysql://localhost:3306/bank?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=your_actual_local_password
```

The application reads `DB_URL`, `DB_USER`, and `DB_PASSWORD` environment variables first if present; otherwise it reads `db.properties` from the working/project root.

## Step 3 — Open in IntelliJ IDEA
1. Extract the ZIP.
2. IntelliJ → **File → Open** → select the extracted `FinCore-Full-Project` folder (the one containing `pom.xml`).
3. If asked, choose **Open as Project** and trust it. Wait for Maven sync/download to finish. If not prompted, open the Maven tool window and click **Reload All Maven Projects**.
4. Expand `src` and run `TestConnection.java` first.
5. Expected output:

```text
MySQL Connected Successfully!
Database: bank
Connection valid: true
```

6. If the connection test succeeds, run `Main.java` to open the FinCore UI.

## Step 4 — First use
- Select **Create a new account** and make a test account number, your name, a starting balance, and a 4-digit PIN.
- Then log in with that account number and PIN.
- Test deposits, withdrawals, transfers, and transaction history with test data only.

## Troubleshooting
- `db.properties` missing/password not configured: verify file is named exactly `db.properties` (not `db.properties.txt`) and is in the same project root as `pom.xml`.
- `Access denied for user`: MySQL username/password is incorrect, or that user does not have permission.
- `Communications link failure` / connection refused: start MySQL Server and confirm port 3306.
- `com.mysql.cj.jdbc.Driver` not found: reload Maven; make sure Maven sync completed and the dependency is visible under External Libraries.
- `Unknown database 'bank'`: execute `database.sql` in Workbench.
- `Table 'bank.accounts' doesn't exist`: execute `database.sql` and confirm the Workbench connection is to the same local server.

## Files
- `src/Main.java` — Swing UI
- `src/Account.java` — account model
- `src/AccountDAO.java` — JDBC operations; transaction updates are committed atomically
- `src/DBConnection.java` — connection/configuration
- `src/TestConnection.java` — direct connection test
- `database.sql` — schema setup
- `db.properties.example` — safe template; copy to `db.properties`
- `pom.xml` — MySQL JDBC dependency and Java build configuration

## Educational-project note
This is a learning/demo project, not production banking software. PINs are stored as plain integers in this starter schema. Use test accounts and never use real banking credentials or funds.
