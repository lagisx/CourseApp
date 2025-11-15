package com.example.courseapp.controllers;

import com.example.courseapp.HelloApplication;
import com.example.courseapp.models.Cours;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminPanelController {

    @FXML private VBox paneCourses;
    @FXML private VBox paneUsers;

    @FXML private Button btnManageCourses;
    @FXML private Button btnManageUsers;
    @FXML private Button btnAddCourse;

    @FXML private FlowPane flowCourses;
    @FXML private FlowPane flowUsers;

    private String user;
    private String password;

    @FXML
    private void initialize() {
        btnManageCourses.setOnAction(e -> showPane(paneCourses));
        btnManageUsers.setOnAction(e -> showPane(paneUsers));

        btnAddCourse.setOnAction(e -> showCourseDialog(null));
        loadCourses();
        loadUsers();
    }

    private void showPane(VBox paneToShow) {
        paneCourses.setVisible(false);
        paneUsers.setVisible(false);
        paneToShow.setVisible(true);
    }

    private void loadCourses() {
        flowCourses.getChildren().clear();
        BDControllers db = new BDControllers();

        for (Cours course : db.getAllCourses()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/courseapp/CourseCard.fxml"));
                Node card = loader.load();

                CourseCardController controller = loader.getController();
                controller.setData(course.getId(), course.getTitle(), course.getDescription(), course.getLevel(), "admin");

                controller.getActionButton().setText("Редактировать");
                controller.getActionButton().setOnAction(e -> showCourseDialog(course));

                controller.getDeleteBtn().setOnAction(e -> {
                    deleteCourse(course.getId());
                    loadCourses();
                });

                flowCourses.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadUsers() {
        flowUsers.getChildren().clear();
        BDControllers db = new BDControllers();

        try {
            db.connect();
            String sql = "SELECT username, phone FROM users";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String username = rs.getString("username");
                    String phone = rs.getString("phone");

                    VBox userCard = new VBox(10);
                    userCard.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10,0,0,0);");
                    Label lblName = new Label("Логин: " + username);
                    Label lblPhone = new Label("Телефон: " + phone);

                    Button btnEdit = new Button("Редактировать");
                    btnEdit.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-background-radius: 10;");
                    btnEdit.setOnAction(e -> showUserDialog(username));

                    Button btnDelete = new Button("Удалить");
                    btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 10;");
                    btnDelete.setOnAction(e -> {
                        deleteUser(username);
                        loadUsers();
                    });

                    userCard.getChildren().addAll(lblName, lblPhone, btnEdit, btnDelete);
                    flowUsers.getChildren().add(userCard);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            db.disconnect();
        }
    }

    private void showCourseDialog(Cours course) {
        Dialog<Cours> dialog = new Dialog<>();
        dialog.setTitle(course == null ? "Добавить курс" : "Редактировать курс");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField tfTitle = new TextField(course != null ? course.getTitle() : "");
        TextArea taDesc = new TextArea(course != null ? course.getDescription() : "");
        TextField tfLevel = new TextField(course != null ? course.getLevel() : "");

        VBox content = new VBox(10, new Label("Название:"), tfTitle,
                new Label("Описание:"), taDesc,
                new Label("Уровень:"), tfLevel);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return new Cours(course != null ? course.getId() : 0,
                        tfTitle.getText(), taDesc.getText(), tfLevel.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (course == null) addCourse(result);
            else updateCourse(result);
            loadCourses();
        });
    }

    private void addCourse(Cours course) {
        BDControllers db = new BDControllers();
        try {
            db.connect();
            String sql = "INSERT INTO courses (title, description, level) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setString(1, course.getTitle());
                stmt.setString(2, course.getDescription());
                stmt.setString(3, course.getLevel());
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            db.disconnect();
        }
    }

    private void updateCourse(Cours course) {
        BDControllers db = new BDControllers();
        try {
            db.connect();
            String sql = "UPDATE courses SET title = ?, description = ?, level = ? WHERE id = ?";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setString(1, course.getTitle());
                stmt.setString(2, course.getDescription());
                stmt.setString(3, course.getLevel());
                stmt.setInt(4, course.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            db.disconnect();
        }
    }

    private void deleteCourse(int courseId) {
        BDControllers db = new BDControllers();
        try {
            db.connect();
            String sql = "DELETE FROM courses WHERE id = ?";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setInt(1, courseId);
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            db.disconnect();
        }
    }

    private void showUserDialog(String username) {
        BDControllers db = new BDControllers();
        try {
            db.connect();
            String sql = "SELECT username, password, phone FROM users WHERE username = ?";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    TextInputDialog dialog = new TextInputDialog(rs.getString("phone"));
                    dialog.setTitle("Редактировать пользователя");
                    dialog.setHeaderText("Измените телефон пользователя " + username);
                    dialog.showAndWait().ifPresent(newPhone -> {
                        if (newPhone.length() > 12) {
                            showAlert("Ошибка", "Телефон не должен быть больше 12 символов");
                        } else {
                            updateUser(username, newPhone);
                            loadUsers();
                        }
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            db.disconnect();
        }
    }

    private void updateUser(String username, String phone) {
        BDControllers db = new BDControllers();
        try {
            db.connect();
            String sql = "UPDATE users SET phone = ? WHERE username = ?";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setString(1, phone);
                stmt.setString(2, username);
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            db.disconnect();
        }
    }

    private void deleteUser(String username) {
        BDControllers db = new BDControllers();
        try {
            db.connect();
            String sql = "DELETE FROM users WHERE username = ?";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            db.disconnect();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setPassUsername(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public void setNameUser(String user) {
        System.out.println(user);
    }

    public static void AdminPanel(Stage stage, String user, String password) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AdminPanelController.class.getResource("/com/example/courseapp/AdminPanelFXML.fxml"));
        Scene userScene = new Scene(fxmlLoader.load());

        AdminPanelController controller = fxmlLoader.getController();
        controller.setNameUser(user);
        controller.setPassUsername(user, password);

        stage.setTitle("Админ-панель");
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
