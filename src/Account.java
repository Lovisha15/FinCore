public class Account {
    private final long accountnumber;
    private final String name;
    private final double balance;
    private final int pin;

    public Account(long accountnumber, String name, double balance, int pin) {
        this.accountnumber = accountnumber;
        this.name = name;
        this.balance = balance;
        this.pin = pin;
    }

    public long getAccountnumber() { return accountnumber; }
    public String getName() { return name; }
    public double getbalance() { return balance; }
    public int getPin() { return pin; }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than zero.");
    }

    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than zero.");
        if (amount > balance) throw new IllegalArgumentException("Insufficient balance.");
    }
}
