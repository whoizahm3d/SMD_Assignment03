package com.example.smd_assignment03.models;

public class CatalogItem {

    private String id;
    private String displayName;
    private String type; // "IMAGE" or "TEXT"
    private int resId;
    private String typeLabel;

    public CatalogItem(String id, String displayName, String type, int resId, String typeLabel) {
        this.id = id;
        this.displayName = displayName;
        this.type = type;
        this.resId = resId;
        this.typeLabel = typeLabel;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getType() {
        return type;
    }

    public int getResId() {
        return resId;
    }

    public String getTypeLabel() {
        return typeLabel;
    }
}
