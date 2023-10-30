package com.se.netdiagram;

import com.se.netdiagram.application.JSONReaderService;
import com.se.netdiagram.application.PrinterService;
import com.se.netdiagram.domain.model.NetworkDiagram;
import com.se.netdiagram.port.adapter.JSONFileTaskDataReader;

/**
 * Reads a list of tasks from a json file, calculates ES, EF, LS, LF, Slack and
 * finds critical paths.
 */
public class App {
    public static void main(String[] args) {
        String jsonFile = "examples/tasks.json";
        if (args.length > 0)
            jsonFile = args[0];

        NetworkDiagram nd = JSONReaderService.readNetworkDiagramWith(
                new JSONFileTaskDataReader(jsonFile));

        PrinterService.print(nd);
    }

}
