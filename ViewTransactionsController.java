package com.bankingsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.BufferedReader; // Missing import added
import java.io.FileReader;    // Missing import added
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ViewTransactionsController implements Initializable {

    @FXML
    private TextArea transactionArea;

    // --- Account Context State ---
    private Account sourceAccount;
    private String accountType; // e.g., "SAVINGS", "INVESTMENT"
    private String filename;

    /**
     * REQUIRED: Method to receive the source account object and its type.
     * Signature matches the reflection call (Account, String).
     */
    public void setSourceAccount(Account account, String type) {
        this.sourceAccount = account;
        this.accountType = type;

        // Dynamically set the filename based on the account type
        this.filename = getLogFileName(account);

        // Reload transactions after context is set (since context isn't available in initialize())
        // Ensure FXML elements are loaded before attempting to access them
        if (transactionArea != null) {
            loadTransactionsFromFile();
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
     * Initializes the controller. Note: We cannot load transactions here as the
     * setSourceAccount method hasn't been called yet.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (transactionArea != null) {
            transactionArea.setText("Loading transactions...");
        }
    }

    /**
     * Reads all lines from the dynamically determined file and displays them.
     */
    private void loadTransactionsFromFile() {
        if (this.filename == null || this.sourceAccount == null) {
            transactionArea.setText("ERROR: No account context provided to load transactions.");
            return;
        }

        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(this.filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            if (content.length() == 0) {
                transactionArea.setText(String.format("--- Transactions for %s Account (%s) ---\nNo transactions recorded yet in %s.",
                        accountType, sourceAccount.getAccountNumber(), this.filename));
            } else {
                transactionArea.setText(String.format("--- Transactions for %s Account (%s) ---\n%s",
                        accountType, sourceAccount.getAccountNumber(), content.toString()));
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + this.filename + ". Details: " + e.getMessage());
            transactionArea.setText("ERROR: Could not read transaction file (" + this.filename + "). " +
                    "Please ensure the file exists and is accessible.");
        }
    }

    /**
     * Handles navigation back to the original Account Page.
     */
    @FXML
    private void handleBackToMenu(ActionEvent event) {
        // Navigate back to the specific account page using the stored account type
        String fxmlPath = "";
        String title = "";

        // Determine which page to return to
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
            // Load the determined FXML
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = new Scene(root);

            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.setTitle("Bank App - " + title);
            window.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading FXML. Cannot navigate back.");
        }
    }
}
