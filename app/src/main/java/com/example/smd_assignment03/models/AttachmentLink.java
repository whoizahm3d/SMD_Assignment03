package com.example.smd_assignment03.models;

import java.io.Serializable;
import java.util.UUID;

public class AttachmentLink implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String catalogId;
    private int resId;
    private long linkedAt;

    public AttachmentLink(String catalogId, int resId) {
        this.id = UUID.randomUUID().toString();
        this.catalogId = catalogId;
        this.resId = resId;
        this.linkedAt = System.currentTimeMillis();
    }

    public AttachmentLink(String id, String catalogId, int resId, long linkedAt) {
        this.id = id;
        this.catalogId = catalogId;
        this.resId = resId;
        this.linkedAt = linkedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public long getLinkedAt() {
        return linkedAt;
    }

    public void setLinkedAt(long linkedAt) {
        this.linkedAt = linkedAt;
    }
}
