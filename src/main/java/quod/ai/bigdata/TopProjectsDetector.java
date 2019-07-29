package quod.ai.bigdata;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quod.ai.bigdata.project.Project;
import quod.ai.bigdata.project.ProjectStatistic;
import quod.ai.bigdata.scorer.Measurable;
import quod.ai.bigdata.scorer.metrics.ResponseTimeForIssueMetric;
import quod.ai.bigdata.scorer.metrics.count.CommitMetric;
import quod.ai.bigdata.scorer.metrics.count.ReleaseMetric;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TopProjectsDetector {
    private static final Logger LOG = LoggerFactory.getLogger(TopProjectsDetector.class);

    private static final int NUMBER_OF_COLLECTED_HOURS = 30 * 24;

    private List<Measurable> metrics;
    private ProjectStatistic projectStatistic;

    public TopProjectsDetector(List<Measurable> metrics) {
        this.metrics = metrics;
        projectStatistic = new ProjectStatistic(metrics);
    }

    public void detectTop100Projects(String csvFilePath) {
        if (collectProjectStatistic()) {
            projectStatistic.updateProjectHealth();
            Project[] top100Projects = searchTop100Projects();
            writeToCSV(top100Projects, csvFilePath);
        }
    }

    private boolean collectProjectStatistic() {
        LocalDateTime atHour = LocalDateTime.now().minusDays(30).withHour(0);
        String lastHourStr = getHourPresentation(LocalDateTime.now().withHour(0));
        for (int i = 0; i < NUMBER_OF_COLLECTED_HOURS; i++) {
            try {
                LOG.info("Collecting events at " + getHourPresentation(atHour) + " / last hour: " + lastHourStr);
                collectProjectStatistic(atHour);
                Thread.sleep((int)(Math.random() * 1000));
            } catch (Exception e) {
                LOG.error("Error during execute events at " + getHourPresentation(atHour));
                LOG.error("", e);
                return false;
            }
            atHour = atHour.plusHours(1);
        }
        return true;
    }

    private void collectProjectStatistic(LocalDateTime atHour) throws Exception {
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

    private String getHourPresentation(LocalDateTime atHour) {
        return String.format("%4d-%02d-%02d-%02d:00",
                atHour.getYear(), atHour.getMonthValue(), atHour.getDayOfMonth(), atHour.getHour(), atHour.getMinute());
    }

    private void writeToCSV(Project[] top100Projects, String filePath) {
        try {
            String fileName = filePath + "//top100Projects.csv";
            BufferedWriter csvWriter = new BufferedWriter(new FileWriter(new File(fileName)));
            csvWriter.write(buildCSVTitle());
            for (int i = top100Projects.length - 1; i >= 0; i--)
                csvWriter.write(buildCSVRow(top100Projects[i]));
            csvWriter.close();
        } catch (IOException e) {
            LOG.error("", e);
        }
    }

    private String buildCSVTitle() {
        StringBuilder titleSb = new StringBuilder();
        titleSb.append("repo_name,health_score");
        for (Measurable metric : metrics)
            titleSb.append("," + metric.csvTitle());
        return titleSb.append("\n").toString();
    }

    private String buildCSVRow(Project project) {
        StringBuilder rowSb = new StringBuilder();
        rowSb.append(project.csvContent());
        List<Measurable> projMetrics = projectStatistic.getProjectToMetrics().get(project);
        for (Measurable projMetric : projMetrics)
            rowSb.append("," + projMetric.csvContent());
        return rowSb.append("\n").toString();
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            LOG.error("Specific directory for csv file");
            return;
        }
        List<Measurable> metrics = new ArrayList<>();
        metrics.add(new ResponseTimeForIssueMetric());
        metrics.add(new CommitMetric());
        metrics.add(new ReleaseMetric());
        TopProjectsDetector topProjectsDetector = new TopProjectsDetector(metrics);
        topProjectsDetector.detectTop100Projects(args[0]);
    }
}
