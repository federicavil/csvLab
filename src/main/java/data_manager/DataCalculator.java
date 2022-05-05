package data_manager;

import model.Commit;
import model.Issue;
import model.Release;
import model.RenamedClassesList;

import java.util.HashMap;
import java.util.List;

public class DataCalculator {

    public static void calculateBugginess(List<Release> releases, List<Issue> issues){
        for(Issue issue: issues){
            if(!issue.getRelatedCommits().isEmpty()){
                if(!issue.getAffectedVersions().isEmpty()){
                    assignBugginess(issue, releases);
                }
                else {
                    calculateAffectedVersion();
                }
            }
        }
    }

    private static void calculateAffectedVersion(){

    }

    private static void updateClassBugginess(Release affectedVersion, List<Commit> relatedCommit){
        for(Commit commit: relatedCommit){
            for(String file: commit.getClassModified()){
                System.out.println("Version "+ affectedVersion.getName() + " file " +file);
                if(affectedVersion.getClasses().replace(file,true) == null){
                    setOriginalFile(affectedVersion,file);
                }
            }
        }
    }

    private static void setOriginalFile(Release release, String file){
        HashMap<String, List<String>> renamed = RenamedClassesList.getInstance().getRenamedClasses();
        if(renamed.get(file) != null){
            // Vedo qual'è la versione del file presente nella release
            for(String oldFile: renamed.get(file)){
                if(release.getClasses().replace(oldFile,true) != null){

                    break;
                }
            }
        }
        else{
            //controllo per ogni lista se c'è il file
            for(List<String> files: renamed.values()){
                for(String oldFile: files){
                    if(release.getClasses().replace(oldFile,true) != null){

                        break;
                    }
                }
            }
        }
    }

    private static void assignBugginess(Issue issue, List<Release> releases){
        for(Release release: releases){
            for(Release affectedVersion: issue.getAffectedVersions())
                if(release.getName().equals(affectedVersion.getName())){
                    updateClassBugginess(release,issue.getRelatedCommits());
                }
        }
    }

}
