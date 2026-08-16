public class Account {
    private long accountnumber;
    private String name;
    private double balance;
    private int pin;

    public Account(long accountnumber,String name,double balance , int pin){
        this.accountnumber=accountnumber;
        this.balance=balance;
        this.name=name;
        this.pin=pin;
    }
    public long getAccountnumber(){
        return accountnumber;
    }
    public String getName(){
        return name;
    }
    public double getbalance(){
        return balance;
    }
    public int getPin() {
        return pin;
    }
    public void deposit(double amount){
        if(amount>0){
        balance=balance+amount;
        System.out.println("Amount deposited");}
        else{
            System.out.println("invalid amount");

        }
    }
    public void withdraw(double amount){
        if(balance==0){
            System.out.println("balance is empty");
        }
        else if(amount>balance){
            System.out.println("Insufficient balance");


        }
        else{
            balance=balance-amount;
            System.out.println("amount withdrawed and balance successfully updated");
        }
    }

}
//class test{
//    public static void main(String[] args){
//        Account ac = new Account(123568,"Lovisha",20000,5614);
//        System.out.println("Account holder : "+ ac.getName());
//         System.out.println("Account balance : "+ ac.getbalance());
//         ac.deposit(3000);
//         System.out.println("Total balance: "+ac.getbalance());
//         ac.withdraw(100000);
//         System.out.println("balance after withdrawal: "+ac.getbalance());
//
//    }
//}
