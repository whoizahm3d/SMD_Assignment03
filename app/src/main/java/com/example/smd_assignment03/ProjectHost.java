package com.example.smd_assignment03;

import com.example.smd_assignment03.models.Project;
import com.example.smd_assignment03.models.TaskItem;

public interface ProjectHost {
    Project getProject();
    TaskFilterState getTaskFilterState();
    FileFilterState getFileFilterState();
    void onProjectChanged();
    void selectTab(int index);
    void showTaskDialog(TaskItem taskOrNull);
    void openTasksWithFilter(String status, String dateFilter);
    void refreshVisibleFragments();
}
