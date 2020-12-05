package com.example.a202sgi_project;

public class Model {
    private String task,details,id,date;

    public Model(String task, String details, String id, String date) {
        this.task = task;
        this.details = details;
        this.id = id;
        this.date = date;
    }

    public Model(){

    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
