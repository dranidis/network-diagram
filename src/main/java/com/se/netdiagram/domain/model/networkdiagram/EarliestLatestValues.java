package com.se.netdiagram.domain.model.networkdiagram;

import java.util.List;
import java.util.OptionalLong;

public class EarliestLatestValues {
    private OptionalLong earliestStart;
    private OptionalLong earliestFinish;
    private OptionalLong latestStart;
    private OptionalLong latestFinish;
    private OptionalLong slack;

    public EarliestLatestValues() {
        setEarliestAndLatestValuesToEmpty();
    }

    private EarliestLatestValues(EarliestLatestValues earliestLatestValues) {
        this.earliestStart = earliestLatestValues.earliestStart;
        this.earliestFinish = earliestLatestValues.earliestFinish;
        this.latestStart = earliestLatestValues.latestStart;
        this.latestFinish = earliestLatestValues.latestFinish;
        this.slack = earliestLatestValues.slack;
    }

    private void setEarliestAndLatestValuesToEmpty() {
        earliestStart = OptionalLong.empty();
        earliestFinish = OptionalLong.empty();
        latestStart = OptionalLong.empty();
        latestFinish = OptionalLong.empty();
    }

    public OptionalLong slack() {
        return slack;
    }

    public OptionalLong earliestFinish() {
        return earliestFinish;
    }

    public OptionalLong earliestStart() {
        return earliestStart;
    }

    public OptionalLong latestStart() {
        return latestStart;
    }

    public OptionalLong latestFinish() {
        return latestFinish;
    }

    private void calculateEarliestValues(List<Dependency> predecessors, Duration duration) {
        this.earliestStart = OptionalLong.of(0);
        for (Dependency predDependency : predecessors) {
            Task predTask = predDependency.task();

            long es = this.earliestStart.getAsLong();
            long pef = predTask.earliestLatestValues().earliestFinish.getAsLong();
            long pes = predTask.earliestLatestValues().earliestStart.getAsLong();

            switch (predDependency.type()) {
            case FS:
                es = Math.max(es, pef + predDependency.lag());
                break;
            case SS:
                es = Math.max(es, pes + predDependency.lag());
                break;
            case FF:
                es = Math.max(es, pef - duration.value() + predDependency.lag());
                break;
            case SF:
                es = Math.max(es, pes - duration.value() + predDependency.lag());
            }
            this.earliestStart = OptionalLong.of(es);
        }
        this.earliestFinish = OptionalLong.of(this.earliestStart.getAsLong() + duration.value());

        assert this.earliestStart.getAsLong() >= 0;
    }

    private void calculateLatestValuesAndSlack(List<Dependency> successors, Duration duration, long projectEnd) {
        this.latestFinish = OptionalLong.of(projectEnd);
        for (Dependency succDependency : successors) {
            Task succTask = succDependency.task();

            long lf = this.latestFinish.getAsLong();
            long sls = succTask.earliestLatestValues().latestStart.getAsLong();
            long slf = succTask.earliestLatestValues().latestFinish.getAsLong();

            switch (succDependency.type()) {
            case FS:
                lf = Math.min(lf, sls - succDependency.lag());
                break;
            case SS:
                lf = Math.min(lf, sls + duration.value() - succDependency.lag());
                break;
            case FF:
                lf = Math.min(lf, slf - succDependency.lag());
                break;
            case SF:
                lf = Math.min(lf, slf + duration.value() - succDependency.lag());
            }
            this.latestFinish = OptionalLong.of(lf);
        }
        this.latestStart = OptionalLong.of(this.latestFinish.getAsLong() - duration.value());
        this.slack = OptionalLong.of(this.latestFinish.getAsLong() - this.earliestFinish.getAsLong());
    }

    public EarliestLatestValues calcEarliestValues(List<Dependency> predecessors, Duration duration) {
        EarliestLatestValues earliestLatestValues = new EarliestLatestValues(this);
        earliestLatestValues.calculateEarliestValues(predecessors, duration);
        return earliestLatestValues;
    }

    public EarliestLatestValues calcLatestValuesAndSlack(List<Dependency> successors, Duration duration,
            long projectEnd) {
        EarliestLatestValues earliestLatestValues = new EarliestLatestValues(this);
        earliestLatestValues.calculateLatestValuesAndSlack(successors, duration, projectEnd);
        return earliestLatestValues;
    }
}
