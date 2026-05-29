/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.sprint.model;

import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.layout.ChartLayout;
import com.flexganttfx.model.layout.GanttLayout;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class SampleDataGenerator {

    private final TeamRoot teamRoot = new TeamRoot();
    private final EngineerFleet engineerFleet = new EngineerFleet();
    private final List<ActivityLink<?>> activityLinks = new ArrayList<>();

    private final Layer featuresLayer;
    private final Layer bugsLayer;
    private final Layer techDebtLayer;
    private final Layer milestonesLayer;
    private final Layer burnDownLayer;

    public SampleDataGenerator(Layer featuresLayer, Layer bugsLayer, Layer techDebtLayer,
                               Layer milestonesLayer, Layer burnDownLayer) {
        this.featuresLayer = featuresLayer;
        this.bugsLayer = bugsLayer;
        this.techDebtLayer = techDebtLayer;
        this.milestonesLayer = milestonesLayer;
        this.burnDownLayer = burnDownLayer;
        generate();
    }

    public TeamRoot getTeamRoot() {
        return teamRoot;
    }

    public EngineerFleet getEngineerFleet() {
        return engineerFleet;
    }

    public List<ActivityLink<?>> getActivityLinks() {
        return activityLinks;
    }

    private static Instant day(int offsetDays) {
        return LocalDate.now().minusDays(-offsetDays).atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    private void generate() {
        // Sprint windows (each 14 days, starting from 56 days ago)
        int[] sprintStarts = {-56, -42, -28, -14};
        double[] sprintCompletions = {1.0, 0.85, 0.45, 0.10};

        // --- Engineers for team-load chart ---
        String[] engineerNames = {"Alice", "Bob", "Carol", "Dave", "Eve"};
        EngineerRow[] engineers = new EngineerRow[engineerNames.length];
        for (int i = 0; i < engineerNames.length; i++) {
            engineers[i] = new EngineerRow(engineerNames[i]);
            engineerFleet.getChildren().add(engineers[i]);
        }

        // --- Teams ---
        String[][] teamEpics = {
            {"User Auth Epic", "Dashboard Epic", "Notifications Epic"},
            {"API Gateway Epic", "Data Pipeline Epic"},
            {"Test Automation Epic", "Performance Epic", "Regression Epic"}
        };
        String[] teamNames = {"Frontend Team", "Backend Team", "QA Team"};

        // Keep track of a few stories for linking
        List<StoryRow> linkCandidates = new ArrayList<>();
        List<UserStory> linkStories = new ArrayList<>();

        int engineerIdx = 0;

        for (int t = 0; t < teamNames.length; t++) {
            TeamRow teamRow = new TeamRow(teamNames[t]);
            teamRow.setExpanded(true);
            teamRoot.getChildren().add(teamRow);

            for (int e = 0; e < teamEpics[t].length; e++) {
                EpicRow epicRow = new EpicRow(teamEpics[t][e]);
                epicRow.setExpanded(true);
                epicRow.setLayout(new ChartLayout());
                teamRow.getChildren().add(epicRow);

                double totalPoints = 0;

                for (int s = 0; s < 4; s++) {
                    int sprintStart = sprintStarts[s];
                    double completionBase = sprintCompletions[s];
                    int sprintDays = 14;

                    int storyCount = 3 + (s % 2);
                    for (int i = 0; i < storyCount; i++) {
                        int storyOffsetDays = i * 2;
                        Instant start = day(sprintStart + storyOffsetDays);
                        Instant end = day(sprintStart + storyOffsetDays + (3 + (i % 3)));
                        double pct = Math.min(100.0, completionBase * (80 + i * 5));
                        int points = 3 + (i % 5);
                        totalPoints += points;

                        String assignee = engineerNames[(engineerIdx + i) % engineerNames.length];
                        String storyName = "S" + (s + 1) + "-" + teamEpics[t][e].charAt(0) + (i + 1);

                        UserStory story = new UserStory(storyName, start, end, assignee, points, pct);

                        StoryRow storyRow = new StoryRow(storyName + ": " + assignee);
                        storyRow.setLayout(new GanttLayout());
                        epicRow.getChildren().add(storyRow);
                        storyRow.addActivity(featuresLayer, story);

                        // Add a bug on some rows
                        if (i == 1 && s < 3) {
                            Instant bugStart = start.plus(1, ChronoUnit.DAYS);
                            Instant bugEnd = bugStart.plus(1, ChronoUnit.DAYS);
                            storyRow.addActivity(bugsLayer, new BugActivity("Bug-" + storyName, bugStart, bugEnd, assignee));
                        }

                        // Add tech-debt on some rows
                        if (i == 2 && s % 2 == 0) {
                            Instant tdStart = start.plus(2, ChronoUnit.DAYS);
                            Instant tdEnd = tdStart.plus(2, ChronoUnit.DAYS);
                            storyRow.addActivity(techDebtLayer, new TechDebtActivity("TD-" + storyName, tdStart, tdEnd, assignee));
                        }

                        // Collect candidates for activity links (first sprint, first team, first epic)
                        if (t == 0 && e == 0 && s == 0 && linkCandidates.size() < 5) {
                            linkCandidates.add(storyRow);
                            linkStories.add(story);
                        }

                        // Add tasks on engineer rows
                        EngineerRow engineer = engineers[(engineerIdx + i) % engineerNames.length];
                        Instant taskStart = start.plus(1, ChronoUnit.HOURS);
                        Instant taskEnd = taskStart.plus(2, ChronoUnit.DAYS);
                        engineer.addActivity(featuresLayer, new TaskActivity(storyName + "-task", taskStart, taskEnd, assignee));
                    }

                    // Milestone at sprint end
                    if (e == 0) {
                        Instant milestoneTime = day(sprintStart + sprintDays);
                        StoryRow msRow = new StoryRow("Sprint " + (s + 1) + " Review");
                        msRow.setLayout(new GanttLayout());
                        epicRow.getChildren().add(msRow);
                        msRow.addActivity(milestonesLayer, new MilestoneActivity("Sprint " + (s + 1) + " Review", milestoneTime));
                    }
                }

                // BurnDown activity spanning the full 8-week range
                Instant bdStart = day(sprintStarts[0]);
                Instant bdEnd = day(sprintStarts[3] + 14);
                epicRow.addActivity(burnDownLayer, new BurnDownActivity(teamEpics[t][e] + " Burn-Down", bdStart, bdEnd, totalPoints));
            }

            engineerIdx++;
        }

        // --- Activity Links using all 4 LinkType values ---
        if (linkCandidates.size() >= 4 && linkStories.size() >= 4) {
            // FINISH_TO_START (END_TO_START): story 0 must finish before story 1 starts
            activityLinks.add(new ActivityLink<>(
                new ActivityRef<>(linkCandidates.get(0), featuresLayer, linkStories.get(0)),
                new ActivityRef<>(linkCandidates.get(1), featuresLayer, linkStories.get(1)),
                ActivityLink.LinkType.END_TO_START));

            // START_TO_START: story 1 and story 2 start together
            activityLinks.add(new ActivityLink<>(
                new ActivityRef<>(linkCandidates.get(1), featuresLayer, linkStories.get(1)),
                new ActivityRef<>(linkCandidates.get(2), featuresLayer, linkStories.get(2)),
                ActivityLink.LinkType.START_TO_START));

            // FINISH_TO_FINISH (END_TO_END): story 2 and story 3 finish together
            activityLinks.add(new ActivityLink<>(
                new ActivityRef<>(linkCandidates.get(2), featuresLayer, linkStories.get(2)),
                new ActivityRef<>(linkCandidates.get(3), featuresLayer, linkStories.get(3)),
                ActivityLink.LinkType.END_TO_END));

            // START_TO_FINISH (START_TO_END): story 0 must start before story 3 can finish
            activityLinks.add(new ActivityLink<>(
                new ActivityRef<>(linkCandidates.get(0), featuresLayer, linkStories.get(0)),
                new ActivityRef<>(linkCandidates.get(3), featuresLayer, linkStories.get(3)),
                ActivityLink.LinkType.START_TO_END));

            if (linkCandidates.size() >= 5 && linkStories.size() >= 5) {
                // Additional END_TO_START between story 3 and story 4
                activityLinks.add(new ActivityLink<>(
                    new ActivityRef<>(linkCandidates.get(3), featuresLayer, linkStories.get(3)),
                    new ActivityRef<>(linkCandidates.get(4), featuresLayer, linkStories.get(4)),
                    ActivityLink.LinkType.END_TO_START));
            }
        }
    }
}
