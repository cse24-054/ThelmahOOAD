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

public class DepositPageController {

    // --- FXML Element Connections ---
    @FXML
    private TextField amountField;

    @FXML
    private TextField depositToField; // Corresponds to "Deposit To"

    @FXML
    private TextField numberField; // Corresponds to "Phone Number"

    @FXML
    private Label messageLabel; // Used for success/error feedback

    // --- Account State ---
    private Account sourceAccount;
    private String sourceAccountType;


    /**
     * REQUIRED: This method is called by the main account controllers (Savings, Investment, etc.)
     * to pass the specific account data to this page. Signature matches the reflection call (Account, String).
     * @param account The specific Account instance selected for deposit.
     * @param accountType The type of the account (e.g., "Savings", "Investment").
     */
    public void setSourceAccount(Account account, String accountType) {
        this.sourceAccount = account;
        this.sourceAccountType = accountType;

        // Pre-fill the "Deposit To" field with account details and disable it
        if (account != null) {
            depositToField.setText(accountType + " (" + account.getAccountNumber() + ")");
            depositToField.setDisable(true);
        }
    }


    /**
     * Handles the deposit action triggered by the "Done" button.
     */
    @FXML
    private void handlesDeposit(ActionEvent event) {
        if (sourceAccount == null) {
            messageLabel.setText("Error: Account data not loaded. Cannot process deposit.");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        String amountText = amountField.getText();

        // 1. Basic Validation
        if (amountText.trim().isEmpty()) {
            messageLabel.setText("Error: Please enter a deposit amount.");
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

            // Perform the deposit on the actual Account object
            sourceAccount.deposit(amount);

            // --- Logging the Successful Transaction ---
            String filename = getLogFileName(sourceAccount);

            // Get current timestamp
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String timestamp = dtf.format(now);

            // Format the transaction record, including the updated balance
            String transactionRecord = String.format(
                    "[%s] DEPOSIT | Account Type: %s | Account: %s | Amount: $%.2f | New Balance: $%,.2f | Phone: %s\n",
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

                // 3. Success Feedback
                messageLabel.setText(String.format("Successfully deposited $%.2f to %s Account. New Balance: $%,.2f. Record saved to %s.",
                        amount,
                        sourceAccountType,
                        sourceAccount.getBalance(),
                        filename));
                messageLabel.setTextFill(javafx.scene.paint.Color.web("#38a169"));

                // Clear fields after successful transaction
                amountField.clear();
                numberField.clear();

            } catch (IOException e) {
                // Handle file writing errors
                e.printStackTrace();
                messageLabel.setText("System Error: Deposit succeeded, but failed to save transaction record.");
                messageLabel.setTextFill(javafx.scene.paint.Color.ORANGE);
            }

        } catch (NumberFormatException e) {
            // Handle invalid input format
            messageLabel.setText("Error: Invalid amount entered. Please enter a valid number (e.g., 100.00).");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    /**
     * Helper to determine the transaction log file based on account type.
     */
    private String getLogFileName(Account account) {
        if (account instanceof Savings) {
            return "savings.txt";
        } else if (account instanceof InvestmentAccount) {
            return "investment.txt";
        } else if (account instanceof ChequeAccount) {
            return "cheque.txt";
        }
        return "transactions_log.txt"; // Default fallback
    }


    /**
     * Handles navigation back to the original Account Page using the stored account type.
     */
    @FXML
    private void handleBack(ActionEvent event) {
        String fxmlPath = "";
        String title = "";

        // Determine which page to return to based on the source type
        if (sourceAccount instanceof Savings) {
            fxmlPath = "/fxml/SavingsAccountPage.fxml";
            title = "Savings Account";
        } else if (sourceAccount instanceof InvestmentAccount) {
            fxmlPath = "/fxml/InvestmentAccountPage.fxml";
            title = "Investment Account";
        } else if (sourceAccount instanceof ChequeAccount) {
            fxmlPath = "/fxml/ChequeAccountPage.fxml"; // Assuming you have this
            title = "Cheque Account";
        } else {
            fxmlPath = "/fxml/AccountsPage.fxml"; // Fallback
            title = "My Accounts";
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            window.setScene(scene);
            window.setTitle("Bank App - " + title);
            window.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading " + fxmlPath + ". Cannot navigate back.");
        }
    }
}