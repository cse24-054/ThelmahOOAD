public class Savings extends Account implements InterestBearing, Withdraw {
    private String companyAddress;
    private String customer;

    // Constructor — note the call to super() to initialize inherited attributes
    public Savings(String accountNumber, String branch, String companyAddress, String customer) {
        super(accountNumber, branch);
        this.companyAddress = companyAddress;
        this.customer = customer;
    }

    // Apply 0.5% interest to the current balance
    @Override
    public void applyInterest() {
        balance += balance * 0.005; // Adds 0.5% interest
    }

    // Withdraws a given amount if sufficient balance exists
    @Override
    public void withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawal successful: " + amount);
        } else {
            System.out.println("Insufficient funds.");
        }
    }

    // Optional: Display account details
    public void displayAccountDetails() {
        System.out.println("Savings Account [" + accountNumber + "] - Holder: " + customer);
        System.out.println("Branch: " + branch + ", Company Address: " + companyAddress);
        System.out.println("Current Balance: " + balance);
    }
}
