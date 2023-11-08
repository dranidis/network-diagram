package com.se.netdiagram.port.adapter;

import com.se.netdiagram.application.TaskDataReader;
import com.se.netdiagram.application.TaskDataList;

public class JSONFileTaskDataReader implements TaskDataReader {

    private final String jsonFileName;

    public JSONFileTaskDataReader(String jsonFileName) {
        this.jsonFileName = jsonFileName;
    }

    @Override
    public TaskDataList read() {
        FileReader fileReader = new FileReader();
        return fileReader.readJsonFile(jsonFileName);
    }

}
