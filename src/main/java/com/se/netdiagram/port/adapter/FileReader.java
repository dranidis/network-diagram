package com.se.netdiagram.port.adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.se.netdiagram.application.TaskData;

public class FileReader {

    public List<TaskData> readJsonFile(String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        List<TaskData> taskJSONList = new ArrayList<>();

        try {
            taskJSONList = mapper.readValue(new File(fileName), new TypeReference<List<TaskData>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return taskJSONList;
    }
}
