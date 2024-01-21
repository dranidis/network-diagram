package com.se.netdiagram;

import com.se.netdiagram.application.DiagramNetworkReaderService;
import com.se.netdiagram.application.ParsingError;
import com.se.netdiagram.application.PrinterService;
import com.se.netdiagram.domain.model.exceptions.DuplicateTaskKeyException;
import com.se.netdiagram.domain.model.exceptions.KeyNotFoundException;
import com.se.netdiagram.domain.model.networkdiagram.NetworkDiagram;
import com.se.netdiagram.port.adapter.ConsoleNetworkDiagramPrinter;
import com.se.netdiagram.port.adapter.JSONFileTaskDataReader;

/**
 * Reads a list of tasks from a json file, calculates ES, EF, LS, LF, Slack and
 * finds critical paths.
 */
public class App {
    public static void main(String[] args) throws DuplicateTaskKeyException, KeyNotFoundException, ParsingError {
        /**
         * Default json file
         */
        String jsonFile = "examples/tasks.json";
        int scale = 2;

        if (args.length > 0)
            jsonFile = args[0];

        if (args.length > 1)
            scale = Integer.parseInt(args[1]);
        NetworkDiagram nd;

        nd = DiagramNetworkReaderService.readNetworkDiagramWith(new JSONFileTaskDataReader(jsonFile));

        PrinterService.printTasksAndCriticalPaths(nd, new ConsoleNetworkDiagramPrinter(scale));

    }

}
