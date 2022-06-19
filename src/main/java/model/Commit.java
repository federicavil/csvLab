package model;

import java.util.*;

public class Commit {

    private String id;
    private String author;
    private Date date;
    private Set<String> issuesId;
    private Set<String> classAdded;
    private Set<String> classModified;
    private Set<String> classDeleted;
    private Release release;

    public Commit() {
        this.author = null;
        this.date = null;
        this.issuesId = new HashSet<>();
        this.classAdded = new HashSet<>();
        this.classModified = new HashSet<>();
        this.classDeleted = new HashSet<>();
    }

    public Commit(String id, String author, Date date, Set<String> issuesId,Set<String> classAdded,Set<String> classDeleted, Set<String> classModified ) {
        this.id = id;
        this.author = author;
        this.date = date;
        this.issuesId = issuesId;
        this.classAdded = classAdded;
        this.classModified = classModified;
        this.classDeleted = classDeleted;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    public Release getRelease() {
        return release;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Set<String> getIssues() {
        return issuesId;
    }

    public void setIssues(Set<String> issues) {
        this.issuesId = issues;
    }

    public void addIssue(String issue){
        this.issuesId.add(issue);
    }

    public Set<String> getClassAdded() {
        return classAdded;
    }

    public void setClassAdded(Set<String> classAdded) {
        this.classAdded = classAdded;
    }

    public void addClassAdded(String toAdd){
        this.classAdded.add(toAdd);
    }

    public Set<String> getClassModified() {
        return classModified;
    }

    public void setClassModified(Set<String> classModified) {
        this.classModified = classModified;
    }

    public void addClassModified(String toAdd){
        this.classModified.add(toAdd);
    }

    public Set<String> getClassDeleted() {
        return classDeleted;
    }

    public void setClassDeleted(Set<String> classDeleted) {
        this.classDeleted = classDeleted;
    }

    public void addClassDeleted(String toAdd){
        this.classDeleted.add(toAdd);
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return this.id;
    }
}
