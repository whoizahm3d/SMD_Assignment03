package com.example.smd_assignment03;

import java.io.Serializable;

public class TaskFilterState implements Serializable {
    private static final long serialVersionUID = 1L;

    public String query = "";
    public String status = "All statuses";
    public String priority = "All priorities";
    public String date = "All dates";
    public String sort = "Due date (earliest first)";

    public void reset() {
        query = "";
        status = "All statuses";
        priority = "All priorities";
        date = "All dates";
        sort = "Due date (earliest first)";
    }
}
