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
    public void projectProgressPercentage_calculatesCorrectly() {
        ArrayList<TaskItem> tasks = new ArrayList<>();
        tasks.add(new TaskItem("t1", "Task 1", true));
        tasks.add(new TaskItem("t2", "Task 2", false));
        tasks.add(new TaskItem("t3", "Task 3", true));
        tasks.add(new TaskItem("t4", "Task 4", false));

        Project project = new Project("p1", "Tag", "Name", "Desc", new ArrayList<>(), 0, tasks);

        assertEquals(50, project.getProgressPercentage());
        assertEquals(4, project.getTaskCount());
        assertEquals("4 Tasks", project.getTaskCountLabel());
    }

    @Test
    public void taskItem_defaultPriorityAndProperties() {
        TaskItem task = new TaskItem("t1", "Description", false);
        assertEquals("Medium", task.getPriority());
        assertFalse(task.isDone());

        task.setDone(true);
        assertTrue(task.isDone());
    }
}
