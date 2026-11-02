package com.example.smd_assignment03.models;

import com.example.smd_assignment03.AttachmentCatalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String tag;
    private String name;
    private String description;
    private ArrayList<TaskItem> tasks;
    private ArrayList<AttachmentLink> attachments;
    private int commentCount;

    public Project(String tag, String name, String description, ArrayList<TaskItem> tasks, ArrayList<AttachmentLink> attachments, int commentCount) {
        this.id = UUID.randomUUID().toString();
        this.tag = tag;
        this.name = name;
        this.description = description;
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        this.attachments = attachments != null ? attachments : new ArrayList<>();
        this.commentCount = commentCount;
    }

    public Project(String id, String tag, String name, String description, ArrayList<TaskItem> tasks, ArrayList<AttachmentLink> attachments, int commentCount) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.description = description;
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        this.attachments = attachments != null ? attachments : new ArrayList<>();
        this.commentCount = commentCount;
    }

    public int getTotalTasks() {
        return tasks == null ? 0 : tasks.size();
    }

    public int getTodoCount() {
        if (tasks == null)
        {
            return 0;
        }
        int count = 0;
        for (TaskItem t : tasks) {
            if (TaskItem.STATUS_TODO.equals(t.getStatus()))
            {
                count++;
            }
        }
        return count;
    }

    public int getInProgressCount() {
        if (tasks == null)
        {
            return 0;
        }
        int count = 0;
        for (TaskItem t : tasks) {
            if (TaskItem.STATUS_IN_PROGRESS.equals(t.getStatus()))
            {
                count++;
            }
        }
        return count;
    }

    public int getDoneCount() {
        if (tasks == null)
        {
            return 0;
        }
        int count = 0;
        for (TaskItem t : tasks) {
            if (t.isDone())
            {
                count++;
            }
        }
        return count;
    }

    public int getOverdueCount() {
        if (tasks == null)
        {
            return 0;
        }
        int count = 0;
        for (TaskItem t : tasks) {
            if (t.isOverdue())
            {
                count++;
            }
        }
        return count;
    }

    public int getProgressPercent() {
        int total = getTotalTasks();
        if (total == 0)
        {
            return 0;
        }
        int done = getDoneCount();
        return (done * 100) / total;
    }

    public int getAttachmentCount() {
        return attachments == null ? 0 : attachments.size();
    }

    public int getImageCount() {
        if (attachments == null)
        {
            return 0;
        }
        int count = 0;
        for (AttachmentLink link : attachments) {
            CatalogItem item = AttachmentCatalog.findById(link.getCatalogId());
            if (item != null && "IMAGE".equals(item.getType()))
            {
                count++;
            }
        }
        return count;
    }

    public int getTextCount() {
        if (attachments == null)
        {
            return 0;
        }
        int count = 0;
        for (AttachmentLink link : attachments) {
            CatalogItem item = AttachmentCatalog.findById(link.getCatalogId());
            if (item != null && "TEXT".equals(item.getType()))
            {
                count++;
            }
        }
        return count;
    }

    public TaskItem findTaskById(String taskId) {
        if (tasks == null || taskId == null)
        {
            return null;
        }
        for (TaskItem t : tasks) {
            if (taskId.equals(t.getId()))
            {
                return t;
            }
        }
        return null;
    }

    public boolean removeTaskById(String taskId) {
        TaskItem t = findTaskById(taskId);
        if (t != null)
        {
            return tasks.remove(t);
        }
        return false;
    }

    public AttachmentLink findAttachmentById(String attId) {
        if (attachments == null || attId == null)
        {
            return null;
        }
        for (AttachmentLink a : attachments) {
            if (attId.equals(a.getId()))
            {
                return a;
            }
        }
        return null;
    }

    public boolean removeAttachmentById(String attId) {
        AttachmentLink a = findAttachmentById(attId);
        if (a != null)
        {
            return attachments.remove(a);
        }
        return false;
    }

    public boolean isCatalogItemLinked(String catalogId) {
        if (attachments == null || catalogId == null)
        {
            return false;
        }
        for (AttachmentLink a : attachments) {
            if (catalogId.equals(a.getCatalogId()))
            {
                return true;
            }
        }
        return false;
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

    public ArrayList<TaskItem> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<TaskItem> tasks) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
    }

    public ArrayList<AttachmentLink> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<AttachmentLink> attachments) {
        this.attachments = attachments != null ? attachments : new ArrayList<>();
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
