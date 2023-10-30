package com.se.netdiagram.domain.model.networkdiagram;

public class Dependency {
    private String id;
    private DependencyType type;

    public Dependency(String id, DependencyType type) {
        this.id = id;
        this.type = type;
    }

}
