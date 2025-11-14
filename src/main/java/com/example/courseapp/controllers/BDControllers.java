package com.example.courseapp.controllers;

import com.example.courseapp.models.Cours;
import javafx.fxml.FXML;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BDControllers {
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/CourseBD";
    public static final String DB_USER = "postgres";
    public static final String DB_PASSWORD = "lagisx";
    private Connection connection;

    public void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
    }
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try {
            connect();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            disconnect();
        }
    }
    public boolean registerUser(String username, String password, String phone) {
        String checkSql = "SELECT * FROM users WHERE username = ? OR phone = ?";
        String insertSql = "INSERT INTO users (username, password, phone) VALUES (?, ?, ?)";

        try {
            connect();

            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);
                checkStmt.setString(2, phone);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    return false;
                }
            }

            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);
                insertStmt.setString(3, phone);
                insertStmt.executeUpdate();
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            disconnect();
        }
    }



    public List<Cours> getAllCourses() {
        List<Cours> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";

        try {
            connect();
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    courses.add(new Cours(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("level")
                    ));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }

        return courses;
    }
}
