package com.example.courseapp.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class AdminPanelController {
    public String user;
    public String password;

    public void setPassUsername(String user, String password) throws SQLException {
        this.user = user;
        this.password = password;

    }
    public void setNameUser(String user) {
        System.out.println(user);
    }


    public static void AdminPanel(Stage stage, String user, String password) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(AdminPanelController.class.getResource("/com/example/courseapp/AdminPanelFXML.fxml"));
        Scene userScene = new Scene(fxmlLoader.load());

        AdminPanelController controller = fxmlLoader.getController();
        controller.setNameUser(user);
        controller.setPassUsername(user, password);

        stage.setTitle("Панель пользователя");
        stage.setScene(userScene);
        stage.show();
        stage.centerOnScreen();

    }
}
