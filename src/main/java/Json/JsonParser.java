package Json;

import Model.Issue;
import Model.Release;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JsonParser {

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

    public static List<Release> getRelease(JSONArray result) throws ParseException {
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
        string = string.substring(0,10);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(string);
    }
}
