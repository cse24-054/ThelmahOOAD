package com.bankingsystem;

public abstract class Account {
    // Encapsulated (protected) attributes - accessible by subclasses
    protected double balance;
    protected String accountNumber;
    protected String branch;

    /**
     * Constructor to initialize shared account properties.
     * @param accountNumber The unique identifier for the account.
     * @param branch The bank branch where the account is held.
     */
    public Account(String accountNumber, String branch) {
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.balance = 0.0; // Default starting balance
    }

    /**
     * Deposit method shared by all account types.
     * @param amount The amount to deposit.
     */
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

    /**
     * Abstract method for applying interest or fees.
     * Implemented by subclasses (e.g., Savings, Investment).
     */
    public abstract void applyInterest();

    /**
     * Abstract method for withdrawing funds.
     * FIX: The return type is changed from 'void' to 'boolean'.
     * @param amount The amount to withdraw.
     * @return true if the withdrawal was successful (e.g., sufficient funds), false otherwise.
     */
    public abstract boolean withdraw(double amount);
}