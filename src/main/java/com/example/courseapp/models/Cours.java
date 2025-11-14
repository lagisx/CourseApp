package com.example.courseapp.models;

public class Cours {
    private int id;
    private String title;
    private String description;
    private String level;

    public Cours(int id, String title, String description, String level) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.level=level;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
}
