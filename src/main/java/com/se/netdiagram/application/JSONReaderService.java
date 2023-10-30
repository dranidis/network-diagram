package com.se.netdiagram.application;

import java.util.List;

import com.se.netdiagram.FileReader;
import com.se.netdiagram.domain.model.NetworkDiagram;
import com.se.netdiagram.domain.model.exceptions.DuplicateTaskKeyException;
import com.se.netdiagram.domain.model.exceptions.KeyNotFoundException;

public class JSONReaderService {

    private JSONReaderService() {
    }

    public static NetworkDiagram readNetworkDiagramFromJSONFile(String jsonFileName) {
        NetworkDiagram nd = new NetworkDiagram();
        FileReader fileReader = new FileReader();
        List<TaskData> taskJSONList = fileReader.readJsonFile(jsonFileName);

        try {
            processTaskList(nd, taskJSONList);
        } catch (DuplicateTaskKeyException | KeyNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        nd.forwardAndBackWard();
        return nd;
    }

    /**
     * @param taskList
     * @throws DuplicateTaskKeyException
     * @throws KeyNotFoundException
     */
    public static void processTaskList(NetworkDiagram nd, List<TaskData> taskList)
            throws DuplicateTaskKeyException, KeyNotFoundException {
        populateTasksFrom(nd, taskList);
        addPredecessorsToTasksFrom(nd, taskList);
    }

    private static void populateTasksFrom(NetworkDiagram nd, List<TaskData> taskList) throws DuplicateTaskKeyException {
        for (TaskData taskJSON : taskList) {
            nd.addTask(taskJSON.id, taskJSON.duration);
        }
    }

    private static void addPredecessorsToTasksFrom(NetworkDiagram nd, List<TaskData> taskList)
            throws KeyNotFoundException {
        for (TaskData taskJSON : taskList) {
            nd.addPredecessorsToTask(taskJSON.id, taskJSON.pred);
        }
    }
}
