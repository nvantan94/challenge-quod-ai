package quod.ai.bigdata.scorer;

import com.google.gson.JsonObject;

import java.time.LocalDateTime;

public interface Measurable {
    void consumeEvent(JsonObject event, LocalDateTime atHour);
    double calculateScore();
    Measurable clone();
    String csvTitle();
    String csvContent();
}
