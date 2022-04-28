import DataManager.DataCreator;
import DataManager.Retriever;
import Model.Commit;
import Model.Issue;
import Model.Release;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class MainClass {

    private static final String projectName = "BOOKKEEPER";
    private static final String projectLocation = "C:/Users/Federica/git/bookkeeper_ml";
    private static final String branch = "HEAD";

    public static void main(String[] args){
        Retriever ret = new Retriever(projectName,projectLocation,branch);

        List<Release> releases;
        List<Issue> issues;
        List<Commit> commits;

        try {
            // Recupero le release, i commit e le issues
            issues = ret.retrieveIssues();
            releases = ret.retrieveReleases();
            commits = ret.retrieveCommits();
            // Determino le classi presenti nelle varie releases
            DataCreator.releaseClassesLinkage(commits,releases);
            // Elimino i commit che non si riferiscono alle issue prese in considerazione
            System.out.println(commits.size());
            DataCreator.deleteUnnecessaryCommits(commits,issues);
            System.out.println(commits.size());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
