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

        // 1. FIX: Get the current customer using the CORRECT static method name
        // as defined in your LoginPageController: getLoggedInCustomer().
        currentCustomer = LoginPageController.getLoggedInCustomer();

        if (currentCustomer == null) {
            balanceValueLabel.setText("ERROR");
            chequeTextArea.setText("Customer data not loaded. Please log in again.");
            System.err.println("Error: Customer data is null in ChequeAccountController.");
            return;
        }

        // 2. Find the Cheque Account using the dedicated, type-safe method (assumed to be in Customer.java)
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

            // Display specific Cheque Account details, including the Overdraft Limit
            // NOTE: The Overdraft Limit label in FXML is fx:id="overdraftLimitLabel".
            // We should use it here if it were declared in this Java file.
            // For now, it's just shown in the TextArea.
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


    // --- ACTION HANDLERS (Updated to pass context) ---

    @FXML
    private void handleWithdraw(ActionEvent event) {
        navigateToActionPage(event, "/fxml/WithdrawPage.fxml", "Withdraw Funds", CHEQUE_ACCOUNT_TYPE);
    }

    @FXML
    private void handleDeposit(ActionEvent event) {
        navigateToActionPage(event, "/fxml/DepositPage.fxml", "Deposit Funds", CHEQUE_ACCOUNT_TYPE);
    }

    @FXML
    private void handleTransferFunds(ActionEvent event) {
        navigateToActionPage(event, "/fxml/TransferPage.fxml", "Transfer Funds", CHEQUE_ACCOUNT_TYPE);
    }


    @FXML
    private void handleViewTransaction(ActionEvent event) {
        // Assuming a generic Transaction View page exists
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // *** IMPORTANT CONTEXT PASSING LOGIC ***
            // Assuming your target controllers (Withdraw/Deposit/Transfer) implement
            // an interface like 'ActionControllerBase' with a 'setSourceAccount' method.
            Object controller = loader.getController();

            // Since I don't have the definition for ActionControllerBase, I'll leave the
            // required logic commented out to ensure compilation, but this is where
            // you'd pass the 'chequeAccount' object:
            // if (controller instanceof ActionControllerBase actionController) {
            //     actionController.setSourceAccount(chequeAccount, accountType);
            // }


            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            window.setScene(scene);
            window.setTitle("Bank App - " + title);
            window.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading " + fxmlPath + ". Ensure the file exists and the path is correct.");
        }
    }
}