package com.example.courseapp.controllers;

import com.example.courseapp.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button authButton;

    @FXML
    private void initialize() {
        errorLabel.setText("");

        authButton.setOnAction(event -> {
                authenticate(event);
        });
    }


    private void authenticate(ActionEvent event) {
        String login = loginField.getText();
        String pass = passwordField.getText();

        if (login.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }

        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            UserNamaPanelController.UserPanel(stage, login, pass);

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Ошибка при открытии панели пользователя");
        }
    }

    @FXML
    private void OnReg(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Regis.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Регистрация");
            stage.centerOnScreen();
        } catch (
                IOException e) {
            showStatus("Ошибка при открытии регистрации");
            e.printStackTrace();
        }
    }
    private void showStatus(String message) {
        errorLabel.setText(message);
    }

}
