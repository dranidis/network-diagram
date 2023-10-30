package com.se.netdiagram;

public class PrinterService {

    private PrinterService() {
    }

    public static void print(NetworkDiagram nd) {

        PrinterService.prettyprintHeader();

        for (Task task : nd.tasks()) {
            PrinterService.prettyprint(task);
        }

        for (Path path : nd.getCriticalPaths()) {
            for (Task task : path.tasks()) {
                System.out.printf("%5s ->", task.id());
            }
            System.out.println(" end");
        }
    }

    private static void prettyprint(Task task) {
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

    public static void prettyprintHeader() {
        System.out.printf("%s %5s %4s %4s %4s %4s %4s %6s\n", " ", "ID", "DUR", "ES", "EF", "LS", "LF", "SLACK");
    }

}
