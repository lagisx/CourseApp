package com.example.courseapp.controllers;

import com.example.courseapp.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class UserNamaPanelController {
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/CourseBD";
    public static final String DB_USER = "postgres";
    public static final String DB_PASSWORD = "lagisx";


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
        }

        private void showPane(VBox paneToShow) {
            paneMyCourses.setVisible(false);
            paneCatalog.setVisible(false);
            paneProfile.setVisible(false);

            paneToShow.setVisible(true);
        }

        private void setNameUser(String user) {
            welcomeLabel.setText("Добро пожаловать, " + user + "!");
        }

    private void setPassUsername(String user, String password) {
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

    @FXML
    private void logout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Авторизация");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
