package com.example.smd_assignment_2.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String tag;
    private String name;
    private String description;
    private ArrayList<Integer> attachmentResIds;
    private int commentCount;
    private ArrayList<TaskItem> tasks;
    private String dueDate; // e.g. "25 May"

    public Project(String id, String tag, String name, String description,
                    ArrayList<Integer> attachmentResIds, int commentCount,
                    ArrayList<TaskItem> tasks) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.description = description;
        this.attachmentResIds = attachmentResIds;
        this.commentCount = commentCount;
        this.tasks = tasks;
        this.dueDate = "30 May";
    }

    public Project(String id, String tag, String name, String description,
                    ArrayList<Integer> attachmentResIds, int commentCount,
                    ArrayList<TaskItem> tasks, String dueDate) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.description = description;
        this.attachmentResIds = attachmentResIds;
        this.commentCount = commentCount;
        this.tasks = tasks;
        this.dueDate = dueDate;
    }

    public String getDueDate() {
        return dueDate == null ? "30 May" : dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public int getProgressPercentage() {
        if (tasks == null || tasks.isEmpty()) return 0;
        int doneCount = 0;
        for (TaskItem t : tasks) {
            if (t.isDone()) doneCount++;
        }
        return (doneCount * 100) / tasks.size();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Integer> getAttachmentResIds() {
        return attachmentResIds;
    }

    public void setAttachmentResIds(ArrayList<Integer> attachmentResIds) {
        this.attachmentResIds = attachmentResIds;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public ArrayList<TaskItem> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<TaskItem> tasks) {
        this.tasks = tasks;
    }

    public int getTaskCount() {
        return tasks == null ? 0 : tasks.size();
    }

    public String getTaskCountLabel() {
        return getTaskCount() + " Tasks";
    }
}
