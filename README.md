# 🏦FinCore - Financial Transaction Management System
A console-based Bank Management System developed using Java, MySQL, and JDBC.

## 🚀 Features

- Create new bank account
- Secure login using account number and PIN
- Check account balance
- Deposit money
- Withdraw money
- Transfer money between accounts
- View transaction history
- Input validation
- Logout functionality
- Database transaction handling using commit and rollback

## 🛠️ Technologies Used

- Java
- MySQL
- JDBC
- MySQL Connector/J
- IntelliJ IDEA

## 📂 Project Structure

```text
src/
├── Main.java
├── Account.java
├── AccountDAO.java
├── DBConnection.java
└── InputHelper.java

```




## 🗄️ Database

The project uses a MySQL database named `bank`.

### Tables

- `accounts`
- `transactions`

## 🔐 Security

The MySQL password is not stored directly in the source code. It is accessed using the `DB_PASSWORD` environment variable.

## ▶️ How to Run

1. Start MySQL.
2. Create the `bank` database and required tables.
3. Add MySQL Connector/J to the project.
4. Set the `DB_PASSWORD` environment variable.
5. Run `Main.java`.

## 💡 Concepts Learned

- Object-Oriented Programming
- JDBC
- SQL and database connectivity
- DAO pattern
- PreparedStatement
- ResultSet
- Database transactions
- Commit and rollback
- Input validation

## 🔮 Future Improvements

- GUI using JavaFX/Swing
- Admin dashboard
- Password/PIN encryption
- Email/SMS notifications
- Better exception handling
