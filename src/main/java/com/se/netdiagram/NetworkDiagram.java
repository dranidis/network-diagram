package com.se.netdiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

public class NetworkDiagram {
    private Map<TaskId, Task> tasks = new HashMap<>();

    /**
     * @param taskList
     * @throws DuplicateTaskKeyException
     * @throws KeyNotFoundException
     */
    public void readTasklist(List<TaskData> taskList)
            throws DuplicateTaskKeyException, KeyNotFoundException, CircularDependencyException {
        tasks = new HashMap<>();

        for (TaskData taskJSON : taskList) {
            TaskId taskId = new TaskId(taskJSON.id);
            if (tasks.containsKey(taskId)) {
                throw new DuplicateTaskKeyException("Task Id: " + taskId + " already exists!");
            }
            Task task = new Task(taskId);
            task.duration = taskJSON.duration;
            tasks.put(task.id(), task);
        }

        for (TaskData taskJSON : taskList) {
            TaskId taskId = new TaskId(taskJSON.id);
            Task task = tasks.get(taskId);
            for (String predId : taskJSON.pred) {
                TaskId predTaskId = new TaskId(predId);

                Task predTask = tasks.get(predTaskId);
                if (predTask == null) {
                    throw new KeyNotFoundException(
                            "Not existing predecessor KEY: " + predId + " in Task: " + task.id());
                }
                task.pred.add(predTask);
            }
        }

        successors();

        List<Task> circular = Task.getCircularDependency(tasks.values());
        if (circular != null) {
            String path = "";
            for (Task t : circular) {
                path += t.id() + " -> ";
            }
            throw new CircularDependencyException("Circular dependency: " + path);
        }
    }

    public void preProcess(List<TaskData> taskJSONList) {
        try {
            readTasklist(taskJSONList);
        } catch (DuplicateTaskKeyException | KeyNotFoundException | CircularDependencyException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void process() {
        long projectEnd = forward();
        backward(projectEnd);
    }

    /**
     * Create succ links
     */
    private void successors() {
        for (Task task : tasks.values()) {
            for (Task predTask : task.pred) {
                predTask.succ.add(task);
            }
        }
    }

    /**
     * Makes forward processing assigning values to ES, EF
     * 
     * @return project end
     */
    private long forward() {
        OptionalLong projectEnd = OptionalLong.of(0);
        List<Task> workingTasks = new ArrayList<>(tasks.values());

        while (!workingTasks.isEmpty()) {
            List<Task> toRemove = new ArrayList<>();
            for (Task task : workingTasks) {
                if (!existsAtLeastOneInList(task.pred, workingTasks)) {
                    calculateEarliestValues(task);
                    projectEnd = max(projectEnd, task.earliestFinish);
                    toRemove.add(task);
                }
            }
            workingTasks.removeAll(toRemove);
        }
        return projectEnd.getAsLong();
    }

    private void backward(long projectEnd) {
        List<Task> workingTasks = new ArrayList<>(tasks.values());

        while (!workingTasks.isEmpty()) {
            List<Task> toRemove = new ArrayList<>();
            for (Task task : workingTasks) {
                if (!existsAtLeastOneInList(task.succ, workingTasks)) {
                    calculateLatestValuesAndSlack(task, projectEnd);
                    toRemove.add(task);
                }
            }
            workingTasks.removeAll(toRemove);
        }
    }

    private void calculateEarliestValues(Task task) {
        task.earliestStart = OptionalLong.of(0);
        for (Task predTask : task.pred) {
            task.earliestStart = max(task.earliestStart, predTask.earliestFinish);
        }
        task.earliestFinish = OptionalLong.of(task.earliestStart.getAsLong() + task.duration);
    }

    private void calculateLatestValuesAndSlack(Task task, long projectEnd) {
        task.latestFinish = OptionalLong.of(projectEnd);
        for (Task succTask : task.succ) {
            task.latestFinish = min(task.latestFinish, succTask.latestStart);
        }
        task.latestStart = OptionalLong.of(task.latestFinish.getAsLong() - task.duration);
        task.slack = OptionalLong.of(task.latestFinish.getAsLong() - task.earliestFinish.getAsLong());
    }

    private OptionalLong max(OptionalLong oldMax, OptionalLong newValue) {
        if (oldMax.getAsLong() < newValue.getAsLong()) {
            return newValue;
        }
        return oldMax;
    }

    private OptionalLong min(OptionalLong oldMin, OptionalLong newValue) {
        if (oldMin.getAsLong() > newValue.getAsLong()) {
            return newValue;
        }
        return oldMin;
    }

    public void print() {
        Task.prettyprintHeader();

        for (Task task : tasks.values()) {
            task.prettyprint();
        }

        for (List<Task> path : getCriticalPaths()) {
            for (Task task : path) {
                System.out.printf("%5s ->", task.id());
            }
            System.out.println(" end");
        }

    }

    public List<List<Task>> getCriticalPaths() {
        List<List<Task>> paths = new ArrayList<>();

        List<Task> workingTasks = new ArrayList<>();

        for (Task task : tasks.values()) {
            if (task.slack.getAsLong() == 0)
                workingTasks.add(task);
        }

        while (!workingTasks.isEmpty()) {
            List<Task> toRemove = new ArrayList<>();
            for (Task task : workingTasks) {
                if (!existsAtLeastOneInList(task.pred, workingTasks)) {
                    addTaskToPaths(task, paths);
                    toRemove.add(task);
                }
            }
            workingTasks.removeAll(toRemove);
        }

        return paths;
    }

    private boolean existsAtLeastOneInList(List<Task> tasks, List<Task> taskList) {
        for (Task task : tasks)
            if (taskList.contains(task))
                return true;

        return false;
    }

    private void addTaskToPaths(Task task, List<List<Task>> paths) {
        boolean added = false;
        for (List<Task> path : new ArrayList<>(paths)) {
            List<Task> predTasks = getTaskPredIsInPath(task, path);
            if (!predTasks.isEmpty()) {
                appendTaskToPaths(task, predTasks, path, paths);
                added = true;
            }
        }
        if (!added) {
            List<Task> path = new ArrayList<>();
            path.add(task);
            paths.add(path);
        }

    }

    private void appendTaskToPaths(Task task, List<Task> predTasks, List<Task> path, List<List<Task>> paths) {
        if (predTasks.contains(path.get(path.size() - 1))) {
            path.add(task);
        } else {
            List<Task> clonedPath = new ArrayList<>(path);
            clonedPath.remove(clonedPath.size() - 1);
            clonedPath.add(task);
            paths.add(clonedPath);
        }
    }

    private List<Task> getTaskPredIsInPath(Task task, List<Task> path) {
        List<Task> predTasks = new ArrayList<>();
        for (Task predTask : task.pred) {
            if (path.contains(predTask))
                predTasks.add(predTask);
        }
        return predTasks;
    }

    public Task getTask(String string) {
        TaskId taskId = new TaskId(string);
        return tasks.get(taskId);
    }

}
