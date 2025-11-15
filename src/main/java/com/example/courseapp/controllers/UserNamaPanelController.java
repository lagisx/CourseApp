package com.example.courseapp.controllers;

import com.example.courseapp.HelloApplication;
import com.example.courseapp.models.Cours;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class UserNamaPanelController {
    @FXML private FlowPane flowCatalog;


    private String user;
    private String password;


        @FXML private Label welcomeLabel;
        @FXML private Button btnMyCourses;
        @FXML private Button btnCatalog;
        @FXML private Button btnProfile;

        @FXML private VBox paneMyCourses;
        @FXML private VBox paneCatalog;
        @FXML private VBox paneProfile;

        @FXML
        private void initialize() {

            btnMyCourses.setOnAction(e -> {
                showPane(paneMyCourses);
                loadMyCourses();
            });
            btnCatalog.setOnAction(e -> {
                showPane(paneCatalog);
                loadCatalog();
            });
            btnProfile.setOnAction(e -> showPane(paneProfile));
        }

    public void loadCatalog() {
        BDControllers db = new BDControllers();
        flowCatalog.getChildren().clear();

        for (Cours course : db.getAllCourses()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/courseapp/CourseCard.fxml"));
                StackPane card = loader.load();

                CourseCardController controller = loader.getController();
                controller.setData(course.getId(), course.getTitle(), course.getDescription(), course.getLevel(), "catalog");

                controller.getActionButton().setText("Добавить");
                controller.getActionButton().setOnAction(e -> {
                    db.addCourseToUser(course.getId(), user);
                    loadMyCourses();
                });

                flowCatalog.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void loadMyCourses() {
        BDControllers db = new BDControllers();
        paneMyCourses.getChildren().clear();

        for (Cours course : db.getMyCourses(user)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/courseapp/CourseCard.fxml"));
                StackPane card = loader.load();

                CourseCardController controller = loader.getController();
                controller.setData(course.getId(), course.getTitle(), course.getDescription(), course.getLevel(), "mycourses");

                controller.getActionButton().setText("Открыть");
                controller.getActionButton().setOnAction(e -> controller.openCourseWindow());

                controller.getDeleteBtn().setOnAction(e -> {
                    db.removeCourseFromUser(course.getId(), user);
                    loadMyCourses();
                });


                paneMyCourses.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

        loadMyCourses();
        loadCatalog();
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
    @FXML private void logout(ActionEvent event) {
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
