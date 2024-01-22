package com.se.netdiagram.domain.model.networkdiagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable class that represents a path of tasks.
 */
public class Path {

    private List<Task> tasks;

    public Path() {
        this.tasks = Collections.unmodifiableList(new ArrayList<>());
    }

    public Path(List<Task> tasks) {
        this.tasks = Collections.unmodifiableList(tasks);
    }

    public List<Task> tasks() {
        return Collections.unmodifiableList(tasks);
    }

    public Path addTask(Task task) {
        List<Task> newTasks = new ArrayList<>(this.tasks);
        newTasks.add(task);
        return new Path(newTasks);
    }

    public Path removeLastTask() {
        List<Task> newTasks = new ArrayList<>(this.tasks);
        newTasks.remove(this.tasks.size() - 1);
        return new Path(newTasks);
    }

    public int size() {
        return tasks.size();
    }

    public Task taskAt(int i) {
        return tasks.get(i);
    }

    public boolean containsTask(Task predTask) {
        return tasks.contains(predTask);
    }

    public Task lastTask() {
        return this.tasks.get(this.tasks.size() - 1);
    }

    public String toString() {
        return tasks.toString();
    }

}
