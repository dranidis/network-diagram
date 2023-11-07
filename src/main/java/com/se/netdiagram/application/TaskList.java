package com.se.netdiagram.application;

import java.util.ArrayList;
import java.util.List;

// DSL for a type safe way to create a list of tasks and add tasks
// to the list
public class TaskList {
    private List<TaskData> taskList = new ArrayList<>();

    public static TaskList taskList() {
        return new TaskList();
    }

    public TaskList add(TaskData task) {
        taskList.add(task);
        return this;
    }

    public List<TaskData> get() {
        return taskList;
    }
}
