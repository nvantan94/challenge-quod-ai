package quod.ai.bigdata.project;

import com.google.gson.JsonObject;
import quod.ai.bigdata.scorer.Measurable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectStatistic {
    private List<Measurable> metrics;
    private Map<Project, List<Measurable>> projectToMetrics;
    private Map<Long, Project> idToProject;

    public ProjectStatistic(List<Measurable> metrics) {
        this.metrics = metrics;
        projectToMetrics = new HashMap<>();
        idToProject = new HashMap<>();
    }

    public void addEvent(JsonObject event, LocalDateTime atHour) {
        Project project = extractProject(event);
        List<Measurable> projMetrics;
        if (projectToMetrics.containsKey(project))
            projMetrics = projectToMetrics.get(project);
        else {
            projMetrics = new ArrayList<>();
            metrics.forEach( metric -> projMetrics.add(metric.clone()));
            projectToMetrics.put(project, projMetrics);
        }
        projMetrics.forEach(projMetric -> projMetric.consumeEvent(event, atHour));
    }

    private Project extractProject(JsonObject event) {
        JsonObject repo = event.getAsJsonObject("repo");
        long id = repo.get("id").getAsLong();
        if (!idToProject.containsKey(id)) {
            String name = repo.get("name").getAsString();
            String org = event.getAsJsonObject("org").get("login").getAsString();
            Project project = new Project(id, org, name);
            idToProject.put(id, project);
        }
        return idToProject.get(id);
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
