package data_manager;

import model.Issue;
import model.Project;
import model.Release;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class Proportion {
    /**
     * Calculates the proportion value using cold start approach
     */

    private Proportion() {
        throw new IllegalStateException("Utility class");
    }

    public static Double coldStart(String currentProject) throws IOException, ParseException {
        List<Double> allProjectProportion = new ArrayList<>();

        Project[] projects = Project.values();
        for(Project project: projects){
            if(!project.toString().equals(currentProject)) {
                // Prendo le issue del progetto
                ProjectManager manager = new ProjectManager(project.toString());
                List<Issue> issues = manager.getIssueInfo();
                HashMap<String, Integer> releasesMap = (HashMap<String, Integer>) generateReleaseMap(manager.getReleases());
                allProjectProportion.add(calculateProportion(issues, releasesMap));
            }
        }

        Collections.sort(allProjectProportion);
        // Ritorna la mediana delle proportion sui progetti calcolati
        return allProjectProportion.get(allProjectProportion.size()/2);
    }

    public static Double calculateProportion(List<Issue> issues, Map<String, Integer> releasesMap){
        Double sum = 0.0;
        Double denominator = 0.0;
        for(Issue issue: issues){
            if(!issue.getAffectedVersions().isEmpty()){
                // Prendo i numeri associati alle release di fix, opening e injected
                Integer fixVersion = releasesMap.get(issue.getFixVersion().getName());
                Integer openingVersion = releasesMap.get(issue.getOpeningVersion().getName());
                Integer injectedVersion = releasesMap.get(issue.getAffectedVersions().get(0).getName());
                if(fixVersion-openingVersion != 0)
                    sum = sum +((double)(fixVersion-injectedVersion)/(fixVersion-openingVersion));
                else
                    sum = sum +(fixVersion-injectedVersion);
                denominator = denominator + 1.0;
            }
        }
        if(denominator != 0.0)
            return sum/denominator;
        else return 0.0;
    }

    public static Map<String, Integer> generateReleaseMap(List<Release> releases){
        // Associa ad ogni release un numero
        HashMap<String, Integer> releasesMap = new HashMap<>();
        for(int i = 0; i < releases.size(); i++){
            releasesMap.put(releases.get(i).getName(),i+1);
        }
        return releasesMap;
    }
}

