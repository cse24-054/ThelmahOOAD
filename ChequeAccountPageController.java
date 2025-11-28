package com.bankingsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL; // Added URL import
import java.util.Optional;

// NOTE: Renamed to ChequeAccountController for naming consistency
public class ChequeAccountPageController {

    // --- FXML Fields matching ChequeAccountPage.fxml ---
    @FXML private TextArea chequeTextArea;
    @FXML private Label balanceValueLabel; // Label showing the primary balance

    // Buttons
    @FXML private Button withdrawButton;
    @FXML private Button depositButton;
    @FXML private Button transferFundsButton;
    @FXML private Button viewTransactionButton;
    @FXML private Button exitButton;

    private Customer currentCustomer;
    private ChequeAccount chequeAccount;

    // --- Constant for identifying the account type for context passing ---
    public static final String CHEQUE_ACCOUNT_TYPE = "Cheque";

    /**
     * Called automatically after the FXML file is loaded.
     * Initializes the account display using the actual data of the logged-in customer.
     */
    @FXML
    public void initialize() {
        System.out.println("Initializing ChequeAccountController...");

        // 1. Get the current customer
        currentCustomer = LoginPageController.getLoggedInCustomer();

        if (currentCustomer == null) {
            balanceValueLabel.setText("ERROR");
            chequeTextArea.setText("Customer data not loaded. Please log in again.");
            System.err.println("Error: Customer data is null in ChequeAccountController.");
            return;
        }

        // 2. Find the Cheque Account
        Optional<ChequeAccount> accountOpt = currentCustomer.getChequeAccount();

        if (accountOpt.isPresent()) {
            chequeAccount = accountOpt.get();
            // 3. Display real data
            updateAccountDisplay();

        } else {
            // Handle case where customer doesn't have this account type
            balanceValueLabel.setText("N/A");
            chequeTextArea.setText("No Cheque Account found for this customer.");
        }

        chequeTextArea.setEditable(false);
    }

    /**
     * Updates the UI elements with the current account balance and details.
     */
    private void updateAccountDisplay() {
        if (chequeAccount != null) {
            // Format balance to two decimal places with comma separation
            balanceValueLabel.setText(String.format("$%,.2f", chequeAccount.getBalance()));

            // Display specific Cheque Account details
            chequeTextArea.setText(
                    String.format("Account Holder: %s\n", currentCustomer.getFullName()) +
                            String.format("Account Number: %s\n", chequeAccount.getAccountNumber()) +
                            String.format("Current Balance: $%,.2f\n", chequeAccount.getBalance()) +
                            String.format("Overdraft Limit: $%,.2f\n", chequeAccount.getOverdraftLimit()) +
                            "\n--- Account Features ---\n" +
                            "This account is ideal for day-to-day transactions and payments."
            );
        }
    }


    // --- ACTION HANDLERS (Updated FXML paths) ---

    @FXML
    private void handleWithdraw(ActionEvent event) {
        // Assume FXML file is named Withdraw.fxml
        navigateToActionPage(event, "/fxml/Withdraw.fxml", "Withdraw Funds", CHEQUE_ACCOUNT_TYPE);
    }

    @FXML
    private void handleDeposit(ActionEvent event) {
        // Assume FXML file is named Deposit.fxml
        navigateToActionPage(event, "/fxml/Deposit.fxml", "Deposit Funds", CHEQUE_ACCOUNT_TYPE);
    }

    @FXML
    private void handleTransferFunds(ActionEvent event) {
        // CRITICAL FIX: Changed from '/fxml/TransferPage.fxml' to '/fxml/Transfer.fxml'
        // based on common naming convention and previous file name.
        navigateToActionPage(event, "/fxml/transfer .fxml", "Transfer Funds", CHEQUE_ACCOUNT_TYPE);
    }


    @FXML
    private void handleViewTransaction(ActionEvent event) {
        // Assume FXML file is named ViewTransactions.fxml
        navigateToActionPage(event, "/fxml/ViewTransactions.fxml", "View Transactions", CHEQUE_ACCOUNT_TYPE);
    }

    /**
     * Handles the click event for the "Exit" button, which returns the user
     * to the Accounts Page (parent page/dashboard).
     */
    @FXML
    private void handleExit(ActionEvent event) {
        try {
            // Assuming this is the correct path to the main dashboard/accounts page
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/AccountsPage.fxml"));
            Scene scene = new Scene(root);

            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.setTitle("Bank App - My Dashboard");
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading DashboardPage.fxml.");
        }
    }

    /**
     * Helper method to load a new FXML scene and pass context (which account is active).
     * @param event The ActionEvent triggering the navigation.
     * @param fxmlPath The resource path to the FXML file.
     * @param title The new title for the window.
     * @param accountType The type of account being acted upon (e.g., "Cheque").
     */
    private void navigateToActionPage(ActionEvent event, String fxmlPath, String title, String accountType) {
        if (chequeAccount == null) {
            System.err.println("Cannot navigate: Cheque Account is null. Account data not initialized.");
            return;
        }

        try {
            // Use getClass().getResource() to find the file
            URL fxmlUrl = getClass().getResource(fxmlPath);

            if (fxmlUrl == null) {
                // If the resource is null, it means the file was not found, which is the cause of the IllegalStateException.
                System.err.println("ERROR: FXML resource not found at path: " + fxmlPath);
                // Throw an exception to halt execution if the resource is missing
                throw new IOException("FXML file not found: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // *** IMPORTANT CONTEXT PASSING LOGIC ***
            // This is the correct place to pass the source account (chequeAccount) to the transferController.
            Object controller = loader.getController();

            // Assuming transferController has a public method setSourceAccount(Account, String)
            if (controller instanceof transferController) {
                // Cast and call the setter to pass the required data
                ((transferController) controller).setSourceAccount(chequeAccount, accountType);
            } else {
                // Log a warning if the controller isn't the expected type
                System.out.println("Warning: Destination controller is not of type transferController. Data was not passed.");
            }

            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            window.setScene(scene);
            window.setTitle("Bank App - " + title);
            window.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Fatal Error: Could not load FXML file. Details: " + e.getMessage());
        }
    }
}