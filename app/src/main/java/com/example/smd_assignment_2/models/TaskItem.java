package com.example.smd_assignment_2.models;

import java.io.Serializable;

public class TaskItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String description;
    private boolean done;
    private String priority; // "High", "Medium", "Low"

    public TaskItem(String id, String description, boolean done) {
        this.id = id;
        this.description = description;
        this.done = done;
        this.priority = "Medium";
    }

    public TaskItem(String id, String description, boolean done, String priority) {
        this.id = id;
        this.description = description;
        this.done = done;
        this.priority = priority;
    }

    public String getPriority() {
        return priority == null ? "Medium" : priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
