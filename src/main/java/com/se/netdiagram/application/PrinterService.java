package com.se.netdiagram.application;

import com.se.netdiagram.domain.model.NetworkDiagram;
import com.se.netdiagram.domain.model.Path;
import com.se.netdiagram.domain.model.Task;

public class PrinterService {

    private PrinterService() {
    }

    public static void printTasksAndCriticalPaths(NetworkDiagram nd, NetworkDiagramPrinter ndp) {

        ndp.printHeader();

        for (Task task : nd.tasks()) {
            ndp.printTask(task);
        }

        for (Path path : nd.getCriticalPaths()) {
            ndp.printCriticalPath(path);
        }
    }

}
