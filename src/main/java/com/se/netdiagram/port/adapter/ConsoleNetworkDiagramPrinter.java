package com.se.netdiagram.port.adapter;

import com.se.netdiagram.application.NetworkDiagramPrinter;
import com.se.netdiagram.domain.model.networkdiagram.Path;
import com.se.netdiagram.domain.model.networkdiagram.Task;

public class ConsoleNetworkDiagramPrinter implements NetworkDiagramPrinter {

    String spacer = "  ";
    String format = "%s %5s %4s %4s %4s %4s %4s %6s %10s";

    @Override
    public void printHeader() {
        System.out.printf(format, " ", "ID", "DUR", "ES", "EF", "LS", "LF", "SLACK", "PID");

        System.out.print(spacer);

        // print numbers for each day of duration starting from ES
        for (int i = 0; i < 20; i++) {
            System.out.print(i % 10);
            System.out.print(" ");
        }
        System.out.println();
    }

    @Override
    public void printTask(Task task) {
        String ANSI_RED = "\u001B[31m";
        String ANSI_RESET = "\u001B[0m";
        String criticalTask = " ";
        if (task.slack().getAsLong() == 0) {
            criticalTask = ANSI_RED + "*";
        }
        // a string with the ids of the predecessors
        String predString = task.predecessors().size() > 0 ? task.predecessors().get(0).toString() : "";
        for (int i = 1; i < task.predecessors().size(); i++) {
            predString += ", " + task.predecessors().get(i).toString();
        }
        System.out.printf(format, criticalTask, task.id(), task.duration(), task.earliestStart().getAsLong(),
                task.earliestFinish().getAsLong(), task.latestStart().getAsLong(), task.latestFinish().getAsLong(),
                task.slack().getAsLong(), predString);
        if (!criticalTask.equals(""))

            System.out.print(spacer);

        // print a █ character for each day of duration starting from ES
        for (int i = 0; i < task.earliestStart().getAsLong(); i++) {
            System.out.print("  ");
        }

        for (int i = 0; i < task.duration(); i++) {
            System.out.print("██");
        }

        // print a ▒ character for each day of slack
        for (int i = 0; i < task.slack().getAsLong(); i++) {
            System.out.print("▒▒");
        }

        System.out.print(ANSI_RESET);

        System.out.println();
        System.out.println();

    }

    @Override
    public void printCriticalPath(Path path) {
        for (Task task : path.tasks()) {
            System.out.printf("%5s ->", task.id());
        }
        System.out.println(" end");
    }

}
