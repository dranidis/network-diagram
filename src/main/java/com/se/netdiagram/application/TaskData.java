package com.se.netdiagram.application;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for JSON file
 */
public class TaskData {

    private final String id;
    private final int duration;
    private final List<DependencyData> predIds;

    public TaskData() {
        this("", 0, null);
    }

    public TaskData(String id, int duration, List<DependencyData> predIds) {
        this.id = id;
        this.duration = duration;
        this.predIds = predIds;
    }

    public TaskData(String string, int i) {
        this(string, i, new ArrayList<>());
    }

    public String getId() {
        return id;
    }

    public int getDuration() {
        return duration;
    }

    public List<DependencyData> getPredIds() {
        return predIds;
    }

    // DSL
    public static TaskData task(String id) {
        return new TaskData(id, 0);
    }

    public static TaskData task(String id, int duration) {
        return new TaskData(id, duration);
    }

    public TaskData withPred(String predId) {
        predIds.add(new DependencyData(predId));
        return this;
    }

    public TaskData withPred(String predId, String dependencyType) {
        predIds.add(new DependencyData(predId, dependencyType, 0));
        return this;
    }

    public TaskData withPred(String predId, String dependencyType, int lag) {
        predIds.add(new DependencyData(predId, dependencyType, lag));
        return this;
    }

}
