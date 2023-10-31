package com.se.netdiagram.domain.model.networkdiagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalLong;

public class Task {
    private TaskId id;
    private int duration;
    private List<Dependency> predecessors = new ArrayList<>();
    private List<Dependency> successors = new ArrayList<>();
    private OptionalLong earliestStart;
    private OptionalLong earliestFinish;
    private OptionalLong latestStart;
    private OptionalLong latestFinish;
    private OptionalLong slack;

    protected Task(TaskId taskId, int duration) {
        this.id = taskId;
        this.duration = duration;
        setEarliestAndLatestValuesToEmpty();
    }

    private void setEarliestAndLatestValuesToEmpty() {
        earliestStart = OptionalLong.empty();
        earliestFinish = OptionalLong.empty();
        latestStart = OptionalLong.empty();
        latestFinish = OptionalLong.empty();
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

    public List<Dependency> successors() {
        return Collections.unmodifiableList(successors);
    }

    public List<Dependency> predecessors() {
        return Collections.unmodifiableList(predecessors);
    }

    public boolean dependsOnAnyTaskFrom(List<Task> tasks) {
        for (Task task : tasks) {
            if (this.dependsOn(task)) {
                return true;
            }
        }
        return false;
    }

    private boolean dependsOn(Task task) {
        for (Dependency dependency : predecessors) {
            if (dependency.task() == task) {
                return true;
            }
        }
        return false;
    }

    public boolean haveAnyTaskDependingOnMeFrom(List<Task> tasks) {
        for (Task task : tasks) {
            if (task.dependsOn(this)) {
                return true;
            }
        }
        return false;
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

        if (this.additionOfDependencyCreatesACircularDepenendency(predDependency)) {
            throw new IllegalArgumentException("Adding a predecessor should not create a circular dependency!");
        }

        predecessors.add(predDependency);
        predDependency.task().successors.add(new Dependency(this, predDependency.type(), predDependency.lag()));

        setEarliestAndLatestValuesToEmpty();
    }

    private boolean additionOfDependencyCreatesACircularDepenendency(Dependency predDependency) {
        for (Dependency dependency : successors) {
            Task nextTask = dependency.task();

            if (nextTask == predDependency.task()) {
                return true;
            }

            if (nextTask.additionOfDependencyCreatesACircularDepenendency(predDependency)) {
                return true;
            }
        }
        return false;
    }

    protected void calculateEarliestValues() {
        this.earliestStart = OptionalLong.of(0);
        for (Dependency predDependency : this.predecessors()) {
            Task predTask = predDependency.task();

            long es = this.earliestStart.getAsLong();
            long pef = predTask.earliestFinish.getAsLong();
            long pes = predTask.earliestStart.getAsLong();

            switch (predDependency.type()) {
            case FS:
                es = Math.max(es, pef + predDependency.lag());
                break;
            case SS:
                es = Math.max(es, pes + predDependency.lag());
                break;
            case FF:
                es = Math.max(es, pef - this.duration + predDependency.lag());
                break;
            case SF:
                es = Math.max(es, pes - this.duration + predDependency.lag());
            }
            this.earliestStart = OptionalLong.of(es);
        }
        this.earliestFinish = OptionalLong.of(this.earliestStart.getAsLong() + this.duration());

        assert this.earliestStart.getAsLong() >= 0;
    }

    protected void calculateLatestValuesAndSlack(long projectEnd) {
        this.latestFinish = OptionalLong.of(projectEnd);
        for (Dependency succDependency : this.successors()) {
            Task succTask = succDependency.task();

            long lf = this.latestFinish.getAsLong();
            long sls = succTask.latestStart.getAsLong();
            long slf = succTask.latestFinish.getAsLong();

            switch (succDependency.type()) {
            case FS:
                lf = Math.min(lf, sls - succDependency.lag());
                break;
            case SS:
                lf = Math.min(lf, sls + this.duration - succDependency.lag());
                break;
            case FF:
                lf = Math.min(lf, slf - succDependency.lag());
                break;
            case SF:
                lf = Math.min(lf, slf + this.duration - succDependency.lag());
            }
            this.latestFinish = OptionalLong.of(lf);
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
