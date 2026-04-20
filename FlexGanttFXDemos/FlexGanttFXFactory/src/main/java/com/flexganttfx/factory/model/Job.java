/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.factory.model;

import com.flexganttfx.model.activity.MutableCompletableActivityBase;

import java.time.Instant;

/**
 * Represents a manufacturing job assigned to a machine. The user object holds
 * the current {@link JobStatus} of the job. The inherited
 * {@code percentageComplete} field (0–100) tracks how much of the job has been
 * executed so far.
 */
public class Job extends MutableCompletableActivityBase<JobStatus> {

    public Job(String name, Instant startTime, Instant endTime, JobStatus status, double percentageComplete) {
        setName(name);
        setStartTime(startTime);
        setEndTime(endTime);
        setUserObject(status);
        setPercentageComplete(percentageComplete);
    }

    public JobStatus getStatus() {
        return getUserObject();
    }

    public void setStatus(JobStatus status) {
        setUserObject(status);
    }
}
