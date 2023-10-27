package com.se.netdiagram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.OptionalLong;

public class Task {
    private TaskId id;
    private int duration;
    private List<Task> pred = new ArrayList<>();
    public OptionalLong earliestStart;
    public OptionalLong earliestFinish;
    public OptionalLong latestStart;
    public OptionalLong latestFinish;
    public OptionalLong slack;
    private List<Task> succ = new ArrayList<>();

    public Task(TaskId taskId, int duration) {
        this.id = taskId;
        this.duration = duration;
    }

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
        return new ArrayList<>();
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
        return id.toString();
    }

    public void prettyprint() {
        String ANSI_RED = "\u001B[31m";
        String ANSI_RESET = "\u001B[0m";
        String criticalTask = " ";
        if (slack.getAsLong() == 0) {
            criticalTask = ANSI_RED + "*";
        }
        System.out.printf("%s %5s %4d %4d %4d %4d %4d %6d\n", criticalTask, id, duration, earliestStart.getAsLong(),
                earliestFinish.getAsLong(), latestStart.getAsLong(), latestFinish.getAsLong(), slack.getAsLong());
        if (!criticalTask.equals(""))
            System.out.print(ANSI_RESET);
    }

    public static void prettyprintHeader() {
        System.out.printf("%s %5s %4s %4s %4s %4s %4s %6s\n", " ", "ID", "DUR", "ES", "EF", "LS", "LF", "SLACK");
    }

    public TaskId id() {
        return id;
    }

    public String idAsString() {
        return id.toString();
    }

    public int duration() {
        return duration;
    }

    public List<Task> succ() {
        return Collections.unmodifiableList(succ);
    }

    public List<Task> pred() {
        return Collections.unmodifiableList(pred);
    }

    /**
     * Adds a predecessor to this task. Also adds this task as a successor to the
     * predecessor.
     * <p>
     * Domain constraint. - A task cannot be its own predecessor. - A task "A" with
     * a "B" as predecessor means that "B" must have "A" as a successor.
     * 
     * @param predTask
     */
    public void addPredecessor(Task predTask) {
        if (predTask == this)
            throw new IllegalArgumentException("A task cannot be its own predecessor!");

        pred.add(predTask);
        predTask.succ.add(this);
    }

    public void calculateEarliestValues() {
        this.earliestStart = OptionalLong.of(0);
        for (Task predTask : this.pred()) {
            this.earliestStart = Util.max(this.earliestStart, predTask.earliestFinish);
        }
        this.earliestFinish = OptionalLong.of(this.earliestStart.getAsLong() + this.duration());
    }

    public void calculateLatestValuesAndSlack(long projectEnd) {
        this.latestFinish = OptionalLong.of(projectEnd);
        for (Task succTask : this.succ()) {
            this.latestFinish = Util.min(this.latestFinish, succTask.latestStart);
        }
        this.latestStart = OptionalLong.of(this.latestFinish.getAsLong() - this.duration());
        this.slack = OptionalLong.of(this.latestFinish.getAsLong() - this.earliestFinish.getAsLong());
    }
}
