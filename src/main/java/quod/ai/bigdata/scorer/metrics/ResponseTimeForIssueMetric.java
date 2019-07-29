package quod.ai.bigdata.scorer.metrics;

import com.google.gson.JsonObject;
import quod.ai.bigdata.scorer.Measurable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResponseTimeForIssueMetric implements Measurable {
    private List<Long> responseTimes;
    private Long minResponseTime;
    private Double avgResponseTime;

    public ResponseTimeForIssueMetric() {
        responseTimes = new ArrayList<>();
    }

    @Override
    public void consumeEvent(JsonObject event, LocalDateTime atHour) {
        if (checkEvent(event)) {
            JsonObject payload = event.getAsJsonObject("payload");
            String issueCreatedAtStr = payload.getAsJsonObject("issue")
                    .get("created_at").getAsString();
            LocalDateTime issueCreatedAt = LocalDateTime.parse(
                    issueCreatedAtStr.substring(0, issueCreatedAtStr.length()-1));

            String commentCreatedAtStr = payload.getAsJsonObject("comment")
                    .get("created_at").getAsString();
            LocalDateTime commentCreatedAt = LocalDateTime.parse(
                    commentCreatedAtStr.substring(0, commentCreatedAtStr.length() - 1));

            responseTimes.add(
                    commentCreatedAt.toEpochSecond(ZoneOffset.UTC) - issueCreatedAt.toEpochSecond(ZoneOffset.UTC));
        }
    }

    private boolean checkEvent(JsonObject event) {
        JsonObject payload = event.getAsJsonObject("payload");
        if (payload.has("issue") && payload.has("comment")) {
            int noComments = payload.getAsJsonObject("issue").get("comments").getAsInt();
            return noComments == 0;
        }
        return false;
    }

    @Override
    public double calculateScore() {
        minResponseTime = Collections.min(responseTimes);
        Long sumResponseTime = responseTimes.stream().reduce(Long::sum).get();
        avgResponseTime = (double) sumResponseTime / responseTimes.size();
        return avgResponseTime / minResponseTime;
    }

    @Override
    public Measurable clone() {
        return new ResponseTimeForIssueMetric();
    }

    @Override
    public String csvTitle() {
        return "svg_response_time_for_issue,min_response_time_for_issue";
    }

    @Override
    public String csvContent() {
        return String.format("%.2f", avgResponseTime) + "," + minResponseTime;
    }
}
