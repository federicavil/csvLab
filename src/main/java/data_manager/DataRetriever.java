package data_manager;

import json.JsonParser;
import json.JsonReader;
import model.Commit;
import model.Issue;
import model.Release;
import org.json.*;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    /**
     * It implements the retrieving of project information
     */

    private String projectname;
    private String projectLocation;
    private String branch;
    private static final String COMMAND = "cmd.exe";

    public DataRetriever(String projectName, String projectLocation, String branch){
        this.projectname = projectName;
        this.projectLocation = projectLocation;
        this.branch = branch;
    }
    public DataRetriever(String projectName){
        this.projectname = projectName;
    }

    public DataRetriever(String projectName, String projectLocation){
        this.projectname = projectName;
        this.projectLocation = projectLocation;
    }

    /**
     *
     * @return list of all the resolved or closed bugs of the project, obtained from Jira
     * @throws IOException
     * @throws ParseException
     */

    public List<Issue> retrieveIssues() throws IOException, ParseException {
        int i = 0;
        int j;
        int total = 1;
        String url;
        List<Issue> issues = new ArrayList<>();
        while(i < total){
            j = i + 1000;
            url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                    + this.projectname + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
                    + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt=" + i+ "&maxResults=" + j;
            JSONObject jsonResult = JsonReader.readJsonFromUrl(url);
            issues.addAll(JsonParser.getIssues(jsonResult));
            total = jsonResult.getInt("total");
            i = j;
        }
        return issues;
    }

    /**
     *
     * @return list of project releases obtained from Jira
     * @throws IOException
     * @throws ParseException
     */
    public List<Release> retrieveReleases() throws IOException, ParseException {
        String url = "https://issues.apache.org/jira/rest/api/2/project/" + this.projectname + "/versions";
        JSONArray jsonResult = JsonReader.readJsonArrayFromUrl(url);
        return JsonParser.getReleases(jsonResult);
    }

    public List<Commit> retrieveCommits() throws IOException, ParseException {
        ProcessBuilder builder = new ProcessBuilder(
                COMMAND, "/c", "cd " + this.projectLocation+ " && git log --full-history --name-status --date=iso --stat "+ this.branch);
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        return JsonParser.getCommits(r,this.projectname);
    }

    public List<String> retrieveFileContent(Commit commit, String filePath) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
                COMMAND, "/c", "cd " + this.projectLocation+ " && git show "+commit.getId()+":"+filePath);
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        return JsonParser.getFileContent(r);
    }

    public int[] getLinesDiff(String commit1,String commit2, String file) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
                COMMAND, "/c", "cd " + this.projectLocation+ " && git diff --numstat "+commit1+".."+commit2+" -- "+file);
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        int[] result;
        try{
            result = JsonParser.getLinesDiff(r);
        }catch(NullPointerException e){
            result = new int[]{0, 0};
        }
        return result;
    }


}
