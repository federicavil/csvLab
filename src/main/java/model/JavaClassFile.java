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
    private int chgSet;
    private int maxChgSet;
    private int[] avgChgSet;
    private boolean[] bugginess;
    private int churn;
    private int maxChurn;
    private int[] avgChurn;

    public JavaClassFile(String name,Date date, boolean[] bugginess){
        this.bugginess = bugginess;
        this.name = name;
        this.creationDate = date;
        this.relatedCommits = new ArrayList<>();
        this.fullHistory = new ArrayList<>();
        this.authors = new HashSet<>();
        this.numberOfRevisions = 0;
        this.chgSet = 0;
        this.maxChgSet = 0;
        this.avgChgSet = new int[2];
        this.churn = 0;
        this.maxChurn = 0;
        this.avgChurn = new int[2];
        this.loc = 0;
    }


    public JavaClassFile(String name, Date date, int loc, int age, boolean[] bugginess, List<Commit> totalCommits, Set<String> authors){
        this.bugginess = bugginess;
        this.name = name;
        this.creationDate = date;
        this.relatedCommits = new ArrayList<>();
        this.fullHistory = totalCommits;
        this.authors = authors;
        this.numberOfRevisions = 0;
        this.age = age;
        this.chgSet = 0;
        this.maxChgSet = 0;
        this.avgChgSet = new int[2];
        this.churn = 0;
        this.maxChurn = 0;
        this.avgChurn = new int[2];
        this.loc = loc;
    }


    public int getChurn() {
        return churn;
    }

    public void updateChurn(int newChurn){
        this.churn = this.churn + newChurn;
    }

    public void setChurn(int churn) {
        this.churn = churn;
    }

    public int getMaxChurn() {
        return maxChurn;
    }

    public void setMaxChurn(int maxChurn) {
        this.maxChurn = maxChurn;
    }

    public int[] getAvgChurn() {
        return avgChurn;
    }

    public int getAvgChurnVal(){
        return this.avgChurn[0];
    }

    public void setAvgChurn(int avgChurn) {
        this.avgChurn[0] = avgChurn;
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
        return this.chgSet;
    }
    public void setAvgChgSetSize(int value){
        this.avgChgSet[0] = value;
    }

    public void setChgSetSize(int val){
        this.chgSet = val;
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
        this.relatedCommits.add(commit);
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

    public boolean[] isBuggy(){
        return this.bugginess;
    }

    public void setBugginess(boolean[] bugginess){
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
        this.fullHistory.add(commit);
    }
}
