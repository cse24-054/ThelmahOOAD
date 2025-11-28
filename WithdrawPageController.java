package com.bankingsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WithdrawPageController {

    // --- FXML Element Connections ---
    @FXML
    private TextField amountField;

    @FXML
    private TextField withdrawField; // Assumed to be the "Withdraw From" field (e.g., account type/number)

    @FXML
    private TextField numberField; // Assumed to be the "Phone Number" field

    @FXML
    private Label messageLabel;

    // --- Account State ---
    // FIX: Changed to use the base class 'Account' so it can accept Savings, Investment, or Cheque accounts.
    private Account sourceAccount;
    private String sourceAccountType;


    /**
     * REQUIRED: This method is called by the account page controllers (Savings, Investment, etc.)
     * to pass the specific account data to this page.
     * * @param account The specific Account instance selected for withdrawal (can be Savings, ChequeAccount, or InvestmentAccount).
     * @param accountType The type of the account (e.g., "SAVINGS", "INVESTMENT", "CHEQUE").
     */
    // FIX: Updated method signature to accept the generic 'Account' object.
    public void setSourceAccount(Account account, String accountType) {
        this.sourceAccount = account;
        this.sourceAccountType = accountType;

        // Pre-fill the "Withdraw From" field with account details
        if (account != null) {
            withdrawField.setText(accountType + " (" + account.getAccountNumber() + ")");
            withdrawField.setDisable(true); // Prevent user from changing the source account
        }
    }


    /**
     * Handles the withdrawal action triggered by the "Done" button.
     * 1. Validates input.
     * 2. Attempts to perform the withdrawal on the source Account object.
     * 3. Logs the transaction details to the appropriate file upon success.
     */
    @FXML
    private void handleWithdraw(ActionEvent event) {
        // FIX: Check against the generic sourceAccount
        if (sourceAccount == null) {
            messageLabel.setText("Error: Account data not loaded.");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        String amountText = amountField.getText();

        // 1. Basic Validation
        if (amountText.trim().isEmpty()) {
            messageLabel.setText("Error: Please enter a withdrawal amount.");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        try {
            // 2. Numeric Validation
            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                messageLabel.setText("Error: Amount must be greater than zero.");
                messageLabel.setTextFill(javafx.scene.paint.Color.RED);
                return;
            }

            // 3. Perform the withdrawal on the actual Account object
            // This relies on the polymorphic 'withdraw' method being correctly implemented in all subclasses (Savings, Cheque, Investment)
            boolean success = sourceAccount.withdraw(amount);

            if (!success) {
                // Withdrawal failed (likely insufficient funds)
                messageLabel.setText(
                        String.format("Withdrawal failed. Insufficient funds in account %s. Current Balance: $%,.2f",
                                sourceAccount.getAccountNumber(),
                                sourceAccount.getBalance())
                );
                messageLabel.setTextFill(javafx.scene.paint.Color.RED);
                return;
            }

            // --- 4. Logging the Successful Transaction ---
            // Determine filename based on account type (assuming savings.txt is for all savings, and others use other files)
            // For now, let's simplify and log to a generic file or rely on the transaction history in the Account class (if implemented).
            // Based on your original code, we will continue logging to "savings.txt" if the source account is Savings, otherwise, you may need a system to choose the file name dynamically.
            String filename = "transactions_log.txt"; // Changed to a generic log file for simplicity across accounts
            if (sourceAccount instanceof Savings) {
                filename = "savings.txt";
            } else if (sourceAccount instanceof InvestmentAccount) {
                filename = "investment.txt";
            } else if (sourceAccount instanceof ChequeAccount) {
                filename = "cheque.txt";
            }


            // Get current timestamp
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String timestamp = dtf.format(now);

            // Format the transaction record, using the account number from the stored object
            String transactionRecord = String.format(
                    "[%s] WITHDRAWAL | Account Type: %s | Account: %s | Amount: $%.2f | New Balance: $%,.2f | Phone: %s\n",
                    timestamp,
                    sourceAccountType,
                    sourceAccount.getAccountNumber(),
                    amount,
                    sourceAccount.getBalance(), // Use the updated balance for logging
                    numberField.getText().trim().isEmpty() ? "N/A" : numberField.getText()
            );

            // Using try-with-resources with BufferedWriter for efficient appending
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
                bw.write(transactionRecord);

                // 5. Success Feedback
                messageLabel.setText(String.format("Successfully withdrew $%.2f from %s. New Balance: $%,.2f. Record saved to %s.",
                        amount,
                        sourceAccount.getAccountNumber(),
                        sourceAccount.getBalance(),
                        filename));
                messageLabel.setTextFill(javafx.scene.paint.Color.web("#38a169"));

                // Clear fields after successful transaction
                amountField.clear();
                numberField.clear();

            } catch (IOException e) {
                // Handle file writing errors
                e.printStackTrace();
                messageLabel.setText("System Error: Withdrawal succeeded, but failed to save transaction record. Check file permissions.");
                messageLabel.setTextFill(javafx.scene.paint.Color.ORANGE); // Use orange for partial success/logging error
            }

        } catch (NumberFormatException e) {
            // Handle invalid input format
            messageLabel.setText("Error: Invalid amount entered. Please enter a valid number (e.g., 100.00).");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    /**
     * Handles navigation back to the Accounts Page (parent page).
     * Since this controller is now generic, it navigates back to the main AccountsPage.
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Load the Accounts Page (where the user chooses the account)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccountsPage.fxml"));
            Parent root = loader.load();

            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            window.setScene(scene);
            window.setTitle("Bank App - My Accounts");
            window.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading AccountsPage.fxml. Cannot navigate back.");
        }
    }
}