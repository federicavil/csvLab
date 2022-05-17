package model;

import java.util.ArrayList;
import java.util.List;

public class JavaClassFile {

    private String name;
    private int loc;
    private int numberOfRevisions;
    private List<Commit> relatedCommits;
    private List<Commit> fullHistory;
    private boolean bugginess;

    public JavaClassFile(String name, boolean bugginess){
        this.bugginess = bugginess;
        this.name = name;
        this.relatedCommits = new ArrayList<>();
        this.fullHistory = new ArrayList<>();
    }

    public JavaClassFile(String name, boolean bugginess, List<Commit> totalCommits){
        this.bugginess = bugginess;
        this.name = name;
        this.relatedCommits = new ArrayList<>();
        this.fullHistory = totalCommits;
    }

    public JavaClassFile(String name, boolean bugginess, List<Commit> commits, List<Commit> totalCommits){
        this.bugginess = bugginess;
        this.name = name;
        this.relatedCommits = commits;
        this.fullHistory = totalCommits;
    }

    public void addRelatedCommit(Commit commit){
        this.relatedCommits.add(0,commit);
    }

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

    public boolean isBuggy(){
        return this.bugginess;
    }

    public void setBugginess(boolean bugginess){
        this.bugginess = bugginess;
    }

    public int getNumberOfRevisions() {
        return numberOfRevisions;
    }

    public void setNumberOfRevisions(int numberOfRevisions) {
        this.numberOfRevisions = numberOfRevisions;
    }

    public List<Commit> getFullHistory() {
        return fullHistory;
    }

    public void setFullHistory(List<Commit> fullHistory) {
        this.fullHistory = fullHistory;
    }

    public void addToFullHistory(Commit commit){
        this.fullHistory.add(0,commit);
    }
}
