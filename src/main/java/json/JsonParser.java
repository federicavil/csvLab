package json;

import model.Commit;
import model.Issue;
import model.Release;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
        String line;
        boolean isAtTheEnd = true;
        while(true){
            line = reader.readLine();
            if(line == null) break;
            else{
                String[] wordsSplitted = Arrays.asList(line.split("\\s+")).stream().filter(str -> !str.isEmpty()).collect(Collectors.toList()).toArray(new String[0]);
                List<String> words = Arrays.asList(wordsSplitted);
                if(!words.isEmpty()){
                    if(words.get(0).startsWith("commit") && isAtTheEnd){
                        if(commit != null){
                            commits.add(commit);
                        }
                        commit = new Commit();
                        commit.setId(words.get(1));
                        isAtTheEnd = false;
                    }
                    else if(words.get(0).startsWith("Author") && commit.getAuthor() == null){
                        commit.setAuthor(words.get(words.size()-1));
                    }
                    else if(words.get(0).startsWith("Date")){
                        if(commit.getDate() == null)
                            commit.setDate(stringToDate(words.get(1)));
                    }
                    else if(words.get(0).equals("A") || words.get(0).equals("D") || words.get(0).equals("M")){
                        isAtTheEnd = addFile(commit,words.get(1),words.get(0));
                    }
                    else{
                        for(String word: words){
                            if(Pattern.matches(projectName + "-[0-9]+", word) || Pattern.matches("#[0-9]+",word)){
                                commit.addIssue(word);
                            }
                            else if(Pattern.matches("#[0-9]+",word)){
                                word = projectName + "-" + word.substring(1);
                                commit.addIssue(word);
                            }
                        }
                    }
                }
            }
        }
        return commits;
    }

    private static boolean addFile(Commit commit, String file, String mode ){
        // Aggiunge una classe alla lista a cui fa riferimento
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
                    break;
            }
        }
        return true;
    }
}
