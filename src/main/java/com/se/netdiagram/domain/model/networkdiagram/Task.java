package com.se.netdiagram.domain.model.networkdiagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.se.netdiagram.domain.model.networkdiagram.date.Duration;
import com.se.netdiagram.domain.model.utilities.Query;

public class Task {
    private TaskId id;
    private Duration duration;
    private List<Dependency> predecessors = new ArrayList<>();
    private List<Dependency> successors = new ArrayList<>();
    private EarliestLatest earliestLatest = new EarliestLatest();

    protected Task(TaskId taskId, Duration duration) {
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

    public Duration duration() {
        return duration;
    }

    public long durationAsInt() {
        return duration.value();
    }

    public List<Dependency> successors() {
        return Collections.unmodifiableList(successors);
    }

    public List<Dependency> predecessors() {
        return Collections.unmodifiableList(predecessors);
    }

    public boolean dependsOnAnyTaskFrom(List<Task> tasks) {
        return Query.any(
                predecessors,
                dependency -> Query.any(
                        tasks,
                        task -> dependency.task() == task));
    }

    public boolean haveAnyTaskDependingOnMeFrom(List<Task> tasks) {
        return Query.any(
                tasks,
                task -> Query.any(
                        task.predecessors,
                        dependency -> dependency.task() == this));
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
     * @param predDependency
     */
    protected void addPredecessor(Dependency predDependency) {
        if (predDependency.task() == this)
            throw new IllegalArgumentException("A task cannot be its own predecessor!");

        if (predecessors.contains(predDependency)) {
            throw new IllegalArgumentException("A task cannot have the same predecessor twice!");
        }

        if (this.additionOfDependencyCreatesACircularDependency(predDependency)) {
            throw new IllegalArgumentException("Adding a predecessor should not create a circular dependency!");
        }

        predecessors.add(predDependency);
        predDependency.task().successors.add(new Dependency(this, predDependency.type(), predDependency.lag()));

        earliestLatest = new EarliestLatest();
        predDependency.task().earliestLatest = new EarliestLatest();
    }

    private boolean additionOfDependencyCreatesACircularDependency(Dependency predDependency) {
        for (Dependency dependency : successors) {
            Task nextTask = dependency.task();

            if (nextTask == predDependency.task()) {
                return true;
            }

            if (nextTask.additionOfDependencyCreatesACircularDependency(predDependency)) {
                return true;
            }
        }
        return false;
    }

    protected void calculateEarliest() {
        earliestLatest = earliestLatest.calculateEarliest(predecessors, duration);
    }

    protected void calculateLatestAndSlack(long projectEnd) {
        earliestLatest = earliestLatest.calculateLatestAndSlack(successors, duration, projectEnd);
    }

    public EarliestLatest earliestLatest() {
        return earliestLatest;
    }

    public long earliestStart() {
        return earliestLatest.earliestStart().getAsLong();
    }

    public long earliestFinish() {
        return earliestLatest.earliestFinish().getAsLong();
    }

    public long latestStart() {
        return earliestLatest.latestStart().getAsLong();
    }

    public long latestFinish() {
        return earliestLatest.latestFinish().getAsLong();
    }

    public long slack() {
        return earliestLatest.slack().value();
    }

}
