package com.se.netdiagram.application;

import java.util.List;

/**
 * DTO for JSON file
 *
 */
public class TaskData {

    private final String id;
    private final int duration;
    private final List<String> predIds;

    public TaskData() {
        this("", 0, null);
    }

    public TaskData(String id, int duration, List<String> predIds) {
        this.id = id;
        this.duration = duration;
        this.predIds = predIds;
    }

    public String getId() {
        return id;
    }

    public int getDuration() {
        return duration;
    }

    public List<String> getPredIds() {
        return predIds;
    }
}
