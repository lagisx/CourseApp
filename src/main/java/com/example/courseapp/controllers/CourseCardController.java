package com.example.courseapp.controllers;

import com.example.courseapp.models.Cours;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class CourseCardController {

    @FXML private Label title;
    @FXML private Label desc;
    @FXML private Label levelLabel;
    @FXML private Button openBtn;

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
        } else {
            openBtn.setText("Открыть");
        }
    }

    public Button getActionButton() {
        return openBtn;
    }

    public String getTitleText() {
        return courseTitle;
    }

    public int getCourseId() {
        return courseId;
    }
}
