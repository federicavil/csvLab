package data_manager;

import model.Commit;
import model.Issue;
import model.Release;

import java.util.HashMap;
import java.util.List;

public class DataCreator {

    public static void releaseClassesLinkage(List<Commit> commits, List<Release> releases){
        int i = 0;
        int j = commits.size()-1;
        Release currentRelease;
        Commit currentCommit;
        HashMap<String, Boolean> classes = null;
        //Scorro tutte le releases in ordine cronologico crescente
        while(i < releases.size()){
            currentRelease = releases.get(i);
            if(i>0){
                // Dalla seconda release in poi, questa avrà anche le classi della release precedente
                currentRelease.setClasses(classes);
            }
            // Scorro tutti i commpits in ordine cronologico crescente
            while(j >= 0){
                currentCommit = commits.get(j);
                if(currentCommit.getDate().before(currentRelease.getReleasedDate())){
                    // Il commit si riferisce alla release presa in considerazione
                    updateReleaseClasses(currentRelease,currentCommit);
                    j--;
                }
                else break;
            }
            classes = currentRelease.getClasses();
            i++;

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

    public static void deleteUnnecessaryCommits(List<Commit> commits, List<Issue> issues){
        // Rimuove i commit non associati ad issue di tipo bug

        for(int i = 0; i < commits.size(); i++){
            Commit commit = commits.get(i);
            if(commit.getIssues().size() == 0){
                commits.remove(commit);
            }
            else{
                boolean isNecessary = false;
                for(String issueCommit: commit.getIssues()){
                    for(Issue issueJira: issues){
                        if(issueJira.getkey().equals(issueCommit)){
                            isNecessary = true;
                            break;
                        }
                    }
                    if(isNecessary) break;
                }
                if(!isNecessary)
                    commits.remove(commit);
            }
        }

    }

}
