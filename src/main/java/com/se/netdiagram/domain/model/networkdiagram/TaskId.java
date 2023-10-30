package com.se.netdiagram.domain.model.networkdiagram;

public class TaskId {
    private String id;

    public TaskId(String id) {
        if (id == null || id.isEmpty())
            throw new IllegalArgumentException("TaskId cannot be null or empty!");

        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof TaskId))
            return false;

        TaskId other = (TaskId) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
