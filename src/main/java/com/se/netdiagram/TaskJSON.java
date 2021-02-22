package com.se.netdiagram;

import java.util.List;

public class TaskJSON {
    public TaskJSON(String string) {
        id = string;
    }
    
    public TaskJSON(String string, int i, List<String> asList) {
        id = string;
        duration = i;
        pred = asList;
	}

	public String id;
    public int duration;
    public List<String> pred;
}