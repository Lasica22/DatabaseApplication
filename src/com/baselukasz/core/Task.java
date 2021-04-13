package com.baselukasz.core;

public class Task {

    private int id;
    private String title;
    private String description;
    private boolean status;

    // Konstruktor z parametrami
    public Task(int id, String title, String description, boolean status){
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    // Getters and Settters
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
