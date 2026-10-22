import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * FinCore desktop UI built with Java Swing.
 * Uses the existing AccountDAO/JDBC/MySQL business logic.
 */
public class Main {
    private static final Color NAVY = new Color(18, 32, 58);
    private static final Color BLUE = new Color(54, 105, 220);
    private static final Color BG = new Color(244, 247, 252);
    private static final Color TEXT = new Color(35, 47, 68);
    private static final Color MUTED = new Color(112, 124, 145);
    private static final Color GREEN = new Color(20, 145, 104);
    private static final Font FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final NumberFormat MONEY = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    private final AccountDAO dao = new AccountDAO();
    private JFrame frame;
    private JPanel root;
    private long currentAccount;
    private String currentName;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new Main().showWelcome();
        });
    }

    private void setupFrame(String title, int width, int height) {
        frame = new JFrame(title + " | FinCore");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setMinimumSize(new Dimension(900, 620));
        frame.setLocationRelativeTo(null);
        root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        frame.setContentPane(root);
    }

    private JLabel label(String text, int size, Color color, boolean bold) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, size));
        l.setForeground(color);
        return l;
    }

    private JButton button(String text, Color background) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(background);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(12, 18, 12, 18));
        return b;
    }

    private JTextField field(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(FONT);
        f.setForeground(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 236)),
                new EmptyBorder(10, 12, 10, 12)));
        f.setPreferredSize(new Dimension(280, 42));
        f.setToolTipText(placeholder);
        return f;
    }

    private JPasswordField passwordField() {
        JPasswordField f = new JPasswordField();
        f.setFont(FONT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 236)),
                new EmptyBorder(10, 12, 10, 12)));
        f.setPreferredSize(new Dimension(280, 42));
        return f;
    }

    private JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 234, 242)),
                new EmptyBorder(22, 24, 22, 24)));
        return p;
    }

    private void showWelcome() {
        setupFrame("Welcome", 1050, 700);
        JPanel left = new JPanel();
        left.setBackground(NAVY);
        left.setPreferredSize(new Dimension(390, 700));
        left.setBorder(new EmptyBorder(55, 42, 42, 42));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel brand = label("◈  FinCore", 27, Color.WHITE, true);
        JLabel tagline = label("Banking made simple.", 27, Color.WHITE, true);
        JLabel detail = label("<html><div style='width:270px'>A clean, secure and convenient place to manage your everyday banking.</div></html>", 15, new Color(196, 208, 229), false);
        left.add(brand); left.add(Box.createVerticalStrut(105)); left.add(tagline);
        left.add(Box.createVerticalStrut(16)); left.add(detail);
        left.add(Box.createVerticalGlue());
        left.add(label("JAVA  •  JDBC  •  MYSQL", 12, new Color(160, 180, 211), true));

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(BG);
        JPanel content = new JPanel();
        content.setBackground(BG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 35, 20, 35));
        content.add(label("Welcome back", 29, TEXT, true));
        content.add(Box.createVerticalStrut(10));
        content.add(label("Choose how you'd like to continue.", 15, MUTED, false));
        content.add(Box.createVerticalStrut(34));
        JButton login = button("Login to your account   →", BLUE);
        login.setAlignmentX(Component.LEFT_ALIGNMENT);
        login.setMaximumSize(new Dimension(350, 48));
        login.addActionListener(e -> showLogin());
        JButton create = button("Create a new account", NAVY);
        create.setAlignmentX(Component.LEFT_ALIGNMENT);
        create.setMaximumSize(new Dimension(350, 48));
        create.addActionListener(e -> showRegister());
        content.add(login); content.add(Box.createVerticalStrut(14)); content.add(create);
        content.add(Box.createVerticalStrut(35));
        content.add(label("Your money. Your overview. Your control.", 13, MUTED, false));
        right.add(content);
        root.add(left, BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void showLogin() {
        setupFrame("Login", 1000, 680);
        root.add(topBar("Sign in", "Access your FinCore account"), BorderLayout.NORTH);
        JPanel center = new JPanel(new GridBagLayout()); center.setBackground(BG);
        JPanel form = card(); form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(label("Login to FinCore", 25, TEXT, true));
        form.add(Box.createVerticalStrut(8));
        form.add(label("Enter your account details to continue.", 14, MUTED, false));
        form.add(Box.createVerticalStrut(25));
        form.add(label("Account number", 13, TEXT, true));
        JTextField account = field("e.g. 100012345"); form.add(account);
        form.add(Box.createVerticalStrut(16));
        form.add(label("4-digit PIN", 13, TEXT, true));
        JPasswordField pin = passwordField(); form.add(pin);
        form.add(Box.createVerticalStrut(22));
        JButton submit = button("Secure login  →", BLUE); submit.setAlignmentX(Component.LEFT_ALIGNMENT);
        submit.addActionListener(e -> {
            try {
                long number = Long.parseLong(account.getText().trim());
                String pinText = new String(pin.getPassword());
                if (number <= 0 || !pinText.matches("\\d{4}")) {
                    error("Enter a valid account number and a 4-digit PIN."); return;
                }
                if (dao.login(number, Integer.parseInt(pinText))) {
                    currentAccount = number;
                    currentName = dao.getAccountName(number);
                    showDashboard();
                } else {
                    error("Login failed. Check your account number and PIN, and ensure MySQL is running.");
                }
            } catch (NumberFormatException ex) { error("Account number must contain digits only."); }
        });
        JButton back = button("← Back", NAVY); back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.addActionListener(e -> showWelcome());
        form.add(submit); form.add(Box.createVerticalStrut(12)); form.add(back);
        center.add(form); root.add(center, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void showRegister() {
        setupFrame("Create account", 1000, 720);
        root.add(topBar("Create your account", "It takes just a moment to get started"), BorderLayout.NORTH);
        JPanel center = new JPanel(new GridBagLayout()); center.setBackground(BG);
        JPanel form = card(); form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(label("New account", 25, TEXT, true));
        form.add(Box.createVerticalStrut(20));
        form.add(label("Account number", 13, TEXT, true));
        JTextField account = field("Choose a unique numeric account number"); form.add(account);
        form.add(Box.createVerticalStrut(12));
        form.add(label("Full name", 13, TEXT, true));
        JTextField name = field("Your full name"); form.add(name);
        form.add(Box.createVerticalStrut(12));
        form.add(label("Initial deposit (₹)", 13, TEXT, true));
        JTextField balance = field("0.00 or more"); form.add(balance);
        form.add(Box.createVerticalStrut(12));
        form.add(label("Set a 4-digit PIN", 13, TEXT, true));
        JPasswordField pin = passwordField(); form.add(pin);
        form.add(Box.createVerticalStrut(20));
        JButton submit = button("Create account", BLUE); submit.setAlignmentX(Component.LEFT_ALIGNMENT);
        submit.addActionListener(e -> {
            try {
                long number = Long.parseLong(account.getText().trim());
                String fullName = name.getText().trim();
                double initial = Double.parseDouble(balance.getText().trim());
                String pinText = new String(pin.getPassword());
                if (number <= 0) { error("Account number must be greater than zero."); return; }
                if (fullName.isEmpty()) { error("Please enter your full name."); return; }
                if (!Double.isFinite(initial) || initial < 0) { error("Initial deposit must be zero or more."); return; }
                if (!pinText.matches("\\d{4}")) { error("PIN must contain exactly 4 digits."); return; }
                Account acc = new Account(number, fullName, initial, Integer.parseInt(pinText));
                if (dao.createAccount(acc)) {
                    info("Account created successfully! You can now log in.");
                    showLogin();
                } else {
                    error("Account could not be created. The account number may already exist, or MySQL may not be connected.");
                }
            } catch (NumberFormatException ex) { error("Enter a valid account number and initial deposit."); }
        });
        JButton back = button("← Back", NAVY); back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.addActionListener(e -> showWelcome());
        form.add(submit); form.add(Box.createVerticalStrut(10)); form.add(back);
        center.add(form); root.add(center, BorderLayout.CENTER); frame.setVisible(true);
    }

    private JPanel topBar(String title, String subtitle) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(20, 30, 20, 30));
        JPanel texts = new JPanel(); texts.setBackground(Color.WHITE);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.add(label(title, 22, TEXT, true)); texts.add(Box.createVerticalStrut(5));
        texts.add(label(subtitle, 13, MUTED, false));
        p.add(texts, BorderLayout.WEST);
        p.add(label("◈ FinCore", 20, BLUE, true), BorderLayout.EAST);
        return p;
    }

    private void showDashboard() {
        setupFrame("Dashboard", 1200, 760);
        JPanel nav = new JPanel(); nav.setBackground(NAVY);
        nav.setPreferredSize(new Dimension(220, 760));
        nav.setBorder(new EmptyBorder(28, 18, 25, 18));
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.add(label("◈  FinCore", 24, Color.WHITE, true));
        nav.add(Box.createVerticalStrut(45));
        nav.add(label("MAIN MENU", 11, new Color(160, 180, 211), true));
        nav.add(Box.createVerticalStrut(18));
        nav.add(navItem("▦   Overview", () -> showDashboard()));
        nav.add(Box.createVerticalStrut(8));
        nav.add(navItem("＋   Deposit money", () -> showMoneyForm("Deposit", false)));
        nav.add(Box.createVerticalStrut(8));
        nav.add(navItem("↗   Withdraw money", () -> showMoneyForm("Withdraw", false)));
        nav.add(Box.createVerticalStrut(8));
        nav.add(navItem("⇄   Transfer money", () -> showMoneyForm("Transfer", true)));
        nav.add(Box.createVerticalStrut(8));
        nav.add(navItem("▤   Transactions", () -> showTransactions()));
        nav.add(Box.createVerticalGlue());
        nav.add(navItem("↪   Log out", () -> { currentAccount = 0; currentName = null; showWelcome(); }));

        JPanel body = new JPanel(new BorderLayout(0, 20)); body.setBackground(BG);
        body.setBorder(new EmptyBorder(28, 30, 28, 30));
        JPanel heading = new JPanel(new BorderLayout()); heading.setOpaque(false);
        JPanel greet = new JPanel(); greet.setOpaque(false); greet.setLayout(new BoxLayout(greet, BoxLayout.Y_AXIS));
        greet.add(label("Good to see you, " + safeName() + " 👋", 26, TEXT, true));
        greet.add(Box.createVerticalStrut(6)); greet.add(label("Here's what's happening with your account today.", 14, MUTED, false));
        heading.add(greet, BorderLayout.WEST);
        heading.add(label("ACCOUNT  •  " + currentAccount, 12, MUTED, true), BorderLayout.EAST);
        double balance = dao.getBalance(currentAccount);
        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0)); cards.setOpaque(false);
        cards.add(statCard("AVAILABLE BALANCE", balance >= 0 ? MONEY.format(balance) : "Unavailable", "Your current account balance", BLUE));
        cards.add(statCard("ACCOUNT HOLDER", safeName(), "Verified account profile", GREEN));
        cards.add(statCard("ACCOUNT NUMBER", String.valueOf(currentAccount), "Your FinCore account ID", new Color(132, 91, 200)));

        JPanel actions = card(); actions.setLayout(new BorderLayout(0, 15));
        actions.add(label("Quick actions", 19, TEXT, true), BorderLayout.NORTH);
        JPanel actionGrid = new JPanel(new GridLayout(1, 3, 14, 0)); actionGrid.setOpaque(false);
        actionGrid.add(actionCard("＋", "Deposit", "Add money to your account", () -> showMoneyForm("Deposit", false), BLUE));
        actionGrid.add(actionCard("↗", "Withdraw", "Take money out securely", () -> showMoneyForm("Withdraw", false), GREEN));
        actionGrid.add(actionCard("⇄", "Transfer", "Send money to another account", () -> showMoneyForm("Transfer", true), new Color(132, 91, 200)));
        actions.add(actionGrid, BorderLayout.CENTER);

        JPanel lower = card(); lower.setLayout(new BorderLayout(0, 12));
        lower.add(label("Recent transactions", 19, TEXT, true), BorderLayout.NORTH);
        JTable table = transactionTable();
        lower.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton all = button("View all transactions  →", BLUE);
        all.addActionListener(e -> showTransactions());
        JPanel foot = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); foot.setOpaque(false); foot.add(all);
        lower.add(foot, BorderLayout.SOUTH);

        JPanel center = new JPanel(); center.setOpaque(false); center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        lower.setPreferredSize(new Dimension(700, 240));
        center.add(cards); center.add(Box.createVerticalStrut(18)); center.add(actions);
        center.add(Box.createVerticalStrut(18)); center.add(lower);
        body.add(heading, BorderLayout.NORTH); body.add(center, BorderLayout.CENTER);
        root.add(nav, BorderLayout.WEST); root.add(body, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private String safeName() { return currentName == null || currentName.trim().isEmpty() ? "there" : currentName; }

    private JButton navItem(String text, Runnable action) {
        JButton b = new JButton(text); b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14)); b.setForeground(new Color(231, 237, 248));
        b.setBackground(NAVY); b.setFocusPainted(false); b.setBorderPainted(false);
        b.setBorder(new EmptyBorder(12, 10, 12, 6)); b.setMaximumSize(new Dimension(190, 45));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> action.run()); return b;
    }

    private JPanel statCard(String title, String value, String detail, Color accent) {
        JPanel p = card(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(label(title, 11, MUTED, true)); p.add(Box.createVerticalStrut(15));
        p.add(label(value, value.length() > 18 ? 18 : 24, accent, true)); p.add(Box.createVerticalStrut(9));
        p.add(label(detail, 12, MUTED, false)); return p;
    }

    private JPanel actionCard(String symbol, String title, String detail, Runnable action, Color accent) {
        JPanel p = new JPanel(); p.setBackground(new Color(249, 250, 253));
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230,234,242)), new EmptyBorder(14, 14, 14, 14)));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(label(symbol, 25, accent, true)); p.add(Box.createVerticalStrut(7));
        p.add(label(title, 16, TEXT, true)); p.add(Box.createVerticalStrut(5));
        p.add(label("<html><div style='width:150px'>" + detail + "</div></html>", 12, MUTED, false));
        p.add(Box.createVerticalGlue());
        JButton go = button("Open  →", accent); go.setAlignmentX(Component.LEFT_ALIGNMENT);
        go.addActionListener(e -> action.run()); p.add(Box.createVerticalStrut(10)); p.add(go);
        return p;
    }

    private void showMoneyForm(String mode, boolean transfer) {
        setupFrame(mode, 1050, 700);
        root.add(topBar(mode + " money", transfer ? "Send money securely to another FinCore account" : "Complete your transaction securely"), BorderLayout.NORTH);
        JPanel center = new JPanel(new GridBagLayout()); center.setBackground(BG);
        JPanel form = card(); form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(label(mode + " money", 25, TEXT, true)); form.add(Box.createVerticalStrut(8));
        form.add(label("Account: " + currentAccount + "  •  " + safeName(), 14, MUTED, false));
        form.add(Box.createVerticalStrut(20));
        form.add(label("Amount (₹)", 13, TEXT, true));
        JTextField amount = field("Enter amount"); form.add(amount);
        JTextField receiver = null;
        if (transfer) {
            form.add(Box.createVerticalStrut(14)); form.add(label("Receiver account number", 13, TEXT, true));
            receiver = field("Enter receiver's account number"); form.add(receiver);
        }
        final JTextField receiverField = receiver;
        form.add(Box.createVerticalStrut(22));
        JButton submit = button(mode + " now  →", BLUE); submit.setAlignmentX(Component.LEFT_ALIGNMENT);
        submit.addActionListener(e -> {
            try {
                double value = Double.parseDouble(amount.getText().trim());
                if (!Double.isFinite(value) || value <= 0) { error("Amount must be greater than zero."); return; }
                boolean success;
                if (transfer) {
                    long to = Long.parseLong(receiverField.getText().trim());
                    if (to <= 0 || to == currentAccount) { error("Enter a valid, different receiver account."); return; }
                    success = dao.transfer(currentAccount, to, value);
                } else if ("Deposit".equals(mode)) {
                    success = dao.deposit(currentAccount, value);
                } else {
                    success = dao.withdraw(currentAccount, value);
                }
                if (success) {
                    info(mode + " completed successfully.");
                    showDashboard();
                } else {
                    error("Transaction failed. Check your balance, account details and MySQL connection.");
                }
            } catch (NumberFormatException ex) {
                error(transfer ? "Enter a valid amount and receiver account number." : "Enter a valid amount.");
            }
        });
        JButton back = button("← Back to dashboard", NAVY); back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.addActionListener(e -> showDashboard());
        form.add(submit); form.add(Box.createVerticalStrut(12)); form.add(back);
        center.add(form); root.add(center, BorderLayout.CENTER); frame.setVisible(true);
    }

    private JTable transactionTable() {
        String[] columns = {"Transaction ID", "Type", "Amount", "Date & time"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        List<Object[]> rows = dao.getTransactions(currentAccount);
        int limit = Math.min(rows.size(), 5);
        for (int i = 0; i < limit; i++) {
            Object[] r = rows.get(i);
            model.addRow(new Object[]{r[0], r[1], MONEY.format(((Number)r[2]).doubleValue()), r[3]});
        }
        JTable table = new JTable(model);
        styleTable(table); return table;
    }

    private void showTransactions() {
        setupFrame("Transactions", 1100, 720);
        root.add(topBar("Transaction history", "A record of activity on your account"), BorderLayout.NORTH);
        JPanel content = card(); content.setLayout(new BorderLayout(0, 16));
        JPanel title = new JPanel(new BorderLayout()); title.setOpaque(false);
        title.add(label("Account " + currentAccount, 17, TEXT, true), BorderLayout.WEST);
        JButton back = button("← Dashboard", NAVY); back.addActionListener(e -> showDashboard());
        title.add(back, BorderLayout.EAST); content.add(title, BorderLayout.NORTH);
        String[] columns = {"Transaction ID", "Type", "Amount", "Date & time"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        for (Object[] r : dao.getTransactions(currentAccount)) {
            model.addRow(new Object[]{r[0], r[1], MONEY.format(((Number)r[2]).doubleValue()), r[3]});
        }
        JTable table = new JTable(model); styleTable(table);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel wrap = new JPanel(new BorderLayout()); wrap.setBackground(BG); wrap.setBorder(new EmptyBorder(25, 30, 30, 30));
        wrap.add(content, BorderLayout.CENTER); root.add(wrap, BorderLayout.CENTER); frame.setVisible(true);
    }

    private void styleTable(JTable table) {
        table.setFont(FONT); table.setRowHeight(32); table.setForeground(TEXT);
        table.setGridColor(new Color(235, 238, 244)); table.setSelectionBackground(new Color(225, 235, 255));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(242, 245, 250));
        table.getTableHeader().setForeground(TEXT);
    }

    private void error(String message) { JOptionPane.showMessageDialog(frame, message, "FinCore", JOptionPane.ERROR_MESSAGE); }
    private void info(String message) { JOptionPane.showMessageDialog(frame, message, "FinCore", JOptionPane.INFORMATION_MESSAGE); }
}
