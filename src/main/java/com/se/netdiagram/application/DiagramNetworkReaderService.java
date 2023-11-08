package com.se.netdiagram.application;

import com.se.netdiagram.domain.model.exceptions.DuplicateTaskKeyException;
import com.se.netdiagram.domain.model.exceptions.KeyNotFoundException;
import com.se.netdiagram.domain.model.networkdiagram.NetworkDiagram;

public class DiagramNetworkReaderService {

    private DiagramNetworkReaderService() {
    }

    public static NetworkDiagram readNetworkDiagramWith(TaskDataReader taskDataReader)
            throws DuplicateTaskKeyException, KeyNotFoundException {
        NetworkDiagram nd = new NetworkDiagram();

        TaskDataList taskJSONList = taskDataReader.read();

        processTaskList(nd, taskJSONList);

        return nd;
    }

    /**
     * @param taskList
     * @throws DuplicateTaskKeyException
     * @throws KeyNotFoundException
     */
    protected static void processTaskList(NetworkDiagram nd, TaskDataList taskList)
            throws DuplicateTaskKeyException, KeyNotFoundException {
        populateTasksFrom(nd, taskList);
        addPredecessorsToTasksFrom(nd, taskList);
    }

    private static void populateTasksFrom(NetworkDiagram nd, TaskDataList taskList) throws DuplicateTaskKeyException {
        for (TaskData taskJSON : taskList.tasks()) {
            nd.addTask(taskJSON.getId(), taskJSON.getDuration());
        }
    }

    private static void addPredecessorsToTasksFrom(NetworkDiagram nd, TaskDataList taskList)
            throws KeyNotFoundException {
        for (TaskData taskJSON : taskList.tasks()) {
            for (DependencyData predJSON : taskJSON.getPredIds()) {
                nd.addPredecessorToTask(taskJSON.getId(), predJSON.getId(), predJSON.getType(), predJSON.getLag());
            }
        }
    }
}
