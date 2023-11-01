package com.se.netdiagram.application;

import com.se.netdiagram.domain.model.networkdiagram.NetworkDiagram;
import com.se.netdiagram.domain.model.networkdiagram.Path;
import com.se.netdiagram.domain.model.networkdiagram.Task;

public class PrinterService {

    private PrinterService() {
    }

    public static void printTasksAndCriticalPaths(NetworkDiagram nd, NetworkDiagramPrinter ndp) {

        ndp.printHeader(nd.projectEnd());

        for (Task task : nd.tasks()) {
            ndp.printTask(task, nd.projectEnd());
        }

        for (Path path : nd.getCriticalPaths()) {
            ndp.printCriticalPath(path);
        }
    }

}
