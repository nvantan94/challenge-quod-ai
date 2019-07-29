package quod.ai.bigdata.scorer;

import com.google.gson.JsonObject;

public interface Measurable {
    void consumeEvent(JsonObject event);
    double calculateScore();
}
