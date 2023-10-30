package com.se.netdiagram.port.adapter;

import com.se.netdiagram.application.NetworkDiagramPrinter;
import com.se.netdiagram.domain.model.Path;
import com.se.netdiagram.domain.model.Task;

public class ConsoleNetworkDiagramPrinter implements NetworkDiagramPrinter {

    @Override
    public void printHeader() {
        System.out.printf("%s %5s %4s %4s %4s %4s %4s %6s\n", " ", "ID", "DUR", "ES", "EF", "LS", "LF", "SLACK");
    }

    @Override
    public void printTask(Task task) {
        String ANSI_RED = "\u001B[31m";
        String ANSI_RESET = "\u001B[0m";
        String criticalTask = " ";
        if (task.slack().getAsLong() == 0) {
            criticalTask = ANSI_RED + "*";
        }
        System.out.printf("%s %5s %4d %4d %4d %4d %4d %6d\n", criticalTask, task.id(), task.duration(),
                task.earliestStart().getAsLong(), task.earliestFinish().getAsLong(), task.latestStart().getAsLong(),
                task.latestFinish().getAsLong(), task.slack().getAsLong());
        if (!criticalTask.equals(""))
            System.out.print(ANSI_RESET);
    }

    @Override
    public void printCriticalPath(Path path) {
        for (Task task : path.tasks()) {
            System.out.printf("%5s ->", task.id());
        }
        System.out.println(" end");
    }

}
