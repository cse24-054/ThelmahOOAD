package com.bankingsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

// NOTE: Corrected class name to follow Java naming convention (PascalCase: LoginPageController)
public class LoginPageController {

    // FXML elements that match the 'fx:id' in your FXML file
    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label messageLabel;

    // --- CRITICAL: Application State Management ---
    // This static variable holds the authenticated user for the entire application session.
    private static Customer loggedInCustomer;

    /**
     * Public static method to retrieve the currently logged-in customer.
     * Other controllers (e.g., ChequeAccountController) call this to get
     * the customer's data and account balances.
     * @return The authenticated Customer object, or null if no one is logged in.
     */
    public static Customer getLoggedInCustomer() {
        return loggedInCustomer;
    }

    /**
     * Handles the login button click, verifying credentials against the Database
     * and navigating to the Main Menu upon success.
     */
    @FXML
    private void handlelogin (ActionEvent event) {

        String customerCode = usernameField.getText().toUpperCase(); // Customer Codes are often uppercase
        String inputPassword = passwordField.getText();

        // 1. Attempt to find the customer in the mock database
        Optional<Customer> customerOpt = Database.getInstance().findCustomerByCode(customerCode);

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();

            // 2. Customer found. Now, verify the password stored in the Customer object.
            if (customer.getPassword().equals(inputPassword)) {

                // --- SUCCESSFUL LOGIN LOGIC ---

                // Set the application state for global access
                loggedInCustomer = customer;

                messageLabel.setText("Login successful for " + customer.getFirstName() + "! Redirecting...");

                // Navigate to the Main Menu
                try {
                    loadMainMenu(event);
                } catch (IOException e) {
                    messageLabel.setText("Login successful, but failed to load Main Menu. Check FXML path.");
                    e.printStackTrace();
                }

            } else {
                // --- FAILED LOGIN LOGIC (Incorrect Password) ---
                messageLabel.setText("Error: Invalid password.");
                passwordField.clear();
            }

        } else {
            // --- FAILED LOGIN LOGIC (Customer Not Found) ---
            messageLabel.setText("Error: Customer code not found.");
            usernameField.clear();
            passwordField.clear();
        }
    }

    /**
     * Utility method to load the Main Menu FXML scene.
     */
    private void loadMainMenu(ActionEvent event) throws IOException {
        final String FXML_PATH = "/fxml/MainMenu.fxml";
        URL fxmlUrl = getClass().getResource(FXML_PATH);

        if (fxmlUrl == null) {
            messageLabel.setText("FATAL ERROR: MainMenu.fxml not found at " + FXML_PATH + ". Check file location.");
            throw new IOException("MainMenu.fxml not found at " + FXML_PATH);
        }

        Parent root = FXMLLoader.load(fxmlUrl);
        Scene scene = new Scene(root);

        // Get the Stage (window) from the event source
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

        window.setScene(scene);
        window.setTitle("Bank App - Main Menu");
        window.show();
    }
}