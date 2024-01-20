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
        this.tasks = new ArrayList<>();
    }

    public Path(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    public List<Task> tasks() {
        return Collections.unmodifiableList(tasks);
    }

    public Path addTask(Task task) {
        Path newPath = new Path(this.tasks);
        newPath.tasks.add(task);
        return newPath;
    }

    public Path removeLastTask() {
        Path newPath = new Path(this.tasks);
        newPath.tasks.remove(this.tasks.size() - 1);
        return newPath;
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

}
