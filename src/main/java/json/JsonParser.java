package json;

import model.Commit;
import model.Issue;
import model.Release;
import model.RenamedClassesList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JsonParser {

    private JsonParser(){
        throw new IllegalStateException("Utility class");
    }

    public static List<Issue> getIssues(JSONObject jsonResult) throws ParseException {
        JSONArray jsonIssues = jsonResult.getJSONArray("issues");
        List<Issue> issues = new ArrayList<>();
        for(int i = 0; i < jsonIssues.length(); i++){
            Issue issue = new Issue();
            JSONObject obj = jsonIssues.getJSONObject(i);
            issue.setId((String) obj.get("id"));
            issue.setKey((String) obj.get("key"));
            JSONObject objFields = (JSONObject) obj.get("fields");
            issue.setCreationDate(stringToDate((String)objFields.get("created")));
            issue.setFixDate(stringToDate((String)objFields.get("resolutiondate")));
            List<Release> affectedVersions = new ArrayList<>();
            JSONArray jsonAffected = objFields.getJSONArray("versions");

            for(int j = 0; j < jsonAffected.length(); j++){
                Release release = new Release();
                release.setName((String) jsonAffected.getJSONObject(j).get("name"));
                try{
                    release.setReleasedDate(stringToDate((String) jsonAffected.getJSONObject(j).get("releaseDate")));
                }catch (JSONException e){
                    release.setReleasedDate(null);
                }
                affectedVersions.add(release);
            }
            issue.setAffectedVersions(affectedVersions);
            issues.add(issue);
        }

        return issues;
    }

    public static List<Release> getReleases(JSONArray result) throws ParseException {
        List<Release> releases = new ArrayList<>();
        for(int i = 0; i < result.length(); i++){
            Release release = new Release();
            JSONObject obj = result.getJSONObject(i);
            release.setName((String)obj.get("name"));
            try {
                release.setReleasedDate(stringToDate((String)obj.get("releaseDate")));
            } catch (JSONException e) {
                release.setReleasedDate(null);

            }
            releases.add(release);
        }
        return releases;
    }

    private static Date stringToDate(String string) throws ParseException {
        string = string.split("T")[0];
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(string);
    }

    public static List<Commit> getCommits(BufferedReader reader, String projectName) throws IOException, ParseException {
        List<Commit> commits = new ArrayList<>();
        Commit commit = null;
        String line = reader.readLine();
        boolean isAtTheEnd = true;
        while(line != null){
            String[] wordsSplitted = Arrays.asList(line.split("\\s+")).stream().filter(str -> !str.isEmpty()).collect(Collectors.toList()).toArray(new String[0]);
            List<String> words = Arrays.asList(wordsSplitted);
            if(!words.isEmpty()){
                if(words.get(0).startsWith("commit") && isAtTheEnd){
                    commit = startNewCommit(commit,commits);
                    commit.setId(words.get(1));
                    isAtTheEnd = false;
                }
                else if(words.get(0).startsWith("Author") && commit.getAuthor() == null){
                    commit.setAuthor(words.get(words.size()-1));
                }
                else if(words.get(0).startsWith("Date") && commit.getDate() == null){
                    commit.setDate(stringToDate(words.get(1)));
                }
                else if(words.get(0).equals("A") || words.get(0).equals("D") || words.get(0).equals("M") || Pattern.matches("R[0-9]+",words.get(0))){
                    isAtTheEnd = addFile(commit,words.subList(1,words.size()),words.get(0));
                }
                else{
                    addIssues(commit, words, projectName);
                }
            }
            line = reader.readLine();
        }

        return commits;
    }

    private static Commit startNewCommit(Commit commit, List<Commit> commits){
        if(commit != null){
            commits.add(commit);
        }
        return new Commit();

    }

    private static void addIssues(Commit commit, List<String> words, String projectName){
        for(String word: words){
            if(Pattern.matches(projectName + "-[0-9]+", word)){
                commit.addIssue(word);
            }
            else if(Pattern.matches("#[0-9]+",word)){
                word = projectName + "-" + word.substring(1);
                commit.addIssue(word);
            }
        }
    }

    private static boolean addFile(Commit commit, List<String> files, String mode ){
        // Aggiunge una classe alla lista a cui fa riferimento
        String file = files.get(0);
        if(file.contains(".java") && !file.contains("test")){
            switch(mode){
                case "A":
                    commit.addClassAdded(file);
                    break;
                case "M":
                    commit.addClassModified(file);
                    break;
                case "D":
                    commit.addClassDeleted(file);
                    break;
                default:
                    updateRenameFiles(commit,file, files.get(1));
                    break;
            }
        }
        return true;
    }

    private static void updateRenameFiles(Commit commit, String oldFile, String newFile){
        commit.addClassDeleted(oldFile);
        commit.addClassAdded(newFile);
        HashMap<String, List<String>> renamed = RenamedClassesList.getInstance().getRenamedClasses();
        List<String> newRename = renamed.get(oldFile);
        if(newRename == null){
            newRename = new ArrayList<>();
        }
        else{
            RenamedClassesList.getInstance().getRenamedClasses().remove(oldFile);
        }
        newRename.add(oldFile);
        RenamedClassesList.getInstance().getRenamedClasses().put(newFile, newRename);

    }
}
