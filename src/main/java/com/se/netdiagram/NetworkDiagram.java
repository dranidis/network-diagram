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
     * refactor to create a list of pred/succ tasks instead of strings
     * 
     * @param taskList
     * @throws DuplicateTaskKeyException
     * @throws KeyNotFoundException
     */
    public void readTasklist(List<Task> taskList) throws DuplicateTaskKeyException, KeyNotFoundException {
        tasks = new HashMap<>();

        for (Task task : taskList) {
            if (tasks.containsKey(task.id)) {
                throw new DuplicateTaskKeyException("Id: " + task.id + " already exists!");
            }
            tasks.put(task.id, task);
        }

        for (Task task : taskList) {
            for (String predId : task.pred) {
                if (!tasks.containsKey(predId)) {
                    throw new KeyNotFoundException("Not existing pred KEY: " + predId);
                }
            }
        }
    }

    /**
     * Makes forward processing assigning values to ES, EF
     * 
     * @return project end
     */
    public long forward() {
        OptionalLong projectEnd = OptionalLong.of(0);

        List<Task> workingTasks = new ArrayList<>(tasks.values());

        while (!workingTasks.isEmpty()) {
            List<Task> toRemove = new ArrayList<>();
            for (Task task : workingTasks) {
                boolean canBeEvaluated = true;
                task.earliestStart = OptionalLong.of(0);
                for (String predId : task.pred) {
                    Task predTask = tasks.get(predId);
                    if (workingTasks.contains(predTask)) {
                        canBeEvaluated = false;
                        break;
                    }
                    task.earliestStart = max(task.earliestStart, predTask.earliestFinish);
                }

                if (canBeEvaluated) {
                    task.earliestFinish = OptionalLong.of(task.earliestStart.getAsLong() + task.duration);
                    projectEnd = max(projectEnd, task.earliestFinish);
                    toRemove.add(task);
                }
            }
            workingTasks.removeAll(toRemove);
        }
        return projectEnd.getAsLong();

    }

    public void backward(long projectEnd) {
        List<Task> workingTasks = new ArrayList<>(tasks.values());

        while (!workingTasks.isEmpty()) {
            List<Task> toRemove = new ArrayList<>();
            for (Task task : workingTasks) {
                boolean canBeEvaluated = true;
                task.latestFinish = OptionalLong.of(projectEnd);
                for (String succId : task.succ) {
                    Task succTask = tasks.get(succId);
                    if (workingTasks.contains(succTask)) {
                        canBeEvaluated = false;
                        break;
                    }
                    task.latestFinish = min(task.latestFinish, succTask.latestStart);
                }

                if (canBeEvaluated) {
                    task.latestStart = OptionalLong.of(task.latestFinish.getAsLong() - task.duration);
                    task.slack = OptionalLong.of(task.latestFinish.getAsLong() - task.earliestFinish.getAsLong());
                    toRemove.add(task);
                }
            }
            workingTasks.removeAll(toRemove);
        }
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

    public Map<String, Task> getTasks() {
        return tasks;
    }

    public void successors() {
        for (Task task : tasks.values()) {
            for (String pred : task.pred) {
                Task predTask = tasks.get(pred);
                predTask.succ.add(task.id);
            }
        }
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

    public void process() {
        successors();
        long projectEnd = forward();
        backward(projectEnd);
    }

	public void readJsonFile(String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        List<Task> taskList = null;

        try {
            taskList = mapper.readValue(new File(fileName), new TypeReference<List<Task>>(){});
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } 
        
        try {
            readTasklist(taskList);
        }
        catch (DuplicateTaskKeyException e) {
            e.printStackTrace();
        } catch (KeyNotFoundException e) {
            e.printStackTrace();
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
                if (taskPredecessorsAreNotInList(task, workingTasks)) {
                    addTaskToPaths(task, paths);
                    toRemove.add(task);
                }
            }
            workingTasks.removeAll(toRemove);
        }

        return paths;
    }

    private boolean taskPredecessorsAreNotInList(Task task, List<Task> taskList) {
        boolean notInList = true;
        for (String predId : task.pred) {
            Task predTask = tasks.get(predId);
            if (taskList.contains(predTask)) {
                notInList = false;
                break;
            }
        }
        return notInList;
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
        if (predTasks.contains(path.get(path.size()-1))) {
            path.add(task);
        } else {
            List<Task> clonedPath = new ArrayList<>(path);
            clonedPath.remove(clonedPath.size()-1);
            clonedPath.add(task);
            paths.add(clonedPath);
        }
    }

    private List<Task> getTaskPredIsInPath(Task task, List<Task> path) {
        List<Task> predTasks = new ArrayList<>();
        for (String predId : task.pred) {
            Task predTask = tasks.get(predId);
            if (path.contains(predTask)) 
                predTasks.add(predTask);
        }
        return predTasks;
    }

}