package quod.ai.bigdata.project;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Project {
    private long id;
    private String name;
    private String url;
    private double healthScore;

    public Project(long id, String url, String name) {
        this.id = id;
        this.url = url;
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
                project.name.equals(name) &&
                project.url.equals(url);
    }
}
