package com.example.smd_assignment03;

import java.io.Serializable;

public class FileFilterState implements Serializable {
    private static final long serialVersionUID = 1L;

    public String query = "";
    public String type = "All";

    public void reset() {
        query = "";
        type = "All";
    }
}
