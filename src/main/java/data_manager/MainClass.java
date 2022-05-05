package data_manager;

import csv.CsvCreator;
import csv.CsvFile;
import model.Commit;
import model.Issue;
import model.Release;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class MainClass {

    private static final String PROJECT_NAME = "AVRO";
    private static final String PROJECT_LOCATION = "C:/Users/Federica/git/avro";
    private static final String BRANCH = "HEAD";

    public static void main(String[] args){
        DataRetriever ret = new DataRetriever(PROJECT_NAME, PROJECT_LOCATION, BRANCH);

        List<Release> releases;
        List<Issue> issues;
        List<Commit> commits;

        try {
            // Recupero le release, i commit e le issues
            issues = ret.retrieveIssues();
            releases = ret.retrieveReleases();
            commits = ret.retrieveCommits();
            // Elimino l'ultima metà delle releases
            releases = releases.subList(0,releases.size()/2);
            // Determino le classi presenti nelle varie releases
            DataPreparer.releaseClassesLinkage(commits,releases);
            // Determino i commits relativi alle varie issues
            DataPreparer.commitsIssuesLinkage(commits,issues);
            // Calcolo la bugginess delle classi
            DataCalculator.calculateBugginess(releases,issues);
            //Creo il file csv
            CsvCreator file = new CsvCreator("bugginess.csv",new String[]{"Release","Class","Bugginess"});
            file.writeDataOnCsv(releases);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
