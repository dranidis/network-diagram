package com.se.netdiagram;

import java.util.List;

/**
 * Reads a list of tasks from a json file, calculates ES, EF, LS, LF, Slack and
 * finds critical paths.
 */
public class App {
    public static void main(String[] args) {
        String jsonFile = "examples/tasks.json";
        if (args.length > 0)
            jsonFile = args[0];

        NetworkDiagram nd = new NetworkDiagram();
        FileReader fileReader = new FileReader();
        List<TaskData> taskJSONList = fileReader.readJsonFile(jsonFile);

        try {
            nd.processTaskList(taskJSONList);
        } catch (DuplicateTaskKeyException | KeyNotFoundException | CircularDependencyException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        nd.forwardAndBackWard();
        nd.print();
    }

}
