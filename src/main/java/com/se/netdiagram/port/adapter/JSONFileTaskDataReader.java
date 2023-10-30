package com.se.netdiagram.port.adapter;

import java.util.List;

import com.se.netdiagram.FileReader;
import com.se.netdiagram.application.TaskData;
import com.se.netdiagram.application.TaskDataReader;

public class JSONFileTaskDataReader implements TaskDataReader {

    private final String jsonFileName;

    public JSONFileTaskDataReader(String jsonFileName) {
        this.jsonFileName = jsonFileName;
    }

    @Override
    public List<TaskData> read() {
        FileReader fileReader = new FileReader();
        return fileReader.readJsonFile(jsonFileName);
    }

}
