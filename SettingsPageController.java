package com.bankingsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SettingsPageController {

    // FXML fields for Change Password
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    // FXML fields for Contact Information
    @FXML private TextField emailField;
    @FXML private TextField phoneField;

    // --- GENERIC SCENE SWITCHING HELPER ---

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

    // --- BUTTON HANDLERS ---

    @FXML
    private void handleChangePassword(ActionEvent event) {
        String current = currentPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        // Implement password change logic here.
        // Example: Validate current password, check newPass == confirmPass, then update credentials.

        System.out.println("Attempting to change password...");
        // In a real application, you would provide feedback to the user (e.g., using a Label).
        if (!newPass.equals(confirmPass)) {
            System.out.println("Error: New passwords do not match.");
        } else {
            System.out.println("Password change logic executed (if successful, clear fields).");
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
        }
    }

    @FXML
    private void handleUpdateContact(ActionEvent event) {
        String email = emailField.getText();
        String phone = phoneField.getText();

        // Implement contact update logic here.
        System.out.println("Attempting to update contact info. Email: " + email + ", Phone: " + phone);
        // In a real application, you would persist this data.
    }

    /**
     * Handles the click event for the "Back to Main Menu" button.
     */
    @FXML
    private void handleBackToMenu(ActionEvent event) {
        // Navigate back to the MainMenu.fxml
        switchScene(event, "MainMenu.fxml", "Bank App - Main Menu");
    }
}