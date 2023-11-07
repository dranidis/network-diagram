package com.se.netdiagram.domain.model.networkdiagram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

import com.se.netdiagram.domain.model.exceptions.DuplicateTaskKeyException;
import com.se.netdiagram.domain.model.exceptions.KeyNotFoundException;
import com.se.netdiagram.domain.model.utilities.Util;

public class NetworkDiagram {
    private Map<TaskId, Task> tasks = new HashMap<>();
    private long projectEnd;

    public Collection<Task> tasks() {
        return this.tasks.values();
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
        Task task = new Task(taskId, duration);
        tasks.put(task.id(), task);

        forwardAndBackWard();
    }

    /**
     * Adds predecessor to a task. For consistency, a forward and backward
     * processing is triggered that updates the ES, EF, LS, LF and Slack values of
     * all tasks.
     */
    public void addPredecessorToTask(String aTaskId, String aPredId, String dependencyType, int lag)
            throws KeyNotFoundException {
        TaskId taskId = new TaskId(aTaskId);
        Task task = tasks.get(taskId);
        TaskId predTaskId = new TaskId(aPredId);
        Task predTask = tasks.get(predTaskId);

        if (predTask == null) {
            throw new KeyNotFoundException("Not existing predecessor KEY: " + aPredId + " in Task: " + task.id());
        }

        task.addPredecessor(new Dependency(predTask, DependencyType.valueOf(dependencyType), lag));

        forwardAndBackWard();
    }

    private void forwardAndBackWard() {
        projectEnd = forward();
        backward(projectEnd);
    }

    public List<Path> getCriticalPaths() {
        List<Path> paths = new ArrayList<>();

        List<Task> workingTasks = new ArrayList<>();

        for (Task task : tasks()) {
            if (task.slack().getAsLong() == 0)
                workingTasks.add(task);
        }

        while (!workingTasks.isEmpty()) {
            List<Task> tasksToRemoveFromWorking = new ArrayList<>();
            for (Task task : workingTasks) {
                if (!task.dependsOnAnyTaskFrom(workingTasks)) {
                    addTaskToPaths(task, paths);
                    tasksToRemoveFromWorking.add(task);
                }
            }
            workingTasks.removeAll(tasksToRemoveFromWorking);
        }

        return paths;
    }

    /**
     * Makes forward processing assigning values to ES, EF
     * 
     * @return project end
     */
    private long forward() {
        OptionalLong projectEnd = OptionalLong.of(0);
        List<Task> notProcessedTasks = new ArrayList<>(tasks());

        while (!notProcessedTasks.isEmpty()) {
            List<Task> processedTasks = new ArrayList<>();

            for (Task task : notProcessedTasks) {
                if (!task.dependsOnAnyTaskFrom(notProcessedTasks)) {
                    task.calculateEarliestValues();
                    projectEnd = Util.max(projectEnd, task.earliestFinish());
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
                    task.calculateLatestValuesAndSlack(projectEnd);

                    processedTasks.add(task);
                }
            }
            notProcessedTasks.removeAll(processedTasks);
        }
    }

    private void addTaskToPaths(Task task, List<Path> paths) {
        boolean added = false;
        for (Path path : new ArrayList<>(paths)) {
            List<Task> predTasks = getTaskPredIsInPath(task, path);
            if (!predTasks.isEmpty()) {
                appendTaskToPaths(task, predTasks, path, paths);
                added = true;
            }
        }
        if (!added) {
            Path path = new Path();
            path.addTask(task);
            paths.add(path);
        }
    }

    private void appendTaskToPaths(Task task, List<Task> predTasks, Path path, List<Path> paths) {
        if (predTasks.contains(path.lasttask(")")) {
            path.addTask(task);
        } else {
            Path clonedPath = new Path(path.tasks());
            clonedPath.removeLastTask();
            clonedPath.addTask(task);
            paths.add(clonedPath);
        }
    }

    private List<Task> getTaskPredIsInPath(Task task, Path path) {
        List<Task> predTasks = new ArrayList<>();
        for (Dependency predDependency : task.predecessors()) {
            Task predTask = predDependency.task();
            if (path.containsTask(predTask))
                predTasks.add(predTask);
        }
        return predTasks;
    }

}
