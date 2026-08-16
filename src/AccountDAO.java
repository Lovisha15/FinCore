
    import java.sql.Connection;
import java.sql.PreparedStatement;
    import java.sql.ResultSet;

        public class AccountDAO {

            public boolean createAccount(Account acc) {

                String sql = "INSERT INTO accounts " +
                        "(account_number, name, balance, pin) VALUES (?, ?, ?, ?)";

                try {
                    Connection con = DBConnection.getConnection();

                    PreparedStatement ps = con.prepareStatement(sql);

                    ps.setLong(1, acc.getAccountnumber());
                    ps.setString(2, acc.getName());
                    ps.setDouble(3, acc.getbalance());
                    ps.setInt(4, acc.getPin());

                    ps.executeUpdate();

                    ps.close();
                    con.close();

                    return true;

                } catch (Exception e) {

                    if (e.getMessage() != null &&
                            e.getMessage().contains("Duplicate entry")) {

                        System.out.println("Account number already exists!");

                    } else {
                        System.out.println("Account could not be created.");
                        e.printStackTrace();
                    }

                    return false;
                }
            }

            public boolean login(long accountNumber, int pin) {

                String sql = "SELECT * FROM accounts WHERE account_number = ? AND pin = ?";

                try {
                    Connection con = DBConnection.getConnection();

                    PreparedStatement ps = con.prepareStatement(sql);

                    ps.setLong(1, accountNumber);
                    ps.setInt(2, pin);

                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        ps.close();
                        con.close();
                        return true;
                    }

                    ps.close();
                    con.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }

            public double getBalance(long accountNumber) {

                String sql = "SELECT balance FROM accounts WHERE account_number = ?";

                try {
                    Connection con = DBConnection.getConnection();

                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setLong(1, accountNumber);

                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        double balance = rs.getDouble("balance");

                        rs.close();
                        ps.close();
                        con.close();

                        return balance;
                    }

                    rs.close();
                    ps.close();
                    con.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return -1;
            }

            public boolean deposit(long accountNumber, double amount) {

                String sql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";

                try {
                    Connection con = DBConnection.getConnection();

                    PreparedStatement ps = con.prepareStatement(sql);

                    ps.setDouble(1, amount);
                    ps.setLong(2, accountNumber);

                    int rows = ps.executeUpdate();

                    ps.close();
                    con.close();

                    return rows > 0;

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }

            }

            public boolean withdraw(long accountNumber, double amount) {

                String sql = "UPDATE accounts " +
                        "SET balance = balance - ? " +
                        "WHERE account_number = ? AND balance >= ?";

                try {
                    Connection con = DBConnection.getConnection();

                    PreparedStatement ps = con.prepareStatement(sql);

                    ps.setDouble(1, amount);
                    ps.setLong(2, accountNumber);
                    ps.setDouble(3, amount);

                    int rows = ps.executeUpdate();

                    ps.close();
                    con.close();

                    return rows > 0;

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            public void addTransaction(long accountNumber, String type, double amount) {

                String sql = "INSERT INTO transactions " +
                        "(account_number, type, amount) VALUES (?, ?, ?)";

                try {
                    Connection con = DBConnection.getConnection();

                    PreparedStatement ps = con.prepareStatement(sql);

                    ps.setLong(1, accountNumber);
                    ps.setString(2, type);
                    ps.setDouble(3, amount);

                    ps.executeUpdate();

                    ps.close();
                    con.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void showTransactions(long accountNumber) {

                String sql = "SELECT transaction_id, type, amount, transaction_date " +
                        "FROM transactions WHERE account_number = ? " +
                        "ORDER BY transaction_date DESC";

                try {
                    Connection con = DBConnection.getConnection();

                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setLong(1, accountNumber);

                    ResultSet rs = ps.executeQuery();

                    System.out.println("\n========== TRANSACTION HISTORY ==========");

                    while (rs.next()) {
                        System.out.println(
                                rs.getInt("transaction_id") + " | " +
                                        rs.getString("type") + " | ₹" +
                                        rs.getDouble("amount") + " | " +
                                        rs.getTimestamp("transaction_date")
                        );
                    }

                    rs.close();
                    ps.close();
                    con.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public boolean transfer(long senderAccount, long receiverAccount, double amount) {

                if (amount <= 0 || senderAccount == receiverAccount) {
                    return false;
                }

                Connection con = null;

                try {
                    con = DBConnection.getConnection();

                    // Auto commit temporarily off
                    con.setAutoCommit(false);

                    // 1. Check sender balance
                    String senderSql =
                            "SELECT balance FROM accounts WHERE account_number = ?";

                    PreparedStatement senderPs = con.prepareStatement(senderSql);
                    senderPs.setLong(1, senderAccount);

                    ResultSet senderRs = senderPs.executeQuery();

                    if (!senderRs.next()) {
                        senderRs.close();
                        senderPs.close();
                        con.rollback();
                        return false;
                    }

                    double balance = senderRs.getDouble("balance");

                    senderRs.close();
                    senderPs.close();

                    if (balance < amount) {
                        con.rollback();
                        return false;
                    }

                    // 2. Check receiver exists
                    String receiverSql =
                            "SELECT account_number FROM accounts WHERE account_number = ?";

                    PreparedStatement receiverPs = con.prepareStatement(receiverSql);
                    receiverPs.setLong(1, receiverAccount);

                    ResultSet receiverRs = receiverPs.executeQuery();

                    if (!receiverRs.next()) {
                        receiverRs.close();
                        receiverPs.close();
                        con.rollback();
                        return false;
                    }

                    receiverRs.close();
                    receiverPs.close();

                    // 3. Sender se money minus
                    String withdrawSql =
                            "UPDATE accounts SET balance = balance - ? " +
                                    "WHERE account_number = ?";

                    PreparedStatement withdrawPs = con.prepareStatement(withdrawSql);

                    withdrawPs.setDouble(1, amount);
                    withdrawPs.setLong(2, senderAccount);

                    withdrawPs.executeUpdate();
                    withdrawPs.close();

                    // 4. Receiver ko money add
                    String depositSql =
                            "UPDATE accounts SET balance = balance + ? " +
                                    "WHERE account_number = ?";

                    PreparedStatement depositPs = con.prepareStatement(depositSql);

                    depositPs.setDouble(1, amount);
                    depositPs.setLong(2, receiverAccount);

                    depositPs.executeUpdate();
                    depositPs.close();

                    // 5. Everything successful
                    con.commit();

                    con.close();

                    return true;

                } catch (Exception e) {

                    try {
                        if (con != null) {
                            con.rollback();
                        }
                    } catch (Exception rollbackError) {
                        rollbackError.printStackTrace();
                    }

                    e.printStackTrace();
                    return false;
                }
            }
            public String getAccountName(long accountNumber) {

                String sql = "SELECT name FROM accounts WHERE account_number = ?";

                try {
                    Connection con = DBConnection.getConnection();

                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setLong(1, accountNumber);

                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        String name = rs.getString("name");

                        rs.close();
                        ps.close();
                        con.close();

                        return name;
                    }

                    rs.close();
                    ps.close();
                    con.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }