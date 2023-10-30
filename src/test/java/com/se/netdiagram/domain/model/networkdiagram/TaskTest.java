package com.se.netdiagram.domain.model.networkdiagram;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TaskTest {

    @Test(expected = IllegalArgumentException.class)
    public void addPredecessor_Should_throw_IllegalArgumentException_When_Predecessor_is_self() {
        Task task = new Task(new TaskId("A"), 1);
        task.addPredecessor(task);
    }

    @Test
    public void aNewTaskShouldHaveAnEmptyES() {
        Task task = new Task(new TaskId("A"), 1);
        assertFalse(task.earliestStart().isPresent());
    }

    @Test
    public void whenCalculateEarliestValuesESShouldBeNonEmpty() {
        Task task = new Task(new TaskId("A"), 1);
        task.calculateEarliestValues();
        assertTrue(task.earliestStart().isPresent());
    }

    @Test
    public void whenAddingPredecessorsESShouldGetEmpty() {
        Task task = new Task(new TaskId("A"), 1);
        task.calculateEarliestValues();
        task.addPredecessor(new Task(new TaskId("B"), 1));
        assertFalse(task.earliestStart().isPresent());
    }
}
