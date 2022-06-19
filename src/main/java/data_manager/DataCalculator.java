package data_manager;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataCalculator {

    private List<Release> releases;
    private List<Issue> issues;

    public DataCalculator(List<Issue> issues, List<Release> releases){
        this.issues = issues;
        this.releases = releases;
    }

    public List<Release> calculateBugginess(Double proportion){
        for(int i = 0; i < this.issues.size(); i++){
            Issue issue = this.issues.get(i);
            // Controllo se l'issue ha associati dei commit
            if(!issue.getRelatedCommits().isEmpty()){
                // Controllo se è già stata indicata l'AV
                if(issue.getAffectedVersions().isEmpty()){
                    // Calcolo l'AV tramite proportion
                    calculateAffectedVersion(proportion, i);
                }
                assignBugginess(issue);
            }
        }
        return this.releases;
    }



    private void calculateAffectedVersion(Double proportion, int index){
        Issue issue = this.issues.get(index);
        HashMap<String, Integer> releaseMap = (HashMap<String, Integer>) Proportion.generateReleaseMap(this.releases);
        Integer fixVersion = releaseMap.get(issue.getFixVersion().getName());
        Integer openingVersion = releaseMap.get(issue.getOpeningVersion().getName());
        Integer injectedVersion = (int)Math.round(fixVersion-(fixVersion-openingVersion)*proportion);
        List<Release> affectedVersions = new ArrayList<>();
        for(Release release: this.releases){
            int relNumber = releaseMap.get(release.getName());
            if((relNumber >= injectedVersion) && (relNumber < fixVersion)){
                affectedVersions.add(release);
            }
        }
        this.issues.get(index).setAffectedVersions(affectedVersions);
    }

    private void updateClassBugginess(Release affectedVersion, List<Commit> relatedCommit){
        for(Commit commit: relatedCommit){
            //Prendo i file modificati da ogni commit che si riferisce alla issue considerata
            for(String file: commit.getClassModified()){
                // Controllo se il file non è presente nell'elenco delle classi della release
                if(!affectedVersion.getClasses().containsKey(file)){
                    //Controllo se aveva un nome diverso nella release
                    setOriginalFile(affectedVersion,file);
                }
                else {
                    // Aggiorno la bugginess
                    setBugginess(affectedVersion.getClasses().get(file),commit.getRelease());
                }
            }
        }
    }

    private void setBugginess(JavaClassFile javaClassFile,Release release){
        int i;
        for(i = 0; i < this.releases.size(); i++){
            if(this.releases.get(i).equals(release))
                break;
        }
        for(int j = i; j < this.releases.size(); j++){
            //Setto la bugginess vista dal punto di vista della release j a true
            javaClassFile.isBuggy()[j] = true;
        }
    }

    private void setOriginalFile(Release release, String file){
        HashMap<String, String> renamed = (HashMap<String, String>) RenamedClassesList.getInstance().getRenamedClasses();
        String newFile = renamed.get(file);
        while(newFile != null && !release.getClasses().containsKey(newFile)){
            newFile = renamed.get(newFile);
        }
        if(newFile != null){
            setBugginess(release.getClasses().get(newFile),release);
        }

    }

    private void assignBugginess(Issue issue){
        for(Release affectedVersion: issue.getAffectedVersions()) {
            for(Release release: this.releases){
                if (release.getName().equals(affectedVersion.getName())) {
                    // Se sto considerando l'affected version aggiorno la bugginess
                    updateClassBugginess(release, issue.getRelatedCommits());
                    break;
                }
            }
        }
    }

}
