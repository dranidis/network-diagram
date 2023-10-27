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
    public void processTaskList(List<TaskData> taskList)
            throws DuplicateTaskKeyException, KeyNotFoundException, CircularDependencyException {

        tasks = new HashMap<>();

        populateTasksFrom(taskList);
        addPredecessorsToTasksFrom(taskList);
        checkForCircularDependencies();
    }

    public void forwardAndBackWard() {
        long projectEnd = forward();
        backward(projectEnd);
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
                if (!existsAtLeastOneInList(task.pred(), workingTasks)) {
                    task.calculateEarliestValues();
                    projectEnd = Util.max(projectEnd, task.earliestFinish);
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
                if (!existsAtLeastOneInList(task.succ(), workingTasks)) {
                    task.calculateLatestValuesAndSlack(projectEnd);
                    toRemove.add(task);
                }
            }
            workingTasks.removeAll(toRemove);
        }
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
                if (!existsAtLeastOneInList(task.pred(), workingTasks)) {
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
        for (Task predTask : task.pred()) {
            if (path.contains(predTask))
                predTasks.add(predTask);
        }
        return predTasks;
    }

    public Task getTask(String string) {
        TaskId taskId = new TaskId(string);
        return tasks.get(taskId);
    }

    private void checkForCircularDependencies() throws CircularDependencyException {
        List<Task> circular = Task.getCircularDependency(tasks.values());
        if (!circular.isEmpty()) {
            String path = "";
            for (Task t : circular) {
                path += t.id() + " -> ";
            }
            throw new CircularDependencyException("Circular dependency: " + path);
        }
    }

    private void addPredecessorsToTasksFrom(List<TaskData> taskList) throws KeyNotFoundException {
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
                task.addPredecessor(predTask);
            }
        }
    }

    private void populateTasksFrom(List<TaskData> taskList) throws DuplicateTaskKeyException {
        for (TaskData taskJSON : taskList) {
            TaskId taskId = new TaskId(taskJSON.id);
            if (tasks.containsKey(taskId)) {
                throw new DuplicateTaskKeyException("Task Id: " + taskId + " already exists!");
            }
            Task task = new Task(taskId, taskJSON.duration);
            tasks.put(task.id(), task);
        }
    }
}
