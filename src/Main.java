import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        boolean running = true;
        while (running) {
            System.out.println("================================");
            System.out.println("     BANK MANAGEMENT SYSTEM");
            System.out.println("================================");

            System.out.println("1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            System.out.print("Enter your choice: ");
            int choice = InputHelper.readInt(sc);

            if (choice == 1) {

                System.out.print("Enter account number: ");
                long accountNumber = InputHelper.readLong(sc);
                if (accountNumber <= 0) {
                    System.out.println("Invalid account number!");
                    continue;
                }
                sc.nextLine();

                System.out.print("Enter your name: ");
                String name = sc.nextLine();
                if (name.trim().isEmpty()) {
                    System.out.println("Name cannot be empty!");
                    continue;
                }
                System.out.print("Enter initial balance: ");
                double balance = InputHelper.readDouble(sc);
                if (balance < 0) {
                    System.out.println("Initial balance cannot be negative!");
                    continue;
                }

                System.out.print("Set your PIN: ");
                int pin = InputHelper.readInt(sc);
                if (pin < 1000 || pin > 9999) {
                    System.out.println("PIN must be exactly 4 digits!");
                    continue;
                }

                Account acc = new Account(
                        accountNumber,
                        name,
                        balance,
                        pin
                );

                AccountDAO dao = new AccountDAO();
               boolean created = dao.createAccount(acc);
                if (created) {
                    System.out.println("Account created successfully!");
                }

            } else if (choice == 2) {

                System.out.print("Enter account number: ");
                long accountNumber = sc.nextLong();

                AccountDAO dao = new AccountDAO();

                boolean loginSuccess = false;

                for (int attempt = 1; attempt <= 3; attempt++) {

                    System.out.print("Enter PIN: ");
                    int pin = sc.nextInt();

                    loginSuccess = dao.login(accountNumber, pin);

                    if (loginSuccess) {
                        break;
                    }

                    System.out.println("Invalid PIN!");

                    if (attempt < 3) {
                        System.out.println("Attempts remaining: " + (3 - attempt));
                    }
                }

                if (loginSuccess) {

                    String name = dao.getAccountName(accountNumber);

                    System.out.println("\nLogin successful!");
                    System.out.println("Welcome, " + name + "!");

                    boolean loggedIn = true;

                    while (loggedIn) {

                        System.out.println("\n========== ACCOUNT MENU ==========");
                        System.out.println("1. Check Balance");
                        System.out.println("2. Deposit Money");
                        System.out.println("3. Withdraw Money");
                        System.out.println("4. Transaction History");
                        System.out.println("5. Transfer Money");
                        System.out.println("6. Logout");

                        System.out.print("Enter your choice: ");
                        int accountChoice = InputHelper.readInt(sc);

                        if (accountChoice == 1) {

                            double balance = dao.getBalance(accountNumber);

                            if (balance >= 0) {
                                System.out.println("Current Balance: ₹" + balance);
                            } else {
                                System.out.println("Unable to fetch balance.");
                            }
                        } else if (accountChoice == 2) {

                            System.out.print("Enter amount to deposit: ");
                            double amount = InputHelper.readDouble(sc);

                            if (amount <= 0) {
                                System.out.println("Invalid amount.");
                            } else {

                                boolean success = dao.deposit(accountNumber, amount);

                                if (success) {
                                    dao.addTransaction(accountNumber, "DEPOSIT", amount);
                                    System.out.println("Amount deposited successfully!");
                                } else {
                                    System.out.println("Deposit failed.");
                                }
                            }
                        } else if (accountChoice == 3) {

                            System.out.print("Enter amount to withdraw: ");
                            double amount = InputHelper.readDouble(sc);
                            if (amount <= 0) {
                                System.out.println("Invalid amount.");
                            } else {

                                boolean success = dao.withdraw(accountNumber, amount);

                                if (success) {
                                    dao.addTransaction(accountNumber, "WITHDRAW", amount);
                                    System.out.println("Amount withdrawn successfully!");
                                } else {
                                    System.out.println("Insufficient balance or withdrawal failed.");
                                }
                            }
                        } else if (accountChoice == 4) {

                            dao.showTransactions(accountNumber);

                        }
                        else if (accountChoice == 5) {

                            System.out.print("Enter receiver account number: ");
                            long receiverAccount = InputHelper.readLong(sc);

                            System.out.print("Enter amount to transfer: ");
                            double amount = InputHelper.readDouble(sc);

                            if (amount <= 0) {
                                System.out.println("Invalid amount.");
                            }
                            else if (receiverAccount <= 0) {
                                System.out.println("Invalid receiver account.");
                            }
                            else {

                                boolean success =
                                        dao.transfer(accountNumber, receiverAccount, amount);

                                if (success) {
                                    dao.addTransaction(accountNumber, "TRANSFER_OUT", amount);
                                    dao.addTransaction(receiverAccount, "TRANSFER_IN", amount);

                                    System.out.println("Money transferred successfully!");
                                } else {
                                    System.out.println("Transfer failed.");
                                }
                            }
                        }



                         else if (accountChoice == 6) {
                            System.out.println("Logged out successfully!");
                            loggedIn = false;
                        } else {
                            System.out.println("Invalid choice!");
                        }
                    }

                } else {

                    System.out.println("Invalid account number or PIN.");
                }
            } else if (choice == 3) {

                System.out.println("Thank you for using our Bank System!");
                running = false;

            } else {

                System.out.println("Invalid choice!");
            }
        }
    }
}