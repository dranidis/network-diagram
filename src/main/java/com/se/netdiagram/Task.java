package com.se.netdiagram;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;

public class Task {
    public String id;
    public int duration;
    public List<Task> pred = new ArrayList<>();
    public OptionalLong earliestStart;
    public OptionalLong earliestFinish;
    public OptionalLong latestStart;
    public OptionalLong latestFinish;
    public OptionalLong slack;
    public List<Task> succ = new ArrayList<>();
}
