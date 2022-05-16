package model;

import java.util.List;

public class JavaClassFile {

    private String name;
    private int loc;
    private List<Commit> relatedCommits;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public List<Commit> getRelatedCommits() {
        return relatedCommits;
    }

    public void setRelatedCommits(List<Commit> relatedCommits) {
        this.relatedCommits = relatedCommits;
    }
}
