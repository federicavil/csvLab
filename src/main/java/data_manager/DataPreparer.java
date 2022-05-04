package data_manager;

import model.Commit;
import model.Issue;
import model.Release;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataPreparer {

    private DataPreparer(){
        throw new IllegalStateException("Utility class");
    }

    public static void releaseClassesLinkage(List<Commit> commits, List<Release> releases){
        int j = commits.size()-1;
        Commit currentCommit;
        Map<String, Boolean> classes = null;
        //Scorro tutte le releases in ordine cronologico crescente
        for(int i = 0; i < releases.size(); i++){
            // Dalla seconda release in poi, questa avrà anche le classi della release precedente
            if(i > 0){
              releases.get(i).getClasses().putAll(releases.get(i-1).getClasses());
            }
            // Scorro tutti i commits in ordine cronologico crescente(quindi dalla fine)
            while(j >= 0){
                currentCommit = commits.get(j);
                if(currentCommit.getDate().before(releases.get(i).getReleasedDate())){
                    // Il commit si riferisce alla release presa in considerazione
                    updateReleaseClasses(releases.get(i),currentCommit);
                    j--;
                }
                else {
                   break;
                }
            }

        }
    }

    private static void updateReleaseClasses(Release release, Commit commit){
        // Aggiunge o elimina le classe da una determinata release
        for(String file: commit.getClassAdded()){
            release.getClasses().put(file, false);
        }
        for(String file: commit.getClassDeleted()){
            release.getClasses().remove(file);
        }
    }

    public static void commitsIssuesLinkage(List<Commit> commits, List<Issue> issues){
        List<Commit> commitToRemove = new ArrayList<>();
        for(Commit commit: commits){
            if(isUseful(commit)){
                if(!commit.getIssues().isEmpty())
                    linkToIssue(commit,issues);
            }
            else commitToRemove.add(commit);
        }
        commits.removeAll(commitToRemove);
        List<Issue> issueToRemove = new ArrayList<>();
        for(Issue issue: issues){
            if(issue.getRelatedCommits().isEmpty())
                issueToRemove.add(issue);
        }
        issues.removeAll(issueToRemove);
    }

    public static boolean isUseful(Commit commit){
        if (commit.getClassModified().isEmpty() && commit.getClassAdded().isEmpty() && commit.getClassDeleted().isEmpty())
            return false;
        else return true;
    }

    private static void linkToIssue(Commit commit, List<Issue> issues){
        for(String issueCommit: commit.getIssues()) {
            for (int i = 0; i < issues.size();i++) {
                if(issues.get(i).getkey().compareTo(issueCommit) == 0){
                    issues.get(i).getRelatedCommits().add(commit);
                }
            }
        }

    }

}
