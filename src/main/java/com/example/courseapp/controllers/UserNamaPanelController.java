package com.example.courseapp.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class UserNamaPanelController {
    private String user;
    private String password;


        @FXML private Label welcomeLabel;
        @FXML private Button btnMyCourses;
        @FXML private Button btnCatalog;
        @FXML private Button btnProfile;
        @FXML private Button btnSettings;

        @FXML private VBox paneMyCourses;
        @FXML private VBox paneCatalog;
        @FXML private VBox paneProfile;
        @FXML private VBox paneSettings;

        @FXML
        private void initialize() {
            btnMyCourses.setOnAction(e -> showPane(paneMyCourses));
            btnCatalog.setOnAction(e -> showPane(paneCatalog));
            btnProfile.setOnAction(e -> showPane(paneProfile));
            btnSettings.setOnAction(e -> showPane(paneSettings));
        }

        private void showPane(VBox paneToShow) {
            paneMyCourses.setVisible(false);
            paneCatalog.setVisible(false);
            paneProfile.setVisible(false);

            paneToShow.setVisible(true);
        }

        public void setNameUser(String user) {
            welcomeLabel.setText("Добро пожаловать, " + user + "!");
        }

    public void setPassUsername(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public static void UserPanel(Stage stage, String user, String password) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserNamaPanelController.class.getResource("/com/example/courseapp/UserPanel.fxml"));
        Scene userScene = new Scene(fxmlLoader.load());

        UserNamaPanelController controller = fxmlLoader.getController();
        controller.setNameUser(user);
        controller.setPassUsername(user, password);

        stage.setTitle("Панель пользователя");
        stage.setScene(userScene);
        stage.show();
        stage.centerOnScreen();
    }
}
