package com.se.netdiagram;

import com.se.netdiagram.application.JSONReader;
import com.se.netdiagram.application.PrinterService;
import com.se.netdiagram.domain.model.NetworkDiagram;

/**
 * Reads a list of tasks from a json file, calculates ES, EF, LS, LF, Slack and
 * finds critical paths.
 */
public class App {
    public static void main(String[] args) {
        String jsonFile = "examples/tasks.json";
        if (args.length > 0)
            jsonFile = args[0];

        NetworkDiagram nd = JSONReader.readNetworkDiagramFromJSONFile(jsonFile);

        PrinterService.print(nd);
    }

}
