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
            String sql = "SELECT id, title, description, level FROM courses";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    courses.add(new Cours(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("level")
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return courses;
        }

        public List<Cours> getMyCourses(String username) {
            List<Cours> courses = new ArrayList<>();
            String sql = "SELECT c.id, c.title, c.description, c.level " +
                    "FROM courses c " +
                    "JOIN user_courses uc ON c.id = uc.course_id " +
                    "JOIN users u ON uc.user_id = u.id " +
                    "WHERE u.username = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    courses.add(new Cours(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("level")
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return courses;
        }

        public void addCourseToUser(int courseId, String username) {
            String sql = "INSERT INTO user_courses(user_id, course_id) " +
                    "SELECT u.id, ? FROM users u WHERE u.username = ? " +
                    "ON CONFLICT DO NOTHING"; // чтобы не дублировать
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, courseId);
                stmt.setString(2, username);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void removeCourseFromUser(int courseId, String username) {
            String sql = "DELETE FROM user_courses " +
                    "WHERE course_id = ? AND user_id = (SELECT id FROM users WHERE username = ?)";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, courseId);
                stmt.setString(2, username);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

