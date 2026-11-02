package com.example.smd_assignment03;

import com.example.smd_assignment03.models.Project;
import com.example.smd_assignment03.models.TaskItem;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void projectProgressPercent_calculatesCorrectly() {
        ArrayList<TaskItem> tasks = new ArrayList<>();
        tasks.add(new TaskItem("Task 1", TaskItem.STATUS_DONE, TaskItem.PRIORITY_NORMAL, -1));
        tasks.add(new TaskItem("Task 2", TaskItem.STATUS_TODO, TaskItem.PRIORITY_NORMAL, -1));
        tasks.add(new TaskItem("Task 3", TaskItem.STATUS_DONE, TaskItem.PRIORITY_NORMAL, -1));
        tasks.add(new TaskItem("Task 4", TaskItem.STATUS_TODO, TaskItem.PRIORITY_NORMAL, -1));

        Project project = new Project("Tag", "Name", "Desc", tasks, new ArrayList<>(), 0);

        assertEquals(50, project.getProgressPercent());
        assertEquals(4, project.getTotalTasks());
        assertEquals(2, project.getDoneCount());
    }

    @Test
    public void taskItem_propertiesAndStatus() {
        TaskItem task = new TaskItem("Description", TaskItem.STATUS_TODO, TaskItem.PRIORITY_NORMAL, -1);
        assertEquals(TaskItem.PRIORITY_NORMAL, task.getPriority());
        assertFalse(task.isDone());

        task.setStatus(TaskItem.STATUS_DONE);
        assertTrue(task.isDone());
    }
}
