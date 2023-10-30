package com.se.netdiagram.application;

import java.util.List;

import com.se.netdiagram.domain.model.NetworkDiagram;
import com.se.netdiagram.domain.model.exceptions.DuplicateTaskKeyException;
import com.se.netdiagram.domain.model.exceptions.KeyNotFoundException;

public class DiagramNetworkReaderService {

    private DiagramNetworkReaderService() {
    }

    public static NetworkDiagram readNetworkDiagramWith(TaskDataReader taskDataReader) {
        NetworkDiagram nd = new NetworkDiagram();

        List<TaskData> taskJSONList = taskDataReader.read();

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
    protected static void processTaskList(NetworkDiagram nd, List<TaskData> taskList)
            throws DuplicateTaskKeyException, KeyNotFoundException {
        populateTasksFrom(nd, taskList);
        addPredecessorsToTasksFrom(nd, taskList);
    }

    private static void populateTasksFrom(NetworkDiagram nd, List<TaskData> taskList) throws DuplicateTaskKeyException {
        for (TaskData taskJSON : taskList) {
            nd.addTask(taskJSON.getId(), taskJSON.getDuration());
        }
    }

    private static void addPredecessorsToTasksFrom(NetworkDiagram nd, List<TaskData> taskList)
            throws KeyNotFoundException {
        for (TaskData taskJSON : taskList) {
            nd.addPredecessorsToTask(taskJSON.getId(), taskJSON.getPred());
        }
    }
}
