package quod.ai.bigdata.scorer.metrics;

import com.google.gson.JsonObject;
import quod.ai.bigdata.scorer.Measurable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CommitMetric implements Measurable {
    private Map<Integer, Integer> dateToNoCommits;
    private int maxNoCommits;
    private int sumNoCommits;

    public CommitMetric() {
        dateToNoCommits = new HashMap<>();
        maxNoCommits = 0;
        sumNoCommits = 0;
    }

    @Override
    public void consumeEvent(JsonObject event, LocalDateTime atHour) {
        if (event.getAsJsonObject("payload").has("comment")) {
            int dayOfYear = atHour.getDayOfYear();
            int noCommits = 0;
            if (dateToNoCommits.containsKey(dayOfYear))
                noCommits = dateToNoCommits.get(dayOfYear);
            dateToNoCommits.put(dayOfYear, noCommits++);
        }
    }

    @Override
    public double calculateScore() {
        for (Integer noCommits : dateToNoCommits.values()) {
            if (noCommits > maxNoCommits)
                maxNoCommits = noCommits;
            sumNoCommits += noCommits;
        }
        return maxNoCommits == 0? 0 : (sumNoCommits/30.0) / maxNoCommits;
    }

    @Override
    public Measurable clone() {
        return new CommitMetric();
    }
}
