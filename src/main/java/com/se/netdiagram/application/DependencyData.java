package com.se.netdiagram.application;

public class DependencyData {
    private final String id;
    private final String type;

    public DependencyData() {
        this("", "");
    }

    public DependencyData(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

}
