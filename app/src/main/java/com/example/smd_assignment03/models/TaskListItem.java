package com.example.smd_assignment03.models;

import java.io.Serializable;

public class TaskListItem implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_TASK = 1;

    private int type;
    private String headerTitle;
    private int headerCount;
    private TaskItem taskItem;

    public TaskListItem(String headerTitle, int headerCount) {
        this.type = TYPE_HEADER;
        this.headerTitle = headerTitle;
        this.headerCount = headerCount;
        this.taskItem = null;
    }

    public TaskListItem(TaskItem taskItem) {
        this.type = TYPE_TASK;
        this.taskItem = taskItem;
        this.headerTitle = null;
        this.headerCount = 0;
    }

    public int getType() {
        return type;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public int getHeaderCount() {
        return headerCount;
    }

    public TaskItem getTaskItem() {
        return taskItem;
    }
}
