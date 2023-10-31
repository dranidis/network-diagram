package com.se.netdiagram.application;

public class DependencyData {
    private final String id;
    private final String type;
    private final int lag;

    public DependencyData() {
        this("", "", 0);
    }

    public DependencyData(String id) {
        this(id, "FS", 0);
    }

    public DependencyData(String id, String type) {
        this(id, type, 0);
    }

    public DependencyData(String id, String type, int lag) {
        this.id = id;
        this.type = type;
        this.lag = lag;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public int getLag() {
        return lag;
    }

}
