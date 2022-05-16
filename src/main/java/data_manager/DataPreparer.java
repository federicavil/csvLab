package data_manager;

import model.Commit;
import model.Issue;
import model.Release;

import java.util.*;

public class DataPreparer {
    private List<Commit> commits;
    private List<Release> releases;
    private List<Issue> issues;
    private int counter = 0;


    public DataPreparer(List<Commit> commits, List<Release> releases, List<Issue> issues){
        this.commits = commits;
        this.releases = releases;
        this.issues = issues;
    }

    public DataPreparer(List<Release> releases, List<Issue> issues){
        this.releases = releases;
        this.issues = issues;
    }

    public List<Release> releaseClassesLinkage(){
        int j = this.commits.size()-1;
        Commit currentCommit;
        //Scorro tutte le releases in ordine cronologico crescente
        for(int i = 0; i < this.releases.size(); i++){
            // Dalla seconda release in poi, questa avrà anche le classi della release precedente
            if(i > 0){
              this.releases.get(i).getClassBugginess().putAll(this.releases.get(i-1).getClassBugginess());
            }
            // Scorro tutti i commits in ordine cronologico crescente(quindi dalla fine)
            while(j >= 0){
                currentCommit = this.commits.get(j);
                if(currentCommit.getDate().before(this.releases.get(i).getReleasedDate())){
                    // Il commit si riferisce alla release presa in considerazione
                    updateReleaseClasses(i,currentCommit);
                    j--;
                }
                else {
                   break;
                }
            }
        }
        return this.releases;
    }

    private void updateReleaseClasses(int index, Commit commit){
        // Aggiunge o elimina le classe da una determinata release
        for(String file: commit.getClassAdded()){
            this.releases.get(index).getClassBugginess().put(file, false);
        }
        for(String file: commit.getClassDeleted()){
            this.releases.get(index).getClassBugginess().remove(file);
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

    private void linkToIssue(Commit commit){
        for(String issueCommit: commit.getIssues()) {
            for (int i = 0; i < this.issues.size();i++) {
                if(this.issues.get(i).getkey().compareTo(issueCommit) == 0){
                    this.issues.get(i).getRelatedCommits().add(commit);
                }
            }
        }
    }

    public List<Issue> versionIssuesLinkage(){
        List<Issue> toRemove = new ArrayList<>();
        // Aggiunge opening e fixed version, conoscendo la data
        for(Issue issue: this.issues) {
            issue.setOpeningVersion(setVersion(issue.getCreationDate()));
            issue.setFixVersion(setVersion(issue.getFixDate()));
            if(!consistencyCheck(issue)){
                toRemove.add(issue);
            }
        }
        System.out.println("RIMOSSE: "+ toRemove.size());
        System.out.println("CONTROLLO: "+ counter);

        this.issues.removeAll(toRemove);

        return this.issues;
    }

    private Release setVersion(Date date){
        for(Release release: this.releases){
            if(date.before(release.getReleasedDate()))
                return release;
        }
        return null;
    }

    private boolean consistencyCheck(Issue issue){
        // Se la fix version o l'affected version non fanno parte del mio dataset butto l'issue
        if((issue.getFixVersion() == null) || (issue.getOpeningVersion() == null))
            return false;
        /*else if(issue.getFixVersion() == issue.getOpeningVersion()){
            this.counter++;
            return false;
        }*/

        // Controlla se nelle AV c'è indicata anche la FV e in caso la rimuove
        List<Release> affectedVersions =  new ArrayList<>();
        for(Release version: issue.getAffectedVersions()){
            if (!version.getReleasedDate().after(issue.getFixDate())){
                affectedVersions.add(version);
            }
        }
        issue.setAffectedVersions(affectedVersions);

        return true;
    }
}
