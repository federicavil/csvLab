package model;

import java.util.*;

public class Issue {

    private String id;
    private String key;
    private Date creationDate;
    private Date fixDate;
    private Release openingVersion;
    private Release fixVersion;
    private List<Release> affectedVersions;
    private List<Commit> relatedCommits;

    public Issue() {
        this.affectedVersions = new ArrayList<>();
        this.relatedCommits = new ArrayList<>();
    }

    public List<Commit> getRelatedCommits() {
        return relatedCommits;
    }

    public void setRelatedCommits(List<Commit> relatedCommits) {
        this.relatedCommits = relatedCommits;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getkey() {
        return key;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getFixDate() {
        return fixDate;
    }

    public void setFixDate(Date fixDate) {
        this.fixDate = fixDate;
    }

    public List<Release> getAffectedVersions() {
        return affectedVersions;
    }

    public void setAffectedVersions(List<Release> affectedVersions) {
        Collections.sort(affectedVersions, Comparator.comparing(Release::getReleasedDate));
        this.affectedVersions = affectedVersions;
    }

    public Release getOpeningVersion() {
        return openingVersion;
    }

    public void setOpeningVersion(Release openingVersion) {
        this.openingVersion = openingVersion;
    }

    public Release getFixVersion() {
        return fixVersion;
    }

    public void setFixVersion(Release fixVersion) {
        this.fixVersion = fixVersion;
    }
}
