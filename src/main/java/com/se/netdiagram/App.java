package com.se.netdiagram;

import java.util.List;

import com.se.netdiagram.application.PrinterService;
import com.se.netdiagram.domain.model.NetworkDiagram;
import com.se.netdiagram.domain.model.exceptions.DuplicateTaskKeyException;
import com.se.netdiagram.domain.model.exceptions.KeyNotFoundException;

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
        } catch (DuplicateTaskKeyException | KeyNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        nd.forwardAndBackWard();
        PrinterService.print(nd);
    }

}
