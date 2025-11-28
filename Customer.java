package com.bankingsystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional; // Required for safe retrieval of specific account types

/**
 * Represents a Customer of the banking system.
 * Holds personal details, login credentials, and a list of all their accounts (using the Account base class).
 */
public class Customer {
    private final String firstName;
    private final String lastName;
    private final String dateOfBirth;
    private final String phoneNumber;
    private final String email;
    private final String customerCode; // Used as the login ID
    private final String password;

    // The key change: List to hold the customer's various account objects.
    private final List<Account> accounts;

    /**
     * Complete constructor for the Customer class.
     */
    public Customer(String firstName, String lastName, String dateOfBirth, String phoneNumber, String email, String customerCode, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.customerCode = customerCode;
        this.password = password;
        this.accounts = new ArrayList<>();
    }

    /**
     * Adds an account to the customer's list of accounts.
     * Used exclusively during mock data initialization (Database.java).
     */
    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    // --- ESSENTIAL ACCOUNT RETRIEVAL METHODS ---

    /**
     * Retrieves the specific Savings account object if it exists, using Optional for safety.
     * @return An Optional containing the Savings object, or empty if none is found.
     */
    public Optional<Savings> getSavingsAccount() {
        // Find the first account in the list that is an instance of the Savings class
        return accounts.stream()
                .filter(account -> account instanceof Savings)
                .map(account -> (Savings) account) // Cast it to the specific Savings type
                .findFirst();
    }

    /**
     * Retrieves the specific Cheque account object if it exists.
     * @return An Optional containing the ChequeAccount object, or empty if none is found.
     */
    public Optional<ChequeAccount> getChequeAccount() {
        return accounts.stream()
                .filter(account -> account instanceof ChequeAccount)
                .map(account -> (ChequeAccount) account)
                .findFirst();
    }

    /**
     * Retrieves the specific com.bankingsystem.Investment account object if it exists.
     * @return An Optional containing the InvestmentAccount object, or empty if none is found.
     */
    public Optional<InvestmentAccount> getInvestmentAccount() {
        return accounts.stream()
                .filter(account -> account instanceof InvestmentAccount)
                .map(account -> (InvestmentAccount) account)
                .findFirst();
    }

    // --- Getters ---

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public String getPassword() {
        return password;
    }

    public List<Account> getAccounts() {
        // Return an unmodifiable view to prevent external modification
        return Collections.unmodifiableList(accounts);
    }

    // Utility to get a customer's full name
    public String getFullName() {
        return firstName + " " + lastName;
    }
}