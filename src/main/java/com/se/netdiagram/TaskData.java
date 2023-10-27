package com.se.netdiagram;

import java.util.List;

public class TaskData {
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

    public final String id;
    public final int duration;
    public final List<String> pred;
}
