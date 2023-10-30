package com.se.netdiagram.domain.model;

import org.junit.Test;

public class TaskTest {

    @Test(expected = IllegalArgumentException.class)
    public void addPredecessor_Should_throw_IllegalArgumentException_When_Predecessor_is_self() {
        Task task = new Task(new TaskId("A"), 1);
        task.addPredecessor(task);
    }
}
