package data_manager;

import csv.CsvCreator;
import csv.CsvFile;
import model.Commit;
import model.Issue;
import model.Release;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainClass {

    private static final String PROJECT_NAME = "BOOKKEEPER";
    private static final String PROJECT_LOCATION = "C:/Users/Federica/git/bookkeeper_ml";
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
            // Ordino le releases ed elimino l'ultima metà delle releases
            Collections.sort(releases, Comparator.comparing(Release::getReleasedDate));
            releases = releases.subList(0,releases.size()/2);

            DataPreparer preparer = new DataPreparer(commits,releases,issues);
            // Determino le classi presenti nelle varie releases
            releases = preparer.releaseClassesLinkage();
            // Determino i commits relativi alle varie issues
            issues = preparer.commitsIssuesLinkage();
            // Calcolo la bugginess delle classi
            DataCalculator calculator = new DataCalculator(issues,releases);
            releases = calculator.calculateBugginess();


            //Creo il file csv
            CsvCreator file = new CsvCreator("bugginess.csv",new String[]{"Release","Class","Bugginess"});
            file.writeDataOnCsv(releases);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
