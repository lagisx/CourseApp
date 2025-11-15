package com.example.courseapp.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class OpenCourseWindowController {

    @FXML private Label titleLabel;
    @FXML private Label descLabel;
    @FXML private Label levelLabel;

    public void setData(String title, String desc, String level) {
        titleLabel.setText(title);
        descLabel.setText(desc);
        levelLabel.setText("Уровень: " + level);
    }


}
