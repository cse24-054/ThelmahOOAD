package com.bankingsystem;

/**
 * Represents an Investment Account, which typically has high interest but restrictions on withdrawals.
 * This class now includes specific details like risk level and investment breakdown for the UI.
 */
public class InvestmentAccount extends Account {
    private double annualReturnRate;
    private static final double WITHDRAWAL_PENALTY_RATE = 0.05; // 5% penalty

    // Investment-specific fields needed by the controller
    private String riskLevel = "Medium";
    private String investmentDetails = "Diversified portfolio of 60% technology ETFs and 40% government bonds.";

    /**
     * Constructor for InvestmentAccount.
     * @param accountNumber Unique account number.
     * @param initialBalance Starting balance.
     * @param annualReturnRate The expected annual return rate (e.g., 0.04 for 4%).
     * @param branch The branch where the account is held.
     */
    public InvestmentAccount(String accountNumber, double initialBalance, double annualReturnRate, String branch) {
        // CALL to Account constructor, now requiring branch
        super(accountNumber, branch);

        // Set the initial balance manually
        this.balance = initialBalance;
        this.annualReturnRate = annualReturnRate;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.err.println("Withdrawal amount must be positive.");
            return false;
        }

        if (this.balance >= amount) {
            double penalty = amount * WITHDRAWAL_PENALTY_RATE;
            double totalDeduction = amount + penalty;

            if (this.balance >= totalDeduction) {
                this.balance -= totalDeduction;
                System.out.printf("Withdrawal of %.2f successful from Investment Account %s. Penalty of $%.2f applied. Remaining balance: $%.2f%n",
                        amount, this.accountNumber, penalty, this.balance);
                return true;
            } else {
                System.err.printf("Withdrawal of %.2f failed. Insufficient funds to cover amount plus penalty of $%.2f. Current balance: $%.2f%n",
                        amount, penalty, this.balance);
                return false;
            }
        } else {
            System.err.printf("Withdrawal of %.2f failed. Insufficient funds in Investment Account %s. Current balance: $%.2f%n",
                    amount, this.accountNumber, this.balance);
            return false;
        }
    }

    /**
     * Applies the annual return rate (simplified monthly application).
     */
    @Override
    public void applyInterest() {
        double monthlyRate = this.annualReturnRate / 12.0;
        double returnGained = this.balance * monthlyRate;
        this.balance += returnGained;
        System.out.printf("Monthly return applied (%.4f%%). Gained $%.2f. New balance: $%.2f%n",
                monthlyRate * 100, returnGained, this.balance);
    }

    // --- Methods added to support the InvestmentAccountController UI ---

    /**
     * Returns the risk level associated with this investment.
     */
    public String getRiskLevel() {
        return riskLevel;
    }

    /**
     * Returns the detailed description of the investment portfolio.
     */
    public String getInvestmentDetails() {
        return investmentDetails;
    }

    /**
     * Renamed from getAnnualReturnRate to match controller usage (getExpectedAnnualReturn).
     * Returns the expected annual rate of return.
     */
    public double getExpectedAnnualReturn() {
        return annualReturnRate;
    }
}