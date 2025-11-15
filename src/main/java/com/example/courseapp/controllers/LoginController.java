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
import java.sql.Connection;
import java.sql.DriverManager;

public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void initialize() {
        errorLabel.setText("");
    }

    @FXML
    private void authenticate(ActionEvent event) throws IOException {
        String login = loginField.getText();
        String pass = passwordField.getText();

        if (login.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        BDControllers bd = new BDControllers();
        if (!login.equals(BDControllers.DB_USER)) {
            if (bd.authenticateUser(login, pass)) {
                UserNamaPanelController.UserPanel(stage, login, pass);
            } else {
                errorLabel.setText("Неверный логин или пароль");
            }
        } else {
            try(Connection con = DriverManager.getConnection(BDControllers.DB_URL, login, pass)) {
                Stage adminstage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                AdminPanelController.AdminPanel(adminstage, login, pass);
            } catch(Exception e) {
                errorLabel.setText("Неверный логин или пароль");
                e.printStackTrace();
            }

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
