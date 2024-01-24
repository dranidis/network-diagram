package com.se.netdiagram.domain.model.networkdiagram;

import com.se.netdiagram.domain.model.networkdiagram.date.Lag;

public class Dependency {
    private Task task;
    private DependencyType type;
    private Lag lag;

    public Dependency(Task task, DependencyType type, Lag lag) {
        this.task = task;
        this.type = type;
        this.lag = lag;
    }

    public Dependency(Task task, DependencyType type) {
        this(task, type, new Lag(0));
    }

    public Task task() {
        return task;
    }

    public DependencyType type() {
        return type;
    }

    public Lag lag() {
        return lag;
    }

    public String toString() {
        return task.toString() + (type != DependencyType.FS ? "-" + type.toString() : "")
                + (lag.value() != 0 ? lag.value() : "");
    }

}
