package com.se.netdiagram.application;

import java.util.List;

/**
 * DTO for JSON file
 *
 */
public class TaskData {

    private final String id;
    private final int duration;
    private final List<String> pred;

    public TaskData() {
        id = "";
        duration = 0;
        pred = null;
    }

    public TaskData(String string, int i, List<String> asList) {
        id = string;
        duration = i;
        pred = asList;
    }

    public String getId() {
        return id;
    }

    public int getDuration() {
        return duration;
    }

    public List<String> getPred() {
        return pred;
    }
}
