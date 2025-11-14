package com.example.courseapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class CourseCardController {

    @FXML private Label title;
    @FXML private Label desc;
    @FXML private Label levelLabel;
    @FXML private Button openBtn;
    private int courseId;

    public void setData(int courseId, String courseTitle, String courseDescription, String level) {
        this.courseId = courseId;
        this.title.setText(courseTitle);
        this.desc.setText(courseDescription);
        levelLabel.setText("Уровень: " + level);
    }

    /** Получить кнопку, чтобы добавить обработчик */
    public Button getOpenButton() {
        return openBtn;
    }

    public String getTitleText() {
        return title.getText();
    }

    public int getCourseId() {
        return courseId;
    }
}
