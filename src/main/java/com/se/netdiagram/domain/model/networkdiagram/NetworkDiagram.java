package com.se.netdiagram.domain.model.networkdiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.se.netdiagram.domain.model.exceptions.DuplicateTaskKeyException;
import com.se.netdiagram.domain.model.exceptions.KeyNotFoundException;
import com.se.netdiagram.domain.model.networkdiagram.date.Date;
import com.se.netdiagram.domain.model.networkdiagram.date.Duration;
import com.se.netdiagram.domain.model.networkdiagram.date.Lag;
import com.se.netdiagram.domain.model.utilities.Query;

public class NetworkDiagram {
    private Map<TaskId, Task> tasks = new HashMap<>();
    private long projectEnd;

    public List<Task> tasks() {
        return new ArrayList<>(this.tasks.values());
    }

    public Task getTask(String string) {
        return tasks.get(new TaskId(string));
    }

    public long projectEnd() {
        return projectEnd;
    }

    /**
     * Adds a task to the network diagram. For consistency, a forward and backward
     * processing is triggered that updates the ES, EF, LS, LF and Slack values of
     * all tasks.
     * 
     * @param id
     * @param duration
     * @throws DuplicateTaskKeyException
     */
    public void addTask(String id, int duration) throws DuplicateTaskKeyException {
        TaskId taskId = new TaskId(id);
        if (tasks.containsKey(taskId)) {
            throw new DuplicateTaskKeyException("Task Id: " + taskId + " already exists!");
        }
        Task task = new Task(taskId, new Duration(duration));
        tasks.put(task.id(), task);

        forwardAndBackWard();
    }

    /**
     * Adds predecessor to a task. For consistency, a forward and backward
     * processing is triggered that updates the ES, EF, LS, LF and Slack values of
     * all tasks.
     */
    public void addPredecessorToTask(String aTaskId, String aPredId, String dependencyType, long lag)
            throws KeyNotFoundException {
        TaskId taskId = new TaskId(aTaskId);
        Task task = tasks.get(taskId);
        TaskId predTaskId = new TaskId(aPredId);
        Task predTask = tasks.get(predTaskId);

        if (predTask == null) {
            throw new KeyNotFoundException("Not existing predecessor KEY: " + aPredId + " in Task: " + task.id());
        }

        task.addPredecessor(new Dependency(
                predTask,
                DependencyType.valueOf(dependencyType),
                new Lag(lag)));

        forwardAndBackWard();
    }

    private void forwardAndBackWard() {
        projectEnd = forward();
        backward(projectEnd);
    }

    /**
     * Makes forward processing assigning values to ES, EF
     * 
     * @return project end
     */
    private long forward() {
        Date projectEnd = new Date(0);
        List<Task> notProcessedTasks = new ArrayList<>(tasks());

        while (!notProcessedTasks.isEmpty()) {
            List<Task> processedTasks = new ArrayList<>();

            for (Task task : notProcessedTasks) {
                if (!task.dependsOnAnyTaskFrom(notProcessedTasks)) {
                    task.calculateEarliest();
                    projectEnd = new Date(
                            Math.max(projectEnd.getAsLong(),
                                    task.earliestLatest().earliestFinish().getAsLong()));
                    processedTasks.add(task);
                }
            }
            notProcessedTasks.removeAll(processedTasks);
        }
        return projectEnd.getAsLong();
    }

    private void backward(long projectEnd) {
        List<Task> notProcessedTasks = new ArrayList<>(tasks());

        while (!notProcessedTasks.isEmpty()) {
            List<Task> processedTasks = new ArrayList<>();
            for (Task task : notProcessedTasks) {
                if (!task.haveAnyTaskDependingOnMeFrom(notProcessedTasks)) {
                    task.calculateLatestAndSlack(projectEnd);
                    processedTasks.add(task);
                }
            }
            notProcessedTasks.removeAll(processedTasks);
        }
    }

    /**
     * Returns the critical paths of the network diagram. A critical path is a path
     * that has zero slack for all its tasks.
     * 
     * @return
     */
    public List<Path> getCriticalPaths() {
        List<Path> criticalPaths = new ArrayList<>();

        List<Task> tasksWithZeroSlack = Query.filter(
                tasks(),
                task -> task.slack() == 0);

        while (!tasksWithZeroSlack.isEmpty()) {
            List<Task> tasksToRemoveFromWorking = new ArrayList<>();
            for (Task task : tasksWithZeroSlack) {
                if (!task.dependsOnAnyTaskFrom(tasksWithZeroSlack)) {
                    addTaskToCriticalPaths(task, criticalPaths);
                    tasksToRemoveFromWorking.add(task);
                }
            }
            tasksWithZeroSlack.removeAll(tasksToRemoveFromWorking);
        }

        return criticalPaths;
    }

    private void addTaskToCriticalPaths(Task task, List<Path> criticalPaths) {
        boolean added = false;
        for (Path criticalPath : new ArrayList<>(criticalPaths)) {
            List<Task> predTasksInCriticalPath = Query.filterAndMap(
                    task.predecessors(),
                    dep -> criticalPath.containsTask(dep.task()),
                    dep -> dep.task());
            ;
            if (!predTasksInCriticalPath.isEmpty()) {
                appendTaskToPaths(task, predTasksInCriticalPath, criticalPath, criticalPaths);
                added = true;
            }
        }
        if (!added) {
            criticalPaths.add(new Path().addTask(task));
        }
    }

    /**
     * If any of the task predecessors is equal to the last task of the path, it
     * replaces in the paths the path with a new path that has the task appended to
     * it. Otherwise, it adds a new path to the list of paths, with the last task
     * removed and the task appended to it.
     * 
     * @param task
     * @param predTasks
     * @param path
     * @param paths
     */
    private void appendTaskToPaths(Task task, List<Task> predTasks, Path path, List<Path> paths) {
        // System.out.println("task: " + task.toString());
        // System.out.println("task deps: " + task.predecessors().toString());
        // System.out.println("predTasks: " + predTasks.toString());
        // System.out.println("path: " + path.toString());
        // System.out.println("paths: " + paths.toString());
        if (predTasks.contains(path.lastTask())) {
            paths.remove(path);
            paths.add(path.addTask(task));
        } else {
            Path pathButLast = path.removeLastTask();
            if (Query.map(
                    task.predecessors(),
                    p -> p.task())
                    .contains(pathButLast.lastTask())) {
                paths.add(pathButLast.addTask(task));
            }
        }
        // System.out.println("POST paths: " + paths.toString());
    }
}
