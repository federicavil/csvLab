package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Issue {

    private String id;
    private String key;
    private Date creationDate;
    private Date fixDate;
    private List<Release> affectedVersions;

    public Issue(String id, String key,Date creationDate, Date fixDate, ArrayList<Release> affectedVersions){
        this.id = id;
        this.key = key;
        this.creationDate = creationDate;
        this.fixDate = fixDate;
        this.affectedVersions = affectedVersions;
    }

    public Issue() {
        this.affectedVersions = new ArrayList<>();
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
        this.affectedVersions = affectedVersions;
    }

}
