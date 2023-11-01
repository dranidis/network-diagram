package com.se.netdiagram.application;

import com.se.netdiagram.domain.model.networkdiagram.Path;
import com.se.netdiagram.domain.model.networkdiagram.Task;

public interface NetworkDiagramPrinter {

    void printHeader(long projectEnd);

    void printTask(Task task, long projectEnd);

    void printCriticalPath(Path path);

}
