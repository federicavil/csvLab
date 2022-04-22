import Json.JsonParser;
import Json.JsonReader;
import Model.Issue;
import Model.Release;
import org.json.*;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Retriever {

    private String projectname;
    private String projectLocation;
    private String branch;

    public Retriever(String projectName, String projectLocation, String branch){
        this.projectname = projectName;
        this.projectLocation = projectLocation;
        this.branch = branch;
    }

    public void retrieveBugs() throws IOException, ParseException {
        int i = 0, j = 0, total = 1;
        String url;
        List<Issue> issues = new ArrayList<>();
        while(i < total){
            j = i + 1000;
            url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                    + this.projectname + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
                    + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt=" + i+ "&maxResults=" + j;
            JSONObject jsonResult = JsonReader.readJsonFromUrl(url);
            issues = JsonParser.getIssues(jsonResult);
            total = jsonResult.getInt("total");
            i = j+1;
        }
    }

    public void retrieveReleases() throws IOException, ParseException {
        String url = "https://issues.apache.org/jira/rest/api/2/project/" + this.projectname + "/versions";
        JSONArray jsonResult = JsonReader.readJsonArrayFromUrl(url);
        List<Release> releases = JsonParser.getRelease(jsonResult);
    }

    public void retrieveCommits() throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "cd " + this.projectLocation+ " && git log --name-status --date=iso --stat "+ this.branch);
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) { break; }
            System.out.println(line);
        }
    }
}