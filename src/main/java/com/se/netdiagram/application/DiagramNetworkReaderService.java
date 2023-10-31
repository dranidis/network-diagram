package com.se.netdiagram.application;

import java.util.List;

import com.se.netdiagram.domain.model.exceptions.DuplicateTaskKeyException;
import com.se.netdiagram.domain.model.exceptions.KeyNotFoundException;
import com.se.netdiagram.domain.model.networkdiagram.Dependency;
import com.se.netdiagram.domain.model.networkdiagram.NetworkDiagram;

public class DiagramNetworkReaderService {

    private DiagramNetworkReaderService() {
    }

    public static NetworkDiagram readNetworkDiagramWith(TaskDataReader taskDataReader)
            throws DuplicateTaskKeyException, KeyNotFoundException {
        NetworkDiagram nd = new NetworkDiagram();

        List<TaskData> taskJSONList = taskDataReader.read();

        processTaskList(nd, taskJSONList);

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
            for (DependencyData predJSON : taskJSON.getPredIds()) {
                nd.addPredecessorToTask(taskJSON.getId(), predJSON.getId(), predJSON.getType(), predJSON.getLag());
            }
        }
    }
}
