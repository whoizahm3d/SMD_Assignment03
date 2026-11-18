package com.example.smd_assignment03;

import com.example.smd_assignment03.models.TaskItem;
import com.example.smd_assignment03.models.TaskListItem;

import java.util.ArrayList;
import java.util.List;

public class TaskGroupingHelper {

    public static final String GROUP_OVERDUE = "Overdue";
    public static final String GROUP_IN_PROGRESS = "In progress";
    public static final String GROUP_TODO = "To do";
    public static final String GROUP_DONE = "Done";

    public static List<TaskListItem> groupTasks(List<TaskItem> tasks) {
        List<TaskListItem> result = new ArrayList<>();
        if (tasks == null || tasks.isEmpty())
        {
            return result;
        }

        List<TaskItem> overdueList = new ArrayList<>();
        List<TaskItem> inProgressList = new ArrayList<>();
        List<TaskItem> todoList = new ArrayList<>();
        List<TaskItem> doneList = new ArrayList<>();

        for (TaskItem t : tasks)
        {
            if (t.isOverdue())
            {
                overdueList.add(t);
            }
            else if (t.isDone() || TaskItem.STATUS_DONE.equals(t.getStatus()))
            {
                doneList.add(t);
            }
            else if (TaskItem.STATUS_IN_PROGRESS.equals(t.getStatus()))
            {
                inProgressList.add(t);
            }
            else
            {
                todoList.add(t);
            }
        }

        // Fixed order: Overdue, In progress, To do, Done
        addGroup(result, GROUP_OVERDUE, overdueList);
        addGroup(result, GROUP_IN_PROGRESS, inProgressList);
        addGroup(result, GROUP_TODO, todoList);
        addGroup(result, GROUP_DONE, doneList);

        return result;
    }

    public static String getTaskGroup(TaskItem task) {
        if (task == null)
        {
            return GROUP_TODO;
        }
        if (task.isOverdue())
        {
            return GROUP_OVERDUE;
        }
        else if (task.isDone() || TaskItem.STATUS_DONE.equals(task.getStatus()))
        {
            return GROUP_DONE;
        }
        else if (TaskItem.STATUS_IN_PROGRESS.equals(task.getStatus()))
        {
            return GROUP_IN_PROGRESS;
        }
        else
        {
            return GROUP_TODO;
        }
    }

    private static void addGroup(List<TaskListItem> result, String groupName, List<TaskItem> groupTasks) {
        if (groupTasks != null && !groupTasks.isEmpty())
        {
            result.add(new TaskListItem(groupName, groupTasks.size()));
            for (TaskItem t : groupTasks)
            {
                result.add(new TaskListItem(t));
            }
        }
    }
}
