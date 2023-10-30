package com.se.netdiagram.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalLong;

import com.se.netdiagram.domain.model.utilities.Util;

public class Task {
    private TaskId id;
    private int duration;
    private List<Task> predecessors = new ArrayList<>();
    private List<Task> successors = new ArrayList<>();
    private OptionalLong earliestStart;
    private OptionalLong earliestFinish;
    private OptionalLong latestStart;
    private OptionalLong latestFinish;
    private OptionalLong slack;

    public Task(TaskId taskId, int duration) {
        this.id = taskId;
        this.duration = duration;
    }

    public String toString() {
        return id.toString();
    }

    public TaskId id() {
        return id;
    }

    public String idAsString() {
        return id.toString();
    }

    public int duration() {
        return duration;
    }

    public List<Task> successors() {
        return Collections.unmodifiableList(successors);
    }

    public List<Task> predecessors() {
        return Collections.unmodifiableList(predecessors);
    }

    /**
     * Adds a predecessor to this task. Also adds this task as a successor to the
     * predecessor.
     * <p>
     * Domain constraints:
     * <ul>
     * <li>A task cannot be its own predecessor.
     * <li>A task "A" with a "B" as predecessor means that "B" must have "A" as a
     * successor.
     * <li>Adding a predecessor should not create a circular dependency.
     * </ul>
     * 
     * @param predTask
     */
    public void addPredecessor(Task predTask) {
        if (predTask == this)
            throw new IllegalArgumentException("A task cannot be its own predecessor!");

        if (predecessors.contains(predTask)) {
            throw new IllegalArgumentException("A task cannot have the same predecessor twice!");
        }

        if (this.additionOfTaskCreatesACircularDepenendency(predTask)) {
            throw new IllegalArgumentException("Adding a predecessor should not create a circular dependency!");
        }

        predecessors.add(predTask);
        predTask.successors.add(this);
    }

    private boolean additionOfTaskCreatesACircularDepenendency(Task predTask) {
        for (Task nextTask : successors) {
            if (nextTask == predTask) {
                return true;
            }
            if (nextTask.additionOfTaskCreatesACircularDepenendency(predTask)) {
                return true;
            }
        }
        return false;
    }

    protected void calculateEarliestValues() {
        this.earliestStart = OptionalLong.of(0);
        for (Task predTask : this.predecessors()) {
            this.earliestStart = Util.max(this.earliestStart, predTask.earliestFinish);
        }
        this.earliestFinish = OptionalLong.of(this.earliestStart.getAsLong() + this.duration());
    }

    protected void calculateLatestValuesAndSlack(long projectEnd) {
        this.latestFinish = OptionalLong.of(projectEnd);
        for (Task succTask : this.successors()) {
            this.latestFinish = Util.min(this.latestFinish, succTask.latestStart);
        }
        this.latestStart = OptionalLong.of(this.latestFinish.getAsLong() - this.duration());
        this.slack = OptionalLong.of(this.latestFinish.getAsLong() - this.earliestFinish.getAsLong());
    }

    public OptionalLong slack() {
        return slack;
    }

    public OptionalLong earliestFinish() {
        return earliestFinish;
    }

    public OptionalLong earliestStart() {
        return earliestStart;
    }

    public OptionalLong latestStart() {
        return latestStart;
    }

    public OptionalLong latestFinish() {
        return latestFinish;
    }
}
