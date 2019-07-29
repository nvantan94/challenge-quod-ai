package quod.ai.bigdata.project;

import com.google.gson.JsonObject;
import quod.ai.bigdata.scorer.Measurable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectStatistic {
    private List<Measurable> metrics;
    private Map<Project, List<Measurable>> projectToMetrics;

    public ProjectStatistic(List<Measurable> metrics) {
        this.metrics = metrics;
        projectToMetrics = new HashMap<>();
    }

    public void addEvent(JsonObject event) {
        Project project = extractProject(event);
        List<Measurable> projMetrics;
        if (projectToMetrics.containsKey(project))
            projMetrics = projectToMetrics.get(project);
        else {
            projMetrics = new ArrayList<>();
            metrics.forEach( metric -> projMetrics.add(metric.clone()));
            projectToMetrics.put(project, projMetrics);
        }
        projMetrics.forEach(projMetric -> projMetric.consumeEvent(event));
    }

    private Project extractProject(JsonObject event) {
        JsonObject repo = event.getAsJsonObject("repo");
        long id = repo.get("id").getAsLong();
        String url = repo.get("url").getAsString();
        String name = repo.get("name").getAsString();
        return new Project(id, url, name);
    }

    public void updateProjectHealth() {
        projectToMetrics.entrySet().forEach(entry -> {
            double healthScore = entry.getValue().stream()
                    .map(metric -> metric.calculateScore())
                    .reduce(0.0, Double::sum);
            entry.getKey().setHealthScore(healthScore);
        });
    }

    public Map<Project, List<Measurable>> getProjectToMetrics() {
        return projectToMetrics;
    }
}
