package com.se.netdiagram.application;

import com.se.netdiagram.domain.model.networkdiagram.Path;
import com.se.netdiagram.domain.model.networkdiagram.Task;

public interface NetworkDiagramPrinter {

    void printHeader();

    void printTask(Task task);

    void printCriticalPath(Path path);

}
