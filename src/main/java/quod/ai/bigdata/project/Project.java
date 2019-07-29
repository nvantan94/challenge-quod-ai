package quod.ai.bigdata.project;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Project implements Comparable<Project> {
    private long id;
    private String name;
    private double healthScore;

    public Project(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (!(other instanceof Project))
            return false;

        Project project = (Project) other;
        return project.id == id &&
                project.name.equals(name);
    }

    @Override
    public int compareTo(Project other) {
        double diff = this.healthScore - other.healthScore;
        if (diff == 0)
            return 0;
        if (diff > 0)
            return 1;
        return -1;
    }

    public String csvContent() {
        return name + "," + String.format("%.2f", healthScore);
    }
}
