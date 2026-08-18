/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
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
