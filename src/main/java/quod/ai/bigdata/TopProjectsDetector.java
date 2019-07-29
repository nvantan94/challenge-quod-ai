package quod.ai.bigdata;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import quod.ai.bigdata.project.Project;
import quod.ai.bigdata.project.ProjectStatistic;
import quod.ai.bigdata.scorer.Measurable;
import quod.ai.bigdata.scorer.metrics.count.CommitMetric;

import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TopProjectsDetector {
    private static final int NUMBER_OF_COLLECTED_HOURS = 30 * 24;
    private ProjectStatistic projectStatistic;

    public TopProjectsDetector(List<Measurable> metrics) {
        projectStatistic = new ProjectStatistic(metrics);
    }

    public void detectTop100Projects() {
        if (collectProjectStatistic()) {
            projectStatistic.updateProjectHealth();
            Project[] top100Projects = searchTop100Projects();
        }
    }

    private boolean collectProjectStatistic() {
        LocalDateTime atHour = LocalDateTime.now().minusDays(30).withHour(0);
        for (int i = 0; i < NUMBER_OF_COLLECTED_HOURS; i++) {
            try {
                collectProjectStatistic(atHour);
                Thread.sleep((int)(Math.random() * 1000));
            } catch (Exception e) {
                System.out.println("Error during execute events at " + atHour.toString());
                e.printStackTrace();
                return false;
            }
            atHour = atHour.plusHours(1);
        }
        return true;
    }

    private void collectProjectStatistic(LocalDateTime atHour) throws Exception {
        System.out.println("Collecting events at: " + atHour.toString());
        JsonParser eventParser = new JsonParser();
        BufferedReader eventsReader = EventDownloader.openStreamToEvents(atHour);
        String line;
        while ((line = eventsReader.readLine()) != null) {
            JsonObject event = eventParser.parse(line).getAsJsonObject();
            projectStatistic.addEvent(event, atHour);
        }
    }

    private Project[] searchTop100Projects() {
        Project[] projects = getListOfProjects();
        int len = projects.length, toIndex = len < 100? len : 100;
        Arrays.sort(projects, 0, toIndex);
        for (int i = 100; i < len; i++) {
            if (projects[i].getHealthScore() > projects[0].getHealthScore()) {
                int insertionPoint = Arrays.binarySearch(projects, 0, toIndex, projects[i]);
                if (insertionPoint < 0)
                    insertionPoint = -(insertionPoint + 2);
                for (int j = 0; j < insertionPoint; j++)
                    projects[j] = projects[j+1];
                projects[insertionPoint] = projects[i];
            }
        }
        return Arrays.copyOfRange(projects, 0, toIndex);
    }

    private Project[] getListOfProjects() {
        Set<Project> projectSet = projectStatistic.getProjectToMetrics().keySet();
        Project[] projects = new Project[projectSet.size()];
        projectSet.toArray(projects);
        return projects;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        List<Measurable> metrics = new ArrayList<>();
        metrics.add(new CommitMetric());
        TopProjectsDetector topProjectsDetector = new TopProjectsDetector(metrics);
        topProjectsDetector.detectTop100Projects();
        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000.0);
    }
}
