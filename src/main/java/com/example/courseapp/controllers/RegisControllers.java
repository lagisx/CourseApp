package com.example.courseapp.controllers;

import com.example.courseapp.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisControllers {
    @FXML private TextField loginField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void initialize() {
        errorLabel.setText("");
    }

    @FXML
    private void register(ActionEvent event) throws IOException {
        String login = loginField.getText();
        String phone = phoneField.getText();
        String pass = passwordField.getText();

        if (login.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }
        if (pass.length() < 5 || pass.length() > 10 ) {
            errorLabel.setText("Пароль должен быть от 5 до 10 символов");
            return;
        }
        if (phone.length() > 12) {
            errorLabel.setText("Некорректный номер");
            return;
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        BDControllers bd = new BDControllers();
        if (bd.registerUser(login, pass, phone)) {
            UserNamaPanelController.UserPanel(stage, login, pass);
        } else {
            errorLabel.setText("Ошибка создания аккаунта");
        }

    }

    @FXML
    private void OnLogin(ActionEvent event) {
        try {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Login.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Авторизация");
        stage.centerOnScreen();
    } catch (
    IOException e) {
        showStatus("Ошибка при открытии авторизации");
        e.printStackTrace();
    }
}
    private void showStatus(String message) {
        errorLabel.setText(message);
    }

}