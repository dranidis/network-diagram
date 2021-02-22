package com.se.netdiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class NetworkDiagram {
    private Map<String, Task> tasks = new HashMap<>();

    /**
     * 
     * @param taskList
     * @throws DuplicateTaskKeyException
     * @throws KeyNotFoundException
     */
    public void readTasklist(List<TaskJSON> taskList)
            throws DuplicateTaskKeyException, KeyNotFoundException, CircularDependencyException {
        tasks = new HashMap<>();

        for (TaskJSON taskJSON : taskList) {
            if (tasks.containsKey(taskJSON.id)) {
                throw new DuplicateTaskKeyException("Task Id: " + taskJSON.id + " already exists!");
            }
            Task task = new Task();
            task.id = taskJSON.id;
            task.duration = taskJSON.duration;
            tasks.put(task.id, task);
        }

        for (TaskJSON taskJSON : taskList) {
            Task task = tasks.get(taskJSON.id);
            for (String predId : taskJSON.pred) {
                Task predTask = tasks.get(predId);
                if (predTask == null) {
                    throw new KeyNotFoundException("Not existing predecessor KEY: " + predId + " in Task: " + task.id);
                }
                task.pred.add(predTask);
            }
        }

        successors();

        List<Task> checked = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (!checked.contains(task)) {
                List<Task> visited = new ArrayList<>();
                checkCircular(task, visited, checked);
            }
        }
    }

    private void checkCircular(Task task, List<Task> visited, List<Task> checked) throws CircularDependencyException {
        List<Task> visitedCopy = new ArrayList<>(visited);
        for (Task succTask : task.succ) {
            if (visitedCopy.contains(succTask)) {
                String path = "";
                for (Task t : visitedCopy) {
                    path += t.id + " -> ";
                }
                path += task.id + " -> " + succTask.id;
                throw new CircularDependencyException("Circular dependency: " + path);
            }
            visitedCopy.add(task);
            checked.add(task);
            checkCircular(succTask, visitedCopy, checked);
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
        System.out.printf("%s %5s %4s %4s %4s %4s %4s %6s\n", " ", "ID", "DUR", "ES", "EF", "LS", "LF", "SLACK");

        for (Task task : tasks.values()) {
            String criticalTask = " ";
            if (task.slack.getAsLong() == 0) {
                criticalTask = "*";
            }
            System.out.printf("%s %5s %4d %4d %4d %4d %4d %6d\n", criticalTask, task.id, task.duration,
                    task.earliestStart.getAsLong(), task.earliestFinish.getAsLong(), task.latestStart.getAsLong(),
                    task.latestFinish.getAsLong(), task.slack.getAsLong());
        }

        for (List<Task> path : getCriticalPaths()) {
            for (Task task : path) {
                System.out.printf("%5s ->", task.id);
            }
            System.out.println(" end");
        }

    }

    public void readJsonFile(String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        List<TaskJSON> taskJSONList;
        try {
            taskJSONList = mapper.readValue(new File(fileName), new TypeReference<List<TaskJSON>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            readTasklist(taskJSONList);
        } catch (DuplicateTaskKeyException | KeyNotFoundException | CircularDependencyException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
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
        boolean inList = false;
        for (Task predTask : tasks) {
            if (taskList.contains(predTask)) {
                inList = true;
                break;
            }
        }
        return inList;
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
        return tasks.get(string);
    }

}