package com.bankingsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainMenuController {

    @FXML private Button AccountsButton;
    @FXML private Button TransactionsButton;
    @FXML private Button SettingsButton;
    @FXML private Button LogoutButton;


    /**
     * Generic helper method to switch scenes, handling file location checks.
     */
    private void switchScene(ActionEvent event, String fxmlFileName, String title) {
        final String FXML_PATH = "/fxml/" + fxmlFileName;
        URL fxmlUrl = getClass().getResource(FXML_PATH);

        if (fxmlUrl == null) {
            System.err.println("CRITICAL ERROR: FXML file not found! Expected path: " + FXML_PATH);
            return;
        }

        try {
            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root);

            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.setTitle(title);
            window.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("FXML Load Exception: Check " + fxmlFileName + " syntax or controller reference.");
        }
    }


    /**
     * Handles the click event for the "My Accounts" button.
     */
    @FXML
    private void handleAccountsPage(ActionEvent event) {
        // Navigates to the AccountsPage.fxml
        switchScene(event, "AccountsPage.fxml", "Bank App - My Accounts");
    }

    /**
     * Handles the click event for the "View Transactions" button.
     * (Assumes you will create a TransactionsPage.fxml)
     */
    @FXML
    private void handleTransactionsPage(ActionEvent event) {
        // Navigates to a new Transactions page
        switchScene(event, "ViewTransactions.fxml", "Bank App - All Transactions");
    }

    /**
     * Handles the click event for the "Settings" button.
     * (Assumes you will create a SettingsPage.fxml)
     */
    @FXML
    private void handleSettingsPage(ActionEvent event)
    {
        // Navigates to a new Transactions page
        switchScene(event, "SettingsPage.fxml", "Bank App - All Transactions");
    }
    /**
     * Handles the click event for the "Log Out" button, returning to the login page.
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        // Navigates back to the login page
        switchScene(event, "login.fxml", "Bank App Login");
    }
}