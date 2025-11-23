public abstract class Account {
    // Encapsulated (protected) attributes - accessible by subclasses
    protected double balance;
    protected String accountNumber;
    protected String branch;

    // Constructor to initialize shared account properties
    public Account(String accountNumber, String branch) {
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.balance = 0.0; // Default starting balance
    }

    // Deposit method shared by all account types
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited: " + amount);
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    // Getter methods (encapsulation)
    public double getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBranch() {
        return branch;
    }

    // Abstract methods to be implemented differently by each account type
    // Demonstrates abstraction and polymorphism
    public abstract void applyInterest();   // e.g., 0.5% for savings, 5% for investment
    public abstract void withdraw(double amount); // Different behavior depending on account type
}

