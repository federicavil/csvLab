package data_manager;

import model.Commit;
import model.Issue;
import model.JavaClassFile;
import model.Release;

import java.io.IOException;
import java.util.*;

public class DataPreparer {
    private List<Commit> commits;
    private List<Release> releases;
    private List<Issue> issues;
    private FeatureCalculator calculator;


    public DataPreparer(List<Commit> commits, List<Release> releases, List<Issue> issues, FeatureCalculator calculator){
        this.commits = commits;
        this.releases = releases;
        this.issues = issues;
        this.calculator = calculator;
    }

    public DataPreparer(List<Release> releases, List<Issue> issues){
        this.releases = releases;
        this.issues = issues;
    }

    /**
     * Associates to every release its own classes
     * @return updated releases list
     * @throws InterruptedException
     */
    public List<Release> releaseClassesLinkage() throws InterruptedException {
        int j = this.commits.size()-1;
        int i;
        //Scorro tutte le releases in ordine cronologico crescente
        for(i = 0; i < this.releases.size(); i++){
            // Dalla seconda release in poi, questa avrà anche le classi della release precedente
            if(i > 0){
                copyPreviousClasses(i);
            }
            // Scorro tutti i commits in ordine cronologico crescente(quindi dalla fine)
            while(j >= 0){
                Commit currentCommit = this.commits.get(j);
                if(currentCommit.getDate().before(this.releases.get(i).getReleasedDate())){
                    // Il commit si riferisce alla release presa in considerazione
                    this.commits.get(j).setRelease(this.releases.get(i));
                    updateReleaseClasses(i,this.commits.get(j));
                    j--;
                }
                else {
                    break;
                }
            }
        }
        return this.releases;
    }

    private void copyPreviousClasses(int index){
        Map<String, JavaClassFile> classes = this.releases.get(index -1).getClasses();
        Map<String, JavaClassFile> newClasses = new HashMap<>();
        // Prendo tutte le classi della precedente release e le copio all'interno della successiva release,
        for(Map.Entry<String,JavaClassFile> entry : classes.entrySet()){
           String name = entry.getKey();
           JavaClassFile classFile = classes.get(name);
           List<Commit> newCommits = new ArrayList<>();
           // Copio tutti i commit rappresentanti la full history della classe
           for(Commit commit: classFile.getFullHistory()){
               newCommits.add(new Commit(commit.getId(),commit.getAuthor(),commit.getDate(),commit.getIssues(),commit.getClassAdded(),
                            commit.getClassModified(),commit.getClassDeleted()));
           }
           // Copio tutti gli autori
           Set<String> newAuthors = new HashSet<>();
           for(String author:  classFile.getAuthors()){
               newAuthors.add(author);
           }
           // Creo la nuova classe, copiando alcuni valori dalla precedente release e ricalcolando l'age
           JavaClassFile newFile = new JavaClassFile(name, classFile.getCreationDate(), classFile.getLoc(), calculator.calculateAge(classFile.getCreationDate(), this.releases.get(index).getReleasedDate()) ,classFile.isBuggy(), newCommits, newAuthors);
           newClasses.put(name, newFile);
        }
        this.releases.get(index).setClasses(newClasses);
    }

    private void updateReleaseClasses(int index, Commit commit) throws InterruptedException {
        Release release = this.releases.get(index);
        List<LocThread> threads = new ArrayList<>();
        // Aggiunge o elimina le classe da una determinata release
        for(String file: commit.getClassAdded()){
            // Array che indica, in ogni release, se la classe è buggy o meno
            boolean[] bugginess = new boolean[this.releases.size()];
            Arrays.fill(bugginess, Boolean.FALSE);
            // Creo la nuova classe
            JavaClassFile javaClass = new JavaClassFile(file, commit.getDate(),bugginess);
            javaClass.addRelatedCommit(commit);
            javaClass.addToFullHistory(commit);
            // Aggiungo la classe alla release corrispondente
            release.getClasses().put(file, javaClass);
            // Calcolo le feature del file tramite questo thread
            LocThread thread = new LocThread(javaClass, commit, release.getReleasedDate());
            threads.add(thread);
            thread.start();
        }
        for(String file: commit.getClassDeleted()){
            release.getClasses().remove(file);
        }

        for(String file: commit.getClassModified()){
            if(release.getClasses().get(file) != null) {
                JavaClassFile javaClass = release.getClasses().get(file);
                javaClass.addRelatedCommit(commit);
                javaClass.addToFullHistory(commit);
                LocThread thread = new LocThread(javaClass, commit, release.getReleasedDate());
                threads.add(thread);
                thread.start();
            }
        }
        for(LocThread thread: threads){
            thread.join();
        }
    }

    public List<Issue> commitsIssuesLinkage(){
        for(Commit commit: this.commits){
            if(isUseful(commit) && !commit.getIssues().isEmpty()){
                linkToIssue(commit);
            }
        }
        List<Issue> issueToRemove = new ArrayList<>();
        for(Issue issue: this.issues){
            if(issue.getRelatedCommits().isEmpty())
                issueToRemove.add(issue);
        }
        this.issues.removeAll(issueToRemove);
        return this.issues;
    }

    public boolean isUseful(Commit commit){
        return !(commit.getClassModified().isEmpty() && commit.getClassAdded().isEmpty() && commit.getClassDeleted().isEmpty());
    }

    // Aggiunge i commit alle relative issue
    private void linkToIssue(Commit commit){
        for(String issueCommit: commit.getIssues()) {
            for (int i = 0; i < this.issues.size();i++) {
                if(this.issues.get(i).getkey().compareTo(issueCommit) == 0){
                    this.issues.get(i).getRelatedCommits().add(commit);
                }
            }
        }
    }

    /**
     * it adds to every issue the opening version and the fixed version, by knowing the creation date and the fixed date
     * @return the updated issues list
     */

    public List<Issue> versionIssuesLinkage(){
        List<Issue> toRemove = new ArrayList<>();
        for(Issue issue: this.issues) {
            issue.setOpeningVersion(getVersion(issue.getCreationDate()));
            issue.setFixVersion(getVersion(issue.getFixDate()));
            if(!consistencyCheck(issue)){
                toRemove.add(issue);
            }
        }

        this.issues.removeAll(toRemove);

        return this.issues;
    }

    private Release getVersion(Date date){
        for(Release release: this.releases){
            if(date.before(release.getReleasedDate()))
                return release;
        }
        return null;
    }

    private boolean consistencyCheck(Issue issue){
        // Se la fix version o la opening version non fanno parte del mio dataset butto l'issue
        if((issue.getFixVersion() == null) || (issue.getOpeningVersion() == null))
            return false;
        // Se la fix version coincide con la opening version, butto l'issue
        else if(issue.getFixVersion() == issue.getOpeningVersion()){
            return false;
        }

        // Controlla se nelle AV c'è indicata anche la FV e in caso la rimuove
        List<Release> affectedVersions =  new ArrayList<>();
        // Impostato a true all'inizio perchè se non è indicata nessuna affected bisogna poi applicare proportion
        boolean isOpeningPresent = true;
        for(Release version: issue.getAffectedVersions()){
            isOpeningPresent = false;
            if (!version.getReleasedDate().after(issue.getFixDate())){
                affectedVersions.add(version);
                if(issue.getOpeningVersion().getName().equals(version.getName()))
                    isOpeningPresent = true;
            }
        }
        // Se nelle affected non è presente la opening version la aggiunge
        if(!isOpeningPresent){
            affectedVersions.add(issue.getOpeningVersion());
        }
        issue.setAffectedVersions(affectedVersions);

        return true;
    }

    class LocThread extends Thread {
        private JavaClassFile file;
        private Commit commit;
        private Date releaseDate;

        LocThread(JavaClassFile file, Commit commit, Date releaseDate) {
            this.file = file;
            this.commit = commit;
            this.releaseDate = releaseDate;
        }

        @Override
        public void run() {
            try {
                calculator.updateFeatures(file,commit, releaseDate);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
