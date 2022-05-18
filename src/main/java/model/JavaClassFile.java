package model;

import java.util.*;

public class JavaClassFile {

    private String name;
    private int age;
    private Date creationDate;
    private int loc;
    private int numberOfRevisions;
    private Set<String> authors;
    private List<Commit> relatedCommits;
    private List<Commit> fullHistory;
    private Set<String> chgSet;
    private int maxChgSet;
    private int[] avgChgSet;
    private boolean bugginess;

    public JavaClassFile(String name,Date date,  boolean bugginess){
        this.bugginess = bugginess;
        this.name = name;
        this.creationDate = date;
        this.relatedCommits = new ArrayList<>();
        this.fullHistory = new ArrayList<>();
        this.authors = new HashSet<>();
        this.numberOfRevisions = 0;
        this.chgSet = new HashSet<>();
        this.maxChgSet = 0;
        this.avgChgSet = new int[2];
    }

    public JavaClassFile(String name, Date date, boolean bugginess, List<Commit> totalCommits){
        this.bugginess = bugginess;
        this.name = name;
        this.creationDate = date;
        this.relatedCommits = new ArrayList<>();
        this.fullHistory = totalCommits;
        this.authors = new HashSet<>();
        this.numberOfRevisions = 0;
        this.chgSet = new HashSet<>();
        this.maxChgSet = 0;
        this.avgChgSet = new int[2];
    }

    public JavaClassFile(String name, Date date, int age, boolean bugginess, List<Commit> totalCommits, Set<String> authors){
        this.bugginess = bugginess;
        this.name = name;
        this.creationDate = date;
        this.relatedCommits = new ArrayList<>();
        this.fullHistory = totalCommits;
        this.authors = authors;
        this.numberOfRevisions = 0;
        this.age = age;
        this.chgSet = new HashSet<>();
        this.maxChgSet = 0;
        this.avgChgSet = new int[2];
    }

    public JavaClassFile(String name, boolean bugginess, List<Commit> commits, List<Commit> totalCommits){
        this.bugginess = bugginess;
        this.name = name;
        this.relatedCommits = commits;
        this.fullHistory = totalCommits;
        this.chgSet = new HashSet<>();
        this.maxChgSet = 0;
        this.avgChgSet = new int[2];
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setAge(int age){
        this.age = age;
    }

    public int getAge(){
        return this.age;
    }

    public int getAuthorsNumber(){
        return this.authors.size();
    }

    public int getMaxChgSet(){
        return this.maxChgSet;
    }

    public void setMaxChgSet(int max){
        this.maxChgSet = max;
    }

    public int getChgSetSize(){
        return this.chgSet.size();
    }

    public void addToChgSet(String file){
        this.chgSet.add(file);
    }

    public int[] getAvgChgSet(){
        return this.avgChgSet;
    }

    public int getAvgSetSize(){
        return this.avgChgSet[0];
    }

    public void addAuthor(String author){
        this.authors.add(author);
    }
    public Set<String> getAuthors(){
        return this.authors;
    }
    public void incrementNR(){
        this.numberOfRevisions++;
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
