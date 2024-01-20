package com.se.netdiagram.port.adapter;

import com.se.netdiagram.application.NetworkDiagramPrinter;
import com.se.netdiagram.domain.model.networkdiagram.Path;
import com.se.netdiagram.domain.model.networkdiagram.Task;

public class ConsoleNetworkDiagramPrinter implements NetworkDiagramPrinter {

    public ConsoleNetworkDiagramPrinter() {
        this(2);
    }

    public ConsoleNetworkDiagramPrinter(int scale) {
        this.scale = scale;
    }

    private String spacer = "  ";
    private String format = "%s %-5s %-4s %-4s %-4s %-4s %-4s %-6s %-10s";
    private int scale;

    private static final String BUSY = "█";
    private static final String SLACK = "▒";
    private static final String FREE = ".";

    @Override
    public void printHeader(long projectEnd) {
        System.out.printf(format, " ", "ID", "DUR", "ES", "EF", "LS", "LF", "SLACK", "PredID");

        System.out.print(spacer);

        // print numbers for each day of duration starting from ES
        for (int i = 0; i < projectEnd; i++) {
            System.out.printf("%-" + this.scale + "s", (this.scale > 2 ? i : i % 10));
        }
        System.out.println();
    }

    @Override
    public void printTask(Task task, long projectEnd) {
        String ANSI_RED = "\u001B[31m";
        String ANSI_RESET = "\u001B[0m";
        String criticalTask = " ";
        if (task.earliestLatestValues().slack().getAsLong() == 0) {
            criticalTask = ANSI_RED + "*";
        }
        // a string with the ids of the predecessors
        String predString = task.predecessors().isEmpty() ? "" : task.predecessors().get(0).toString();
        for (int i = 1; i < task.predecessors().size(); i++) {
            predString += ", " + task.predecessors().get(i).toString();
        }
        System.out.printf(format, criticalTask, task.id(), task.duration(),
                task.earliestLatestValues().earliestStart().getAsLong(),
                task.earliestLatestValues().earliestFinish().getAsLong(),
                task.earliestLatestValues().latestStart().getAsLong(),
                task.earliestLatestValues().latestFinish().getAsLong(),
                task.earliestLatestValues().slack().getAsLong(), predString);
        if (!criticalTask.equals(""))

            System.out.print(spacer);

        for (int i = 0; i < task.earliestLatestValues().earliestStart().getAsLong(); i++) {
            System.out.print(scaleString(FREE));
        }

        for (int i = 0; i < task.durationAsInt(); i++) {
            System.out.print(scaleString(BUSY));
        }

        for (int i = 0; i < task.earliestLatestValues().slack().getAsLong(); i++) {
            System.out.print(scaleString(SLACK));
        }

        for (int i = 0; i < projectEnd - task.earliestLatestValues().latestFinish().getAsLong(); i++) {
            System.out.print(scaleString(FREE));
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

    private String scaleString(String string) {
        String tmpString = "";
        for (int i = 0; i < this.scale; i++) {
            tmpString += string;
        }
        return tmpString;
    }

}
