package com.se.netdiagram.domain.model.networkdiagram;

public class Dependency {
    private Task task;
    private DependencyType type;

    public Dependency(Task task, DependencyType type) {
        this.task = task;
        this.type = type;
    }

    public Task task() {
        return task;
    }

    public DependencyType type() {
        return type;
    }

}
