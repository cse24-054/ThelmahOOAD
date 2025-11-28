package com.bankingsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AccountsPageController implements Initializable {

    // --- FXML FIELDS ---
    // Fields for displaying customer data (from AccountsPage.fxml)
    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField addressField;

    // Optional FXML Button fields (removed if not directly manipulated)
    @FXML private Button savingsButton;
    @FXML private Button chequeButton;
    @FXML private Button investmentButton;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialization logic for loading customer data
        // This is usually done here if the controller is associated with the FXML root element.
        // For demonstration, these lines remain commented:
        // loggedInCustomer = SessionManager.getInstance().getCurrentCustomer();
        // if (loggedInCustomer != null) {
        //     displayCustomerDetails();
        // }
    }

    // Placeholder for displayCustomerDetails method if needed for context
    /* private void displayCustomerDetails() {
        nameField.setText(loggedInCustomer.getName());
        surnameField.setText(loggedInCustomer.getSurname());
        addressField.setText(loggedInCustomer.getAddress());

        nameField.setEditable(false);
        surnameField.setEditable(false);
        addressField.setEditable(false);
    }
    */

    // --- NEW HELPER METHOD FOR SIMPLE SCENE SWITCHING ---
    /**
     * Helper method to switch scenes using simple load (no data passed).
     */
    private void switchSceneSimple(ActionEvent event, String fxmlFileName, String title) {
        try {
            // 1. Load the FXML file from the /fxml/ directory
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/" + fxmlFileName));
            Scene scene = new Scene(root);

            // 2. Get the current Stage (Window)
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

            // 3. Set the new Scene
            window.setScene(scene);
            window.setTitle(title);
            window.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading FXML file: " + fxmlFileName + ". Check the file name and path (CASE SENSITIVE!).");
        }
    }

    // --- NAVIGATION HANDLERS (UPDATED TO USE SIMPLE SWITCH) ---

    @FXML
    private void handleSavingsAccountPage(ActionEvent event) {
        // Navigating to the dedicated com.bankingsystem.Savings com.bankingsystem.Account Page
        switchSceneSimple(event, "SavingsAccountPage.fxml", "Bank App - com.bankingsystem.Savings com.bankingsystem.Account Details");
    }

    @FXML
    private void handleChequeAccountPage(ActionEvent event) {
        // Navigating to the dedicated Cheque com.bankingsystem.Account Page
        switchSceneSimple(event, "ChequeAccountPage.fxml", "Bank App - Cheque com.bankingsystem.Account Details");
    }

    @FXML
    private void handleInvestmentAccountPage(ActionEvent event) {
        // Navigating to the dedicated com.bankingsystem.Investment com.bankingsystem.Account Page
        switchSceneSimple(event, "InvestmentAccountPage.fxml", "Bank App - com.bankingsystem.Investment com.bankingsystem.Account Details");
    }

    /**
     * Handles the click event for the "Back to Main Menu" button.
     */
    @FXML
    private void handleBackToMenu(ActionEvent event) {
        // Navigate back to the MainMenu.fxml.
        switchSceneSimple(event, "MainMenu.fxml", "Bank App - Main Menu");
    }
}