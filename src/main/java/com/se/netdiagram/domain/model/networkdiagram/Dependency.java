package com.se.netdiagram.domain.model.networkdiagram;

public class Dependency {
    private Task task;
    private DependencyType type;
    private int lag;

    public Dependency(Task task, DependencyType type, int lag) {
        this.task = task;
        this.type = type;
        this.lag = lag;
    }

    public Dependency(Task task, DependencyType type) {
        this(task, type, 0);
    }

    public Task task() {
        return task;
    }

    public DependencyType type() {
        return type;
    }

    public int lag() {
        return lag;
    }

    public String toString() {
        return task.toString() + (type != DependencyType.FS ? "-" + type.toString() : "") + (lag != 0 ? lag : "");
    }

}
