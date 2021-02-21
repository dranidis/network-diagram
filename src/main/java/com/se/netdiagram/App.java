package com.se.netdiagram;

/**
 * Reads a list of tasks from a json file, calculates ES, EF, LS, LF, Slack and finds critical paths.
 *
 */
public class App {
    public static void main(String[] args) {
        String jsonFile = "examples/tasks.json";
        if(args.length > 0) 
            jsonFile = args[0];

        NetworkDiagram nd = new NetworkDiagram();
        nd.readJsonFile(jsonFile);
        nd.process();
        nd.print();
    }

}
