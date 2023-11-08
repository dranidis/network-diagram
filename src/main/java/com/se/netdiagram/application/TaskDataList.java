package com.se.netdiagram.application;

import java.util.ArrayList;
import java.util.List;

// DSL for a type safe way to create a list of tasks and add tasks
// to the list
public class TaskDataList {
    private List<TaskData> tasks;

    public TaskDataList() {
        tasks = new ArrayList<>();
    }

    public TaskDataList(List<TaskData> taskList) {
        this.tasks = taskList;
    }

    public List<TaskData> tasks() {
        return tasks;
    }

    // DSL for a type safe way to create a list of tasks and add tasks
    // to the list
    public static TaskDataList taskList() {
        return new TaskDataList();
    }

    public TaskDataList add(TaskData task) {
        tasks.add(task);
        return this;
    }
}
