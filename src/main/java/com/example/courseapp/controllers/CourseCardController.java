package com.example.courseapp.controllers;

import com.example.courseapp.models.Cours;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class CourseCardController {

    @FXML private Label title;
    @FXML private Label desc;
    @FXML private Label levelLabel;
    @FXML private Button openBtn;
    @FXML private Button deleteBtn;

    private int courseId;
    private String courseTitle;
    private String courseDescription;
    private String courseLevel;

    // Контекст карточки: "catalog" или "mycourses"
    private String context;

    public void setData(int id, String title, String desc, String level, String context) {
        this.courseId = id;
        this.courseTitle = title;
        this.courseDescription = desc;
        this.courseLevel = level;
        this.context = context;

        this.title.setText(title);
        this.desc.setText(desc);
        this.levelLabel.setText("Уровень: " + level);

        if ("catalog".equals(context)) {
            openBtn.setText("Добавить");
            deleteBtn.setVisible(false); // скрываем крестик
        } else {
            openBtn.setText("Открыть");
            deleteBtn.setVisible(true);  // показываем крестик
        }
    }

    public Button getActionButton() {
        return openBtn;
    }

    public Button getDeleteBtn() {
        return deleteBtn;
    }

    public String getTitleText() {
        return courseTitle;
    }

    public int getCourseId() {
        return courseId;
    }

    public void openCourseWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/courseapp/OpenCourseWindow.fxml"));
            Scene scene = new Scene(loader.load());

            OpenCourseWindowController controller = loader.getController();
            controller.setData(courseTitle, courseDescription, courseLevel);

            Stage stage = new Stage();
            stage.setTitle(courseTitle);
            stage.setScene(scene);
            stage.show();
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
