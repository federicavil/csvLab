package data_manager;

import model.Commit;
import model.Issue;
import model.Release;

import java.util.*;

public class DataPreparer {
    private List<Commit> commits;
    private List<Release> releases;
    private List<Issue> issues;


    public DataPreparer(List<Commit> commits, List<Release> releases, List<Issue> issues){
        this.commits = commits;
        this.releases = releases;
        this.issues = issues;
    }

    public List<Release> releaseClassesLinkage(){
        int j = this.commits.size()-1;
        Commit currentCommit;
        Map<String, Boolean> classes = null;
        //Scorro tutte le releases in ordine cronologico crescente
        for(int i = 0; i < this.releases.size(); i++){
            // Dalla seconda release in poi, questa avrà anche le classi della release precedente
            if(i > 0){
              this.releases.get(i).getClasses().putAll(this.releases.get(i-1).getClasses());
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
            this.releases.get(index).getClasses().put(file, false);
        }
        for(String file: commit.getClassDeleted()){
            this.releases.get(index).getClasses().remove(file);
        }
    }

    public List<Issue> commitsIssuesLinkage(){
        for(Commit commit: this.commits){
            if(isUseful(commit)){
                if(!commit.getIssues().isEmpty())
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
        if (commit.getClassModified().isEmpty() && commit.getClassAdded().isEmpty() && commit.getClassDeleted().isEmpty())
            return false;
        else return true;
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
}
