package com.bankingsystem;

/**
 * Represents a Cheque Account, a type of Account that supports an overdraft limit.
 */
public class ChequeAccount extends Account {
    private double overdraftLimit;

    /**
     * Constructor for ChequeAccount.
     * FIX: Added String branch parameter and passed it to super().
     * @param accountNumber Unique account number.
     * @param initialBalance Starting balance of the account.
     * @param overdraftLimit The maximum negative balance allowed.
     * @param branch The branch where the account is held.
     */
    public ChequeAccount(String accountNumber, double initialBalance, double overdraftLimit, String branch) {
        // CALL to Account constructor, now requiring branch
        super(accountNumber, branch);

        // The Account constructor initializes balance to 0.0, so we must manually set it here.
        this.balance = initialBalance;

        this.overdraftLimit = Math.max(0, overdraftLimit);
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.err.println("Withdrawal amount must be positive.");
            return false;
        }

        if (this.balance - amount >= -this.overdraftLimit) {
            this.balance -= amount;
            System.out.printf("Withdrawal of %.2f successful from Cheque Account %s. Remaining balance: $%.2f%n",
                    amount, this.accountNumber, this.balance);
            return true;
        } else {
            System.err.printf("Withdrawal of %.2f failed. Exceeds overdraft limit of $%.2f. Current balance: $%.2f%n",
                    amount, this.overdraftLimit, this.balance);
            return false;
        }
    }

    @Override
    public void applyInterest() {
        System.out.println("No interest applied to Cheque Account " + this.accountNumber);
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }
}