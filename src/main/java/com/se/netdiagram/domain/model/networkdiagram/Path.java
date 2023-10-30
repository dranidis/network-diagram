package com.se.netdiagram.domain.model.networkdiagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public void addTask(Task task) {
        tasks.add(task);
    }

    public int size() {
        return tasks.size();
    }

    public Task taskAt(int i) {
        return tasks.get(i);
    }

    public void removeTask(int i) {
        tasks.remove(i);
    }

    public boolean containsTask(Task predTask) {
        return tasks.contains(predTask);
    }

    public void removeLastTask() {
        this.tasks.remove(this.tasks.size() - 1);
    }

    public Task lastTask() {
        return this.tasks.get(this.tasks.size() - 1);
    }

}
