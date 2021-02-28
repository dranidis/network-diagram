package com.se.netdiagram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.OptionalLong;

public class Task {
    public String id;
    public int duration;
    public List<Task> pred = new ArrayList<>();
    public OptionalLong earliestStart;
    public OptionalLong earliestFinish;
    public OptionalLong latestStart;
    public OptionalLong latestFinish;
    public OptionalLong slack;
    public List<Task> succ = new ArrayList<>();

    public static List<Task> getCircularDependency(Collection<Task> tasks) {
        List<Task> checked = new ArrayList<>();
        for (Task task : tasks) {
            if (!checked.contains(task)) {
                List<Task> visited = new ArrayList<>();
                List<Task> circular = getCircular(task, visited, checked);
                if (circular != null)
                    return circular;
            }
        }
        return null;
    }

    private static List<Task> getCircular(Task task, List<Task> visited, List<Task> checked) {
        List<Task> visitedCopy = new ArrayList<>(visited);
        visitedCopy.add(task);
        checked.add(task);

        for (Task succTask : task.succ) {
            if (visitedCopy.contains(succTask)) {
                return visitedCopy;
            }
            if (!checked.contains(succTask)) {
                List<Task> circular = getCircular(succTask, visitedCopy, checked);
                if (circular != null) {
                    return circular;
                }
            }
        }
        return null;
    }

    public String toString() {
        return id;
    }
}
