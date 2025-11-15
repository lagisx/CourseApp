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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserNamaPanelController {

    @FXML private FlowPane flowCatalog;

    private String user;
    private String password;

    @FXML private Label welcomeLabel;
    @FXML private Button btnMyCourses;
    @FXML private Button btnCatalog;
    @FXML private Button btnProfile;

    @FXML private TextField tfUsername;
    @FXML private PasswordField tfPassword;
    @FXML private TextField tfPhone;
    @FXML private Button btnSaveChanges;
    @FXML private Button btnDeleteAccount;

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
        btnProfile.setOnAction(e -> {
            showPane(paneProfile);
            loadProfile();
        });

        btnSaveChanges.setOnAction(e -> saveProfileChanges());
        btnDeleteAccount.setOnAction(e -> deleteAccount());
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

    private void loadProfile() {
        BDControllers db = new BDControllers();
        try {
            db.connect();
            String sql = "SELECT username, password, phone FROM users WHERE username = ?";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setString(1, user);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    tfUsername.setPromptText(rs.getString("username"));
                    tfPassword.setPromptText(rs.getString("password"));
                    tfPhone.setPromptText(rs.getString("phone"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.disconnect();
        }
    }

    private void saveProfileChanges() {
        String newUsername = tfUsername.getText().trim();
        String newPassword = tfPassword.getText().trim();
        String newPhone = tfPhone.getText().trim();

        // Проверка на длину пароля
        if (!newPassword.isEmpty() && (newPassword.length() < 5 || newPassword.length() > 10)) {
            showAlert("Ошибка", "Пароль должен быть от 5 до 10 символов!");
            return;
        }

        // Проверка на длину телефона
        if (!newPhone.isEmpty() && newPhone.length() > 12) {
            showAlert("Ошибка", "Телефон не может быть длиннее 12 символов!");
            return;
        }

        BDControllers db = new BDControllers();
        try {
            db.connect();

            // Строим SQL динамически, чтобы изменять только заполненные поля
            StringBuilder sql = new StringBuilder("UPDATE users SET ");
            boolean first = true;

            if (!newUsername.isEmpty() && !newUsername.equals(user)) {
                if (!isUniqueUsernameOrPhone(newUsername, null)) {
                    showAlert("Ошибка", "Логин уже используется!");
                    return;
                }
                sql.append("username = ?");
                first = false;
            }

            if (!newPassword.isEmpty()) {
                if (!first) sql.append(", ");
                sql.append("password = ?");
                first = false;
            }

            if (!newPhone.isEmpty()) {
                if (!first) sql.append(", ");
                if (!isUniqueUsernameOrPhone(null, newPhone)) {
                    showAlert("Ошибка", "Телефон уже используется!");
                    return;
                }
                sql.append("phone = ?");
            }

            sql.append(" WHERE username = ?");

            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql.toString())) {
                int index = 1;
                if (!newUsername.isEmpty() && !newUsername.equals(user)) stmt.setString(index++, newUsername);
                if (!newPassword.isEmpty()) stmt.setString(index++, newPassword);
                if (!newPhone.isEmpty()) stmt.setString(index++, newPhone);
                stmt.setString(index, user); // условие WHERE
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    if (!newUsername.isEmpty()) user = newUsername;
                    showAlert("Успех", "Данные обновлены!");
                    welcomeLabel.setText("Добро пожаловать, " + user + "!");
                    loadProfile(); // обновим поля
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось сохранить изменения!");
        } finally {
            db.disconnect();
        }
    }

    private boolean isUniqueUsernameOrPhone(String username, String phone) {
        BDControllers db = new BDControllers();
        try {
            db.connect();
            String sql = "SELECT * FROM users WHERE (username = ? OR phone = ?) AND username <> ?";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setString(1, username != null ? username : "");
                stmt.setString(2, phone != null ? phone : "");
                stmt.setString(3, user);
                ResultSet rs = stmt.executeQuery();
                return !rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.disconnect();
        }
    }

    private void deleteAccount() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтвердите удаление");
        confirm.setHeaderText(null);
        confirm.setContentText("Вы точно хотите удалить аккаунт? Это действие необратимо!");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            BDControllers db = new BDControllers();
            try {
                db.connect();
                String sql = "DELETE FROM users WHERE username = ?";
                try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                    stmt.setString(1, user);
                    stmt.executeUpdate();
                }
                // Переход на Login.fxml
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Login.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) paneProfile.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Авторизация");
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.disconnect();
            }
        }
    }

    private void showPane(VBox paneToShow) {
        paneMyCourses.setVisible(false);
        paneCatalog.setVisible(false);
        paneProfile.setVisible(false);

        paneToShow.setVisible(true);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
