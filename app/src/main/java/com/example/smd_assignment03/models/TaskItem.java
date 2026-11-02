package com.example.smd_assignment03.models;

import com.example.smd_assignment03.DateUtils;

import java.io.Serializable;
import java.util.UUID;

public class TaskItem implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String STATUS_TODO = "To do";
    public static final String STATUS_IN_PROGRESS = "In progress";
    public static final String STATUS_DONE = "Done";

    public static final String PRIORITY_HIGH = "High";
    public static final String PRIORITY_NORMAL = "Normal";
    public static final String PRIORITY_LOW = "Low";

    private String id;
    private String description;
    private String status;
    private String priority;
    private long dueDateMillis;
    private long createdAt;

    public TaskItem(String description, String status, String priority, long dueDateMillis) {
        this.id = UUID.randomUUID().toString();
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDateMillis = dueDateMillis;
        this.createdAt = System.currentTimeMillis();
    }

    public TaskItem(String id, String description, String status, String priority, long dueDateMillis, long createdAt) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDateMillis = dueDateMillis;
        this.createdAt = createdAt;
    }

    public boolean isDone() {
        return STATUS_DONE.equals(status);
    }

    public boolean hasDueDate() {
        return dueDateMillis > 0;
    }

    public boolean isDueToday() {
        if (!hasDueDate())
        {
            return false;
        }
        return DateUtils.isToday(dueDateMillis);
    }

    public boolean isOverdue() {
        if (!hasDueDate())
        {
            return false;
        }
        if (isDone())
        {
            return false;
        }
        return DateUtils.isBeforeToday(dueDateMillis);
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public long getDueDateMillis() {
        return dueDateMillis;
    }

    public void setDueDateMillis(long dueDateMillis) {
        this.dueDateMillis = dueDateMillis;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
