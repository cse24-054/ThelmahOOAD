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

// --- MISSING IMPORTS ADDED HERE ---
import com.bankingsystem.WithdrawPageController;
import com.bankingsystem.DepositPageController;
import com.bankingsystem.transferController;
import com.bankingsystem.ViewTransactionsController;
// ----------------------------------

public class InvestmentAccountPageController {

    // --- FXML Fields matching InvestmentAccountPage.fxml ---
    @FXML private TextArea investmentTextAtrea;
    @FXML private Label currentBalanceLabel;
    @FXML private Label balanceValueLabel;   // Used to display the dynamic balance
    @FXML private Button withdrawButton;
    @FXML private Button depositButton;
    @FXML private Button TransferButton;
    @FXML private Button viewTransactionButton;
    @FXML private Button exitButton;

    private Customer currentCustomer;
    private InvestmentAccount investmentAccount;

    // --- Constant for identifying the account type for context passing ---
    public static final String INVESTMENT_ACCOUNT_TYPE = "INVESTMENT";


    /**
     * Called automatically after the FXML file is loaded.
     * Initializes the account display using the actual data of the logged-in customer.
     */
    @FXML
    public void initialize() {
        // 1. Get the current customer established during login
        currentCustomer = LoginPageController.getLoggedInCustomer();

        if (currentCustomer == null) {
            balanceValueLabel.setText("ERROR");
            investmentTextAtrea.setText("Customer data not loaded. Please log in again.");
            System.err.println("Error: Customer data is null in InvestmentAccountPageController.");
            return;
        }

        // 2. Find the Investment Account
        Optional<Account> accountOpt = currentCustomer.getAccounts().stream()
                .filter(a -> a instanceof InvestmentAccount)
                .findFirst();

        if (accountOpt.isPresent()) {
            investmentAccount = (InvestmentAccount) accountOpt.get();

            // 3. Display real data
            updateAccountDisplay();

        } else {
            // Handle case where customer doesn't have this account type (should be prevented by AccountsPage)
            balanceValueLabel.setText("N/A");
            investmentTextAtrea.setText("No Investment Account found for this customer.");
        }

        investmentTextAtrea.setEditable(false);
    }

    /**
     * Updates the UI elements with the current account balance and details.
     */
    private void updateAccountDisplay() {
        if (investmentAccount != null) {
            // Format balance to two decimal places
            balanceValueLabel.setText(String.format("$%,.2f", investmentAccount.getBalance()));

            // Display specific Investment Account details
            investmentTextAtrea.setText(
                    String.format("Account Number: %s\n", investmentAccount.getAccountNumber()) +
                            String.format("Risk Level: %s\n", investmentAccount.getRiskLevel()) +
                            String.format("Expected Annual Return: %.2f%%\n", investmentAccount.getExpectedAnnualReturn() * 100) +
                            "\n--- Portfolio Summary ---\n" +
                            investmentAccount.getInvestmentDetails() +
                            "\n\nNote: This account is designed for long-term growth and may include penalties for early withdrawal. " +
                            "Current balance reflects market value."
            );
        }
    }


    // --- ACTION HANDLERS ---

    @FXML
    private void handleWithdraw(ActionEvent event) {
        navigateToActionPage(event, "/fxml/WithdrawPage.fxml", "Withdraw Funds", INVESTMENT_ACCOUNT_TYPE, WithdrawPageController.class);
    }

    @FXML
    private void handleDeposit(ActionEvent event) {
        navigateToActionPage(event, "/fxml/DepositPage.fxml", "Deposit Funds", INVESTMENT_ACCOUNT_TYPE, DepositPageController.class);
    }

    @FXML
    private void handleTransfer(ActionEvent event) {
        // Transfer involves selecting a target account, but we still identify the source account.
        navigateToActionPage(event, "/fxml/TransferPage.fxml", "Transfer Funds", INVESTMENT_ACCOUNT_TYPE, transferController.class);
    }

    @FXML
    private void handleViewTransaction(ActionEvent event) {
        navigateToActionPage(event, "/fxml/ViewTransactions.fxml", "View Transactions", INVESTMENT_ACCOUNT_TYPE, ViewTransactionsController.class);
    }


    /**
     * Handles the click event for the "Exit" button, which returns the user
     * to the Accounts Page (parent page).
     */
    @FXML
    private void handleExit(ActionEvent event) {
        try {
            // Load the AccountsPage.fxml from the correct resource path
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/AccountsPage.fxml"));
            Scene scene = new Scene(root);

            // Get the current Stage (Window) and set the new Scene
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.setTitle("Bank App - My Accounts");
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading AccountsPage.fxml. Check the file path and existence.");
        }
    }

    /**
     * Helper method to load a new FXML scene and pass context using a specific controller class.
     * @param event The ActionEvent triggering the navigation.
     * @param fxmlPath The resource path to the FXML file.
     * @param title The new title for the window.
     * @param accountType The type of account being acted upon.
     * @param controllerClass The expected controller class for type safety.
     */
    private <T> void navigateToActionPage(ActionEvent event, String fxmlPath, String title, String accountType, Class<T> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // *** IMPORTANT CONTEXT PASSING LOGIC ***
            T controller = loader.getController();

            if (controller instanceof WithdrawPageController withdrawController) {
                withdrawController.setSourceAccount(investmentAccount, accountType);
            } else if (controller instanceof DepositPageController depositController) {
                depositController.setSourceAccount(investmentAccount, accountType);
            } else if (controller instanceof transferController transferController) {
                transferController.setSourceAccount(investmentAccount, accountType);
            } else if (controller instanceof ViewTransactionsController transactionController) {
                transactionController.setSourceAccount(investmentAccount, accountType);
            } else {
                System.err.println("Error: Controller type mismatch or 'setSourceAccount' method not found for " + controllerClass.getSimpleName());
            }


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