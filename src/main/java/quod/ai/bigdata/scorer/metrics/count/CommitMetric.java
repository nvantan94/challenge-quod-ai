package quod.ai.bigdata.scorer.metrics.count;

import com.google.gson.JsonObject;
import quod.ai.bigdata.scorer.Measurable;
import quod.ai.bigdata.scorer.metrics.CountEventMetric;

public class CommitMetric extends CountEventMetric {
    @Override
    public boolean checkEvent(JsonObject event) {
        return event.get("type").getAsString().equals("PushEvent");
    }

    @Override
    public Measurable clone() {
        return new CommitMetric();
    }

    @Override
    public String csvTitle() {
        return "avg_num_commits,max_num_commits";
    }
}
