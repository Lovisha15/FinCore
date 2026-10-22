import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** All database operations for FinCore. SQL values are parameterized. */
public class AccountDAO {
    public boolean createAccount(Account account) {
        String sql = "INSERT INTO accounts (account_number, name, balance, pin) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, account.getAccountnumber());
            ps.setString(2, account.getName().trim());
            ps.setBigDecimal(3, BigDecimal.valueOf(account.getbalance()));
            ps.setInt(4, account.getPin());
            ps.executeUpdate();
            if (account.getbalance() > 0) addTransaction(account.getAccountnumber(), "OPENING_DEPOSIT", account.getbalance());
            return true;
        } catch (SQLException e) {
            System.err.println("Create account failed: " + e.getMessage());
            return false;
        }
    }

    public boolean login(long accountNumber, int pin) {
        String sql = "SELECT 1 FROM accounts WHERE account_number = ? AND pin = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, accountNumber); ps.setInt(2, pin);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            System.err.println("Login/database check failed: " + e.getMessage());
            return false;
        }
    }

    public double getBalance(long accountNumber) {
        String sql = "SELECT balance FROM accounts WHERE account_number = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getBigDecimal(1).doubleValue() : -1; }
        } catch (SQLException e) { System.err.println("Balance query failed: " + e.getMessage()); return -1; }
    }

    public String getAccountName(long accountNumber) {
        String sql = "SELECT name FROM accounts WHERE account_number = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getString(1) : null; }
        } catch (SQLException e) { System.err.println("Account lookup failed: " + e.getMessage()); return null; }
    }

    public boolean deposit(long accountNumber, double amount) {
        if (!validAmount(amount)) return false;
        String update = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(update)) {
                ps.setBigDecimal(1, BigDecimal.valueOf(amount)); ps.setLong(2, accountNumber);
                if (ps.executeUpdate() != 1) { con.rollback(); return false; }
            }
            insertTransaction(con, accountNumber, "DEPOSIT", amount);
            con.commit(); return true;
        } catch (SQLException e) { System.err.println("Deposit failed: " + e.getMessage()); return false; }
    }

    public boolean withdraw(long accountNumber, double amount) {
        if (!validAmount(amount)) return false;
        String update = "UPDATE accounts SET balance = balance - ? WHERE account_number = ? AND balance >= ?";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(update)) {
                ps.setBigDecimal(1, BigDecimal.valueOf(amount)); ps.setLong(2, accountNumber); ps.setBigDecimal(3, BigDecimal.valueOf(amount));
                if (ps.executeUpdate() != 1) { con.rollback(); return false; }
            }
            insertTransaction(con, accountNumber, "WITHDRAWAL", amount);
            con.commit(); return true;
        } catch (SQLException e) { System.err.println("Withdrawal failed: " + e.getMessage()); return false; }
    }

    public boolean transfer(long senderAccount, long receiverAccount, double amount) {
        if (!validAmount(amount) || senderAccount == receiverAccount) return false;
        String debit = "UPDATE accounts SET balance = balance - ? WHERE account_number = ? AND balance >= ?";
        String credit = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(debit)) {
                ps.setBigDecimal(1, BigDecimal.valueOf(amount)); ps.setLong(2, senderAccount); ps.setBigDecimal(3, BigDecimal.valueOf(amount));
                if (ps.executeUpdate() != 1) { con.rollback(); return false; }
            }
            try (PreparedStatement ps = con.prepareStatement(credit)) {
                ps.setBigDecimal(1, BigDecimal.valueOf(amount)); ps.setLong(2, receiverAccount);
                if (ps.executeUpdate() != 1) { con.rollback(); return false; }
            }
            insertTransaction(con, senderAccount, "TRANSFER_SENT", amount);
            insertTransaction(con, receiverAccount, "TRANSFER_RECEIVED", amount);
            con.commit(); return true;
        } catch (SQLException e) { System.err.println("Transfer failed: " + e.getMessage()); return false; }
    }

    public void addTransaction(long accountNumber, String type, double amount) {
        try (Connection con = DBConnection.getConnection()) { insertTransaction(con, accountNumber, type, amount); }
        catch (SQLException e) { System.err.println("Could not save transaction: " + e.getMessage()); }
    }

    private void insertTransaction(Connection con, long accountNumber, String type, double amount) throws SQLException {
        String sql = "INSERT INTO transactions (account_number, type, amount) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, accountNumber); ps.setString(2, type); ps.setBigDecimal(3, BigDecimal.valueOf(amount)); ps.executeUpdate();
        }
    }

    public List<Object[]> getTransactions(long accountNumber) {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT transaction_id, type, amount, transaction_date FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC, transaction_id DESC";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) rows.add(new Object[]{rs.getInt("transaction_id"), rs.getString("type"), rs.getBigDecimal("amount").doubleValue(), rs.getTimestamp("transaction_date")});
            }
        } catch (SQLException e) { System.err.println("Transaction history failed: " + e.getMessage()); }
        return rows;
    }

    public void showTransactions(long accountNumber) {
        for (Object[] row : getTransactions(accountNumber)) System.out.printf("%s | %s | ₹%.2f | %s%n", row[0], row[1], row[2], row[3]);
    }

    private boolean validAmount(double amount) { return Double.isFinite(amount) && amount > 0; }
}
