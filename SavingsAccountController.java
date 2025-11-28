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
import java.lang.reflect.Method;
import java.net.URL; // Added import for URL
import java.util.Optional;

/**
 * Controller for the Savings Account page. Fetches and displays data for the
 * logged-in customer's Savings Account. Simplified navigation without using
 * an ActionControllerBase interface.
 */
public class SavingsAccountController {

    // --- FXML Fields ---
    @FXML private TextArea savingsTextArea;
    @FXML private Label balanceValueLabel; // Main balance display

    @FXML private Button withdrawButton;
    @FXML private Button depositButton;
    @FXML private Button transferFundsButton;
    @FXML private Button viewTransactionButton;
    @FXML private Button exitButton;

    private Customer currentCustomer;
    private Savings savingsAccount;

    public static final String SAVINGS_ACCOUNT_TYPE = "Savings";


    /**
     * Called automatically after the FXML file is loaded.
     */
    @FXML
    public void initialize() {
        // 1. Get the currently logged-in customer
        currentCustomer = LoginPageController.getLoggedInCustomer();

        if (currentCustomer == null) {
            balanceValueLabel.setText("ERROR");
            savingsTextArea.setText("Customer data not loaded. Please log in again.");
            System.err.println("Error: Customer data is null in SavingsAccountController.");
            return;
        }

        // 2. Find the Savings Account object
        Optional<Savings> accountOpt = currentCustomer.getSavingsAccount();

        if (accountOpt.isPresent()) {
            savingsAccount = accountOpt.get(); // Store the specific Savings instance
            // 3. Display data
            updateAccountDisplay();
        } else {
            balanceValueLabel.setText("N/A");
            savingsTextArea.setText("No Savings Account found for this customer.");
        }

        savingsTextArea.setEditable(false);
    }

    /**
     * Updates the UI elements with the current account balance and details.
     */
    private void updateAccountDisplay() {
        if (savingsAccount != null) {
            balanceValueLabel.setText(String.format("$%,.2f", savingsAccount.getBalance()));

            savingsTextArea.setText(
                    String.format("Account Holder: %s\n", currentCustomer.getFullName()) +
                            String.format("Account Number: %s\n", savingsAccount.getAccountNumber()) +
                            String.format("Current Balance: $%,.2f\n", savingsAccount.getBalance()) +
                            String.format("Annual Interest Rate: %.2f%%\n", savingsAccount.getAnnualInterestRate() * 100) +
                            "\n--- Account Features ---\n" +
                            "This account is designed for saving money and earning interest."
            );
        }
    }


    // --- ACTION HANDLERS ---

    @FXML
    private void handleWithdraw(ActionEvent event) {
        if (savingsAccount != null) {
            navigateAndSetupController(event, "/fxml/WithdrawPage.fxml", "Withdraw Funds", WithdrawPageController.class);
        } else {
            System.err.println("Cannot withdraw: Savings Account is null.");
        }
    }

    @FXML
    private void handleDeposit(ActionEvent event) {
        if (savingsAccount != null) {
            navigateAndSetupController(event, "/fxml/DepositPage.fxml", "Deposit Funds", DepositPageController.class);
        } else {
            System.err.println("Cannot deposit: Savings Account is null.");
        }
    }

    @FXML
    private void handleTransferFunds(ActionEvent event) {
        if (savingsAccount != null) {
            // This is the call that was failing:
            navigateAndSetupController(event, "/fxml/transfer .fxml", "Transfer Funds", transferController.class);
        } else {
            System.err.println("Cannot transfer: Savings Account is null.");
        }
    }


    @FXML
    private void handleViewTransaction(ActionEvent event) {
        if (savingsAccount != null) {
            navigateAndSetupController(event, "/fxml/ViewTransactions.fxml", "View Transactions", ViewTransactionsController.class);
        } else {
            System.err.println("Cannot view transactions: Savings Account is null.");
        }
    }


    /**
     * Handles the click event for the "Exit" button, which returns the user
     * to the Accounts Page (parent page).
     */
    @FXML
    private void handleExit(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/AccountsPage.fxml"));
            Scene scene = new Scene(root);

            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.setTitle("Bank App - My Accounts");
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading AccountsPage.fxml.");
        }
    }

    /**
     * Helper method to load a new FXML scene and pass the account data to the destination controller.
     * Includes a check for the resource URL to provide a clearer error message for classpath issues.
     */
    private <T> void navigateAndSetupController(ActionEvent event, String fxmlPath, String title, Class<T> controllerClass) {
        try {
            // CRITICAL STEP: Get the resource URL
            URL fxmlUrl = getClass().getResource(fxmlPath);

            if (fxmlUrl == null) {
                System.err.println("FATAL ERROR: FXML resource not found!");
                System.err.println("Expected path: " + fxmlPath);
                System.err.println("1. Check that 'transfer.fxml' is located inside 'src/main/resources/fxml/'.");
                System.err.println("2. Ensure the file name and path capitalization are correct.");
                return; // Stop execution if resource is not found
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Get the controller instance
            T controller = loader.getController();

            if (controller != null) {
                // Use reflection to find the setSourceAccount method
                Method setupMethod = controller.getClass().getMethod("setSourceAccount", Account.class, String.class);

                // Invoke the method, passing the Savings object (which is also an Account)
                setupMethod.invoke(controller, savingsAccount, SAVINGS_ACCOUNT_TYPE);
            }


            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            window.setScene(scene);
            window.setTitle("Bank App - " + title);
            window.show();

        } catch (NoSuchMethodException e) {
            // Reflection error: the destination controller is missing the required setup method
            System.err.println("NAVIGATION ERROR: The destination controller is missing the required setup method.");
            System.err.println("Expected method signature in " + controllerClass.getName() + ":");
            System.err.println("    public void setSourceAccount(com.bankingsystem.Account account, java.lang.String accountType)");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("General Error navigating to " + fxmlPath + ": " + e.getMessage());
        }
    }
}