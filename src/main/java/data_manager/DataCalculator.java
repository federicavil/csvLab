package data_manager;

import model.Commit;
import model.Issue;
import model.Release;
import model.RenamedClassesList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataCalculator {
    static int counter1 = 0;
    static int counter2 = 0;
    static int modified = 0;

    private List<Release> releases;
    private List<Issue> issues;
    private int proportion = 0;

    public DataCalculator(List<Issue> issues, List<Release> releases){
        this.issues = issues;
        this.releases = releases;
    }

    public List<Release> calculateBugginess(){
        for(Issue issue: this.issues){
            // Controllo se l'issue ha associati dei commit
            if(!issue.getRelatedCommits().isEmpty()){
                // Controllo se le AV già indicate sono consistenti
                consistencyCheckAV(issue);
                // Controllo se è già stata indicata l'AV
                if(!issue.getAffectedVersions().isEmpty()){
                    //Assegno la bugginess alle affected versions
                    assignBugginess(issue);
                    updateProportion();
                }
                else {
                    // Calcolo l'AV tramite le features
                    calculateAffectedVersion();
                }
            }
        }
        System.out.println("Calcolate " + counter1 +" " +counter2);
        System.out.println("Classi modificate "+modified);

        return this.releases;
    }

    private static void updateProportion(){

    }

    private void consistencyCheckAV(Issue issue){
        // Controlla se nelle AV c'è indicata anche la FV e in caso la rimuove
        List<Release> affectedVersions =  new ArrayList<>();
        for(Release version: issue.getAffectedVersions()){
            if (!version.getReleasedDate().after(issue.getFixDate())){
                affectedVersions.add(version);
            }
        }
        issue.setAffectedVersions(affectedVersions);
    }

    private void calculateAffectedVersion(){
        // Tramite proportion incrementale

    }

    private void updateClassBugginess(Release affectedVersion, List<Commit> relatedCommit){
        for(Commit commit: relatedCommit){
            modified += commit.getClassModified().size();
            //Prendo i file modificati da ogni commit che si riferisce alla issue considerata
            for(String file: commit.getClassModified()){
                // Controllo se il file non è presente nell'elenco delle classi della release
                if(!affectedVersion.getClasses().containsKey(file)){
                    //Controllo se aveva un nome diverso nella release
                    setOriginalFile(affectedVersion,file);
                }
                else {
                    // Aggiorno la bugginess
                    affectedVersion.getClasses().replace(file,true);
                    counter1++;
                }
            }
        }
    }

    private void setOriginalFile(Release release, String file){
        HashMap<String, String> renamed = RenamedClassesList.getInstance().getRenamedClasses();
        String oldFile = renamed.get(file);
        while(oldFile != null && !release.getClasses().containsKey(oldFile)){
            oldFile = renamed.get(oldFile);
        }
        if(oldFile != null){
            release.getClasses().replace(oldFile,true);
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
