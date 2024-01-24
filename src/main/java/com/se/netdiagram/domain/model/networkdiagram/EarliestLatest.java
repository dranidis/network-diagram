package com.se.netdiagram.domain.model.networkdiagram;

import java.util.List;
import java.util.NoSuchElementException;

import com.se.netdiagram.domain.model.networkdiagram.date.Date;
import com.se.netdiagram.domain.model.networkdiagram.date.Duration;

public class EarliestLatest {
    private Date earliestStart;
    private Date earliestFinish;
    private Date latestStart;
    private Date latestFinish;
    private Duration slack;

    public EarliestLatest() {
        setEarliestAndLatestToEmpty();
    }

    public EarliestLatest(EarliestLatest earliestLatest) {
        this.earliestStart = earliestLatest.earliestStart;
        this.earliestFinish = earliestLatest.earliestFinish;
        this.latestStart = earliestLatest.latestStart;
        this.latestFinish = earliestLatest.latestFinish;
        this.slack = earliestLatest.slack;
    }

    private void setEarliestAndLatestToEmpty() {
        earliestStart = Date.empty();
        earliestFinish = Date.empty();
        latestStart = Date.empty();
        latestFinish = Date.empty();
    }

    public Duration slack() {
        return slack;
    }

    public Date earliestFinish() {
        return earliestFinish;
    }

    public Date earliestStart() {
        return earliestStart;
    }

    public Date latestStart() {
        return latestStart;
    }

    public Date latestFinish() {
        return latestFinish;
    }

    private void calculateEarliestValues(List<Dependency> predecessors, Duration duration) {
        this.earliestStart = Date.ofLong(0);
        for (Dependency predDependency : predecessors) {
            Task predTask = predDependency.task();

            long es = this.earliestStart.getAsLong();
            long pef = predTask.earliestLatest().earliestFinish.getAsLong();
            long pes = predTask.earliestLatest().earliestStart.getAsLong();

            switch (predDependency.type()) {
            case FS:
                es = Math.max(es, pef + predDependency.lag().value());
                break;
            case SS:
                es = Math.max(es, pes + predDependency.lag().value());
                break;
            case FF:
                es = Math.max(es, pef - duration.value() + predDependency.lag().value());
                break;
            case SF:
                es = Math.max(es, pes - duration.value() + predDependency.lag().value());
            }
            this.earliestStart = Date.ofLong(es);
        }
        this.earliestFinish = Date.ofLong(this.earliestStart.getAsLong() + duration.value());

        assert this.earliestStart.getAsLong() >= 0;
    }

    private void calculateLatestValuesAndSlack(List<Dependency> successors, Duration duration, long projectEnd) {
        try {
            this.latestFinish = Date.ofLong(projectEnd);
            for (Dependency successorDependency : successors) {
                Task successorTask = successorDependency.task();

                long lf = this.latestFinish.getAsLong();
                long sls = successorTask.earliestLatest().latestStart.getAsLong();
                long slf = successorTask.earliestLatest().latestFinish.getAsLong();

                switch (successorDependency.type()) {
                case FS:
                    lf = Math.min(lf, sls - successorDependency.lag().value());
                    break;
                case SS:
                    lf = Math.min(lf, sls + duration.value() - successorDependency.lag().value());
                    break;
                case FF:
                    lf = Math.min(lf, slf - successorDependency.lag().value());
                    break;
                case SF:
                    lf = Math.min(lf, slf + duration.value() - successorDependency.lag().value());
                }
                this.latestFinish = Date.ofLong(lf);
            }
            this.latestStart = Date.ofLong(this.latestFinish.getAsLong() - duration.value());
            this.slack = Duration.difference(this.latestFinish.getAsLong(), this.earliestFinish.getAsLong());
        } catch (NoSuchElementException e) {
            throw new IllegalStateException("Earliest values have not been calculated yet!" + e.getMessage());
        }
    }

    public EarliestLatest calculateEarliest(List<Dependency> predecessors, Duration duration) {
        EarliestLatest earliestLatest = new EarliestLatest(this);
        earliestLatest.calculateEarliestValues(predecessors, duration);
        return earliestLatest;
    }

    public EarliestLatest calculateLatestAndSlack(List<Dependency> successors, Duration duration,
            long projectEnd) {
        EarliestLatest earliestLatest = new EarliestLatest(this);
        earliestLatest.calculateLatestValuesAndSlack(successors, duration, projectEnd);
        return earliestLatest;
    }
}
