package com.se.netdiagram.domain.model.networkdiagram;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.se.netdiagram.domain.model.networkdiagram.date.Duration;

public class TaskTest {

    @Test(expected = IllegalArgumentException.class)
    public void addPredecessor_Should_throw_IllegalArgumentException_When_Predecessor_is_self() {
        Task task = new Task(new TaskId("A"), new Duration(1));
        task.addPredecessor(new Dependency(task, DependencyType.FS));
    }

    @Test
    public void aNewTask_Should_HaveAnEmptyES() {
        Task task = new Task(new TaskId("A"), new Duration(1));
        assertFalse(task.earliestLatest().earliestStart().isDateIsPresent());
    }

    @Test
    public void when_CalculateEarliest_ESShouldBeNonEmpty() {
        Task task = new Task(new TaskId("A"), new Duration(1));
        task.calculateEarliest();
        assertTrue(task.earliestLatest().earliestStart().isDateIsPresent());
    }

    @Test
    public void when_AddingPredecessors_ESShouldGetEmpty() {
        Task task = new Task(new TaskId("A"), new Duration(1));
        task.calculateEarliest();
        task.addPredecessor(new Dependency(new Task(new TaskId("B"), new Duration(1)), DependencyType.FS));
        assertFalse(task.earliestLatest().earliestStart().isDateIsPresent());
    }
}
