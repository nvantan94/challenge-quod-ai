package quod.ai.bigdata.scorer.metrics.count;

import com.google.gson.JsonObject;
import quod.ai.bigdata.scorer.Measurable;
import quod.ai.bigdata.scorer.metrics.CountEventMetric;

public class ReleaseMetric extends CountEventMetric {
    @Override
    public boolean checkEvent(JsonObject event) {
        JsonObject payload = event.getAsJsonObject("payload");
        return payload.has("release")
                && payload.get("action").getAsString().equals("published");
    }

    @Override
    public Measurable clone() {
        return new ReleaseMetric();
    }

    @Override
    public String csvTitle() {
        return "avg_num_releases,max_num_releases";
    }
}
