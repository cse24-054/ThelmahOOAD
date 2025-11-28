package com.bankingsystem;

/**
 * Represents a Savings Account, which earns annual interest.
 */
public class Savings extends Account {
    private double annualInterestRate;
    private static final double MIN_BALANCE_FEE = 10.00;
    private static final double MIN_BALANCE_THRESHOLD = 100.00;

    /**
     * Constructor for Savings.
     * FIX: Added String branch parameter and passed it to super().
     * @param accountNumber Unique account number.
     * @param initialBalance Starting balance.
     * @param annualInterestRate The annual interest rate (e.g., 0.02 for 2%).
     * @param branch The branch where the account is held.
     */
    public Savings(String accountNumber, double initialBalance, double annualInterestRate, String branch) {
        // CALL to Account constructor, now requiring branch
        super(accountNumber, branch);

        // Set the initial balance manually
        this.balance = initialBalance;
        this.annualInterestRate = annualInterestRate;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.err.println("Withdrawal amount must be positive.");
            return false;
        }

        // Basic check for sufficient funds
        if (this.balance >= amount) {
            this.balance -= amount;
            System.out.printf("Withdrawal of %.2f successful from Savings Account %s. Remaining balance: $%.2f%n",
                    amount, this.accountNumber, this.balance);

            // Check for minimum balance fee immediately after withdrawal
            if (this.balance < MIN_BALANCE_THRESHOLD) {
                this.balance -= MIN_BALANCE_FEE;
                System.out.printf("WARNING: Balance fell below $%.2f. Minimum balance fee of $%.2f applied.%n",
                        MIN_BALANCE_THRESHOLD, MIN_BALANCE_FEE);
            }
            return true;
        } else {
            System.err.printf("Withdrawal of %.2f failed. Insufficient funds in Savings Account %s. Current balance: $%.2f%n",
                    amount, this.accountNumber, this.balance);
            return false;
        }
    }

    /**
     * Applies annual interest (simplified monthly application for demonstration).
     */
    @Override
    public void applyInterest() {
        double monthlyRate = this.annualInterestRate / 12.0;
        double interestGained = this.balance * monthlyRate;
        this.balance += interestGained;
        System.out.printf("Monthly interest applied (%.4f%%). Gained $%.2f. New balance: $%.2f%n",
                monthlyRate * 100, interestGained, this.balance);
    }

    public double getAnnualInterestRate() {
        return annualInterestRate;
    }
}