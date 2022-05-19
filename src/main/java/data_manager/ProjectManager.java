package data_manager;

import model.Commit;
import model.Issue;
import model.Release;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProjectManager {

    private String projectName;
    private String projectLocation;
    private String branch;
    private DataRetriever retriever;
    private List<Release> releases;

    public ProjectManager(String projectName, String projectLocation){
        this.projectName = projectName;
        this.projectLocation = projectLocation;
        this.branch = "HEAD";
        this.retriever = new DataRetriever(this.projectName, this.projectLocation, this.branch);
        this.releases = null;
    }

    public ProjectManager(String projectName){
        this.projectName = projectName;
        this.retriever = new DataRetriever(this.projectName);
    }

    public List<Issue> getIssues() throws IOException, ParseException {
        return this.retriever.retrieveIssues();
    }

    public List<Release> getReleases() throws IOException, ParseException {
        this.releases = this.retriever.retrieveReleases();
        // Ordino le releases
        Collections.sort(this.releases, Comparator.comparing(Release::getReleasedDate));
        return this.releases;
    }

    public List<Issue> getIssueInfo() throws IOException, ParseException {
        List<Issue> issues = getIssues();
        if(this.releases == null)
            this.releases = retriever.retrieveReleases();
        DataPreparer preparer = new DataPreparer(this.releases,issues);
        return preparer.versionIssuesLinkage();

    }
    public List<Release> getBugginess(double proportion){
        List<Commit> commits;
        List<Issue> issues;

        try {
            // Recupero le release e i commit e le issues
            this.releases = this.getReleases();
            commits = this.retriever.retrieveCommits();
            issues = this.getIssueInfo();

            FeatureCalculator featureCalculator = new FeatureCalculator(this.releases, this.projectName, this.projectLocation);

            DataPreparer preparer = new DataPreparer(commits,this.releases,issues, featureCalculator);
            // Determino le classi presenti nelle varie releases
            this.releases = preparer.releaseClassesLinkage();
            // Determino i commits relativi alle varie issues
            issues = preparer.commitsIssuesLinkage();
            // Calcolo la bugginess delle classi
            DataCalculator calculator = new DataCalculator(issues,this.releases);
            this.releases = calculator.calculateBugginess(proportion);

            //elimino la metà delle release per lo snoring
            this.releases = this.releases.subList(0,this.releases.size()/2);


        } catch (IOException | ParseException | InterruptedException e) {
            return new ArrayList<>();
        }
        return this.releases;
    }

}
