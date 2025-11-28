package com.bankingsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class transferController {

    // --- FXML Element Connections ---
    @FXML private TextField amountField;
    @FXML private MenuButton transferToButton;
    @FXML private Label sourceAccountLabel; // Displays the source account details and current balance
    @FXML private Label messageLabel;
    @FXML private Button transferButton;

    // --- Account State ---
    private Account sourceAccount;
    private String sourceAccountType;
    private Customer currentCustomer;

    // Map to hold all of the customer's accounts (except the source) for the 'Transfer To' dropdown
    private final Map<String, Account> destinationAccountMap = new HashMap<>();
    private Account selectedDestinationAccount;

    // --- Initialization ---

    /**
     * Initializes the controller. Called automatically after the FXML is loaded.
     */
    @FXML
    public void initialize() {
        messageLabel.setText("");
        transferToButton.setText("Select Destination Account");
    }

    /**
     * REQUIRED: Method to receive the source account object and set up the destination dropdown.
     * This method is called by the previous controller (e.g., SavingsAccountPageController)
     * before the transfer view is displayed.
     * @param account The specific Account instance selected as the source.
     * @param accountType The type of the source account (e.g., "Savings", "Investment").
     */
    public void setSourceAccount(Account account, String accountType) {
        this.sourceAccount = account;
        this.sourceAccountType = accountType;
        // Retrieve the logged-in customer data
        this.currentCustomer = LoginPageController.getLoggedInCustomer();

        if (this.sourceAccount == null || this.currentCustomer == null) {
            messageLabel.setText("System Error: Account or Customer data missing.");
            return;
        }

        // Update the label to show the user which account they are transferring from
        updateSourceAccountLabel();

        // Populate the destination dropdown
        populateDestinationDropdown();
    }

    /**
     * Updates the source account label with current balance information.
     */
    private void updateSourceAccountLabel() {
        sourceAccountLabel.setText(String.format("Transferring from: %s (%s) | Balance: $%,.2f",
                sourceAccountType,
                sourceAccount.getAccountNumber(),
                sourceAccount.getBalance()));
    }

    /**
     * Populates the destination dropdown with all customer accounts, excluding the source.
     */
    private void populateDestinationDropdown() {
        transferToButton.getItems().clear();
        destinationAccountMap.clear();

        // Get all accounts associated with the current customer
        for (Account account : currentCustomer.getAccounts()) {
            // Do not allow transferring to the source account itself
            if (account.getAccountNumber().equals(sourceAccount.getAccountNumber())) {
                continue;
            }

            // Use the simple class name (e.g., "Savings", "ChequeAccount") for display type
            String displayType = account.getClass().getSimpleName();
            String accountDisplay = String.format("%s (%s)", displayType, account.getAccountNumber());

            destinationAccountMap.put(accountDisplay, account);

            MenuItem toItem = new MenuItem(accountDisplay);
            toItem.setOnAction(e -> {
                transferToButton.setText(toItem.getText());
                selectedDestinationAccount = destinationAccountMap.get(toItem.getText());
                messageLabel.setText(""); // Clear status message on selection change
            });
            transferToButton.getItems().add(toItem);
        }

        if (transferToButton.getItems().isEmpty()) {
            transferToButton.setText("No other accounts available");
            transferToButton.setDisable(true);
            transferButton.setDisable(true);
        }
    }


    // --- Core Action Logic ---

    /**
     * Handles the fund transfer action triggered by the "Transfer" button.
     */
    @FXML
    private void handleTransfer(ActionEvent event) {
        String amountText = amountField.getText();

        // 1. Validation
        if (amountText.trim().isEmpty() || selectedDestinationAccount == null) {
            messageLabel.setText("Error: Please select a destination and enter an amount.");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                messageLabel.setText("Error: Amount must be greater than zero.");
                messageLabel.setTextFill(javafx.scene.paint.Color.RED);
                return;
            }

            // 2. Perform Transaction: Attempt withdrawal from Source
            boolean withdrawalSuccess = sourceAccount.withdraw(amount);

            if (!withdrawalSuccess) {
                messageLabel.setText(
                        String.format("Transfer failed. Insufficient funds in source account. Balance: $%,.2f",
                                sourceAccount.getBalance())
                );
                messageLabel.setTextFill(javafx.scene.paint.Color.RED);
                return;
            }

            // 3. Perform Transaction: Deposit to Destination
            selectedDestinationAccount.deposit(amount);


            // --- 4. Logging the Successful Transaction ---
            // Log transaction in *both* files (source and destination)
            String sourceFilename = getLogFileName(sourceAccount);
            String destFilename = getLogFileName(selectedDestinationAccount);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String timestamp = dtf.format(now);

            String sourceAccountNum = sourceAccount.getAccountNumber();
            String destAccountNum = selectedDestinationAccount.getAccountNumber();
            String sourceAccountTypeDisplay = sourceAccount.getClass().getSimpleName();
            String destAccountTypeDisplay = selectedDestinationAccount.getClass().getSimpleName();

            // Log Record for Source (Withdrawal - TRANSFER OUT)
            String sourceTransactionRecord = String.format(
                    "[%s] TRANSFER OUT | Type: %s | To Account: %s (%s) | Amount: $%.2f | New Balance: $%,.2f\n",
                    timestamp,
                    sourceAccountTypeDisplay,
                    destAccountNum,
                    destAccountTypeDisplay,
                    amount,
                    sourceAccount.getBalance()
            );

            // Log Record for Destination (Deposit - TRANSFER IN)
            String destTransactionRecord = String.format(
                    "[%s] TRANSFER IN | Type: %s | From Account: %s (%s) | Amount: $%.2f | New Balance: $%,.2f\n",
                    timestamp,
                    destAccountTypeDisplay,
                    sourceAccountNum,
                    sourceAccountTypeDisplay,
                    amount,
                    selectedDestinationAccount.getBalance()
            );

            // Use try-with-resources for reliable file writing
            try (BufferedWriter bwSource = new BufferedWriter(new FileWriter(sourceFilename, true));
                 BufferedWriter bwDest = new BufferedWriter(new FileWriter(destFilename, true))) {

                bwSource.write(sourceTransactionRecord);
                bwDest.write(destTransactionRecord);


                // 5. Success Feedback and UI Update
                messageLabel.setText(String.format("Successfully transferred $%.2f from %s to %s. Source Bal: $%,.2f. Dest Bal: $%,.2f.",
                        amount,
                        sourceAccountNum,
                        destAccountNum,
                        sourceAccount.getBalance(),
                        selectedDestinationAccount.getBalance()));
                messageLabel.setTextFill(javafx.scene.paint.Color.web("#38a169")); // Green color for success

                // Clear input fields and reset destination selection
                amountField.clear();
                transferToButton.setText("Select Destination Account");
                selectedDestinationAccount = null;

                // Update the source balance display
                updateSourceAccountLabel();

            } catch (IOException e) {
                // If the transaction succeeded in the model but failed to log to disk
                e.printStackTrace();
                messageLabel.setText("System Warning: Transfer succeeded, but failed to save transaction records to file.");
                messageLabel.setTextFill(javafx.scene.paint.Color.ORANGE);
            }

        } catch (NumberFormatException e) {
            messageLabel.setText("Error: Invalid amount entered. Please enter a valid number (e.g., 100.00).");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    /**
     * Helper to determine the transaction log file based on the specific account type instance.
     * @param account The account instance.
     * @return The filename for the transaction log.
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
     * Handles navigation back to the original Account Page using the stored source account type.
     */
    @FXML
    private void handleBack(ActionEvent event) {
        String fxmlPath = "";
        String title = "";

        // Determine which page to return to based on the source account's type
        if (sourceAccount instanceof Savings) {
            fxmlPath = "/fxml/SavingsAccountPage.fxml";
            title = "Savings Account";
        } else if (sourceAccount instanceof InvestmentAccount) {
            fxmlPath = "/fxml/InvestmentAccountPage.fxml";
            title = "Investment Account";
        } else if (sourceAccount instanceof ChequeAccount) {
            fxmlPath = "/fxml/ChequeAccountPage.fxml";
            title = "Cheque Account";
        } else {
            fxmlPath = "/fxml/AccountsPage.fxml"; // Fallback to a general accounts view
            title = "My Accounts";
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Note: If the target page needs data (like the source account object itself),
            // you would call a set data method on the target controller here, similar to how
            // setSourceAccount was called on this controller.

            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            window.setScene(scene);
            window.setTitle("Bank App - " + title);
            window.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading " + fxmlPath + ". Cannot navigate back.");
            // Optionally, show an error message in the UI if navigation fails
        }
    }
}