package quod.ai.bigdata.scorer.metrics;

import com.google.gson.JsonObject;
import quod.ai.bigdata.scorer.Measurable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public abstract class CountEventMetric implements Measurable {
    private Map<Integer, Integer> dateToNoEvents;
    private int maxNoEvents;
    private int sumNoEvents;

    public CountEventMetric() {
        dateToNoEvents = new HashMap<>();
        maxNoEvents = 0;
        sumNoEvents = 0;
    }

    @Override
    public void consumeEvent(JsonObject event, LocalDateTime atHour) {
        if (checkEvent(event)) {
            int dayOfYear = atHour.getDayOfYear();
            int noCommits = 0;
            if (dateToNoEvents.containsKey(dayOfYear))
                noCommits = dateToNoEvents.get(dayOfYear);
            dateToNoEvents.put(dayOfYear, noCommits++);
        }
    }

    public abstract boolean checkEvent(JsonObject event);

    @Override
    public double calculateScore() {
        for (Integer noCommits : dateToNoEvents.values()) {
            if (noCommits > maxNoEvents)
                maxNoEvents = noCommits;
            sumNoEvents += noCommits;
        }
        return maxNoEvents == 0? 0 : (sumNoEvents /30.0) / maxNoEvents;
    }

    @Override
    public Measurable clone() {
        return null;
    }
}
