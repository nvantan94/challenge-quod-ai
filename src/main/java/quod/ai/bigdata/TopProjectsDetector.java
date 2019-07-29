package quod.ai.bigdata;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import quod.ai.bigdata.project.ProjectStatistic;
import quod.ai.bigdata.scorer.Measurable;

import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.util.List;

public class TopProjectsDetector {
    private static final int NUMBER_OF_COLLECTED_HOURS = 30 * 24;
    private ProjectStatistic projectStatistic;

    public TopProjectsDetector(List<Measurable> metrics) {
        projectStatistic = new ProjectStatistic(metrics);
    }

    public void detectTop100Projects() {
        if (collectProjectStatistic()) {
            projectStatistic.updateProjectHealth();
        }
    }

    private boolean collectProjectStatistic() {
        LocalDateTime atHour = LocalDateTime.now().minusDays(30).withHour(0);
        for (int i = 0; i < NUMBER_OF_COLLECTED_HOURS; i++) {
            try {
                collectProjectStatistic(atHour);
            } catch (Exception e) {
                System.out.println("Error during execute events at " + atHour.toString());
                e.printStackTrace();
                return false;
            }
            atHour.plusHours(1);
        }
        return true;
    }

    private void collectProjectStatistic(LocalDateTime atHour) throws Exception {
        JsonParser eventParser = new JsonParser();
        BufferedReader eventsReader = EventDownloader.openStreamToEvents(atHour);
        String line;
        while ((line = eventsReader.readLine()) != null) {
            JsonObject event = eventParser.parse(line).getAsJsonObject();
            projectStatistic.addEvent(event);
        }
    }
}
