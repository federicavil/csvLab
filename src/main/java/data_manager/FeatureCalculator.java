package data_manager;

import model.Commit;
import model.JavaClassFile;
import model.Release;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class FeatureCalculator {

    private String projectName;
    private String projectLocation;

    public FeatureCalculator(String projectName, String projectLocation) {
        this.projectName = projectName;
        this.projectLocation = projectLocation;
    }

    private void calculateLines(JavaClassFile file, Commit commit) throws IOException {
        DataRetriever retriever = new DataRetriever(projectName,projectLocation);
        List<String> lines = retriever.retrieveFileContent(commit, file.getName());
        file.setLoc(lines.size());
    }

    private void calculateChurn(JavaClassFile file, Commit commit) throws IOException {
        int previousCommitIdx = file.getFullHistory().indexOf(commit)-1;
        String previousCommitId = file.getFullHistory().get(previousCommitIdx).getId();
        DataRetriever retriever = new DataRetriever(projectName,projectLocation);
        int[] diff = retriever.getLinesDiff(commit.getId(), previousCommitId, file.getName());
        int churn = diff[0] - diff[1];
        file.setLoc(file.getLoc()+churn);
        file.updateChurn(churn);
        if(churn > file.getMaxChurn())
            file.setMaxChurn(churn);
        int[] avgChurn = file.getAvgChurn();
        file.setAvgChurn(((avgChurn[1]*avgChurn[0])+churn)/(avgChurn[1]+1));
        file.getAvgChurn()[1]++;
    }

    public void updateFeatures(JavaClassFile javaClass, Commit commit, Date releaseDate) throws IOException {
        javaClass.incrementNR();
        javaClass.addAuthor(commit.getAuthor());
        calculateAge(javaClass,releaseDate);
        calculateChgSetSize(javaClass, commit);
        if(javaClass.getLoc() == 0){
            calculateLines(javaClass, commit);
        }
        else calculateChurn(javaClass, commit);

    }

    private void calculateChgSetSize(JavaClassFile javaClass, Commit commit){
        int sumChg = commit.getClassAdded().size() + commit.getClassModified().size() -1;
        javaClass.setChgSetSize(javaClass.getChgSetSize()+sumChg);
        if(sumChg > javaClass.getMaxChgSet())
            javaClass.setMaxChgSet(sumChg);
        int[] avgChgSet = javaClass.getAvgChgSet();
        javaClass.setAvgChgSetSize(((avgChgSet[1]*avgChgSet[0])+sumChg)/(avgChgSet[1]+1));
        javaClass.getAvgChgSet()[1]++;
    }

    private void calculateAge(JavaClassFile javaClass, Date releaseDate){
        long diffInMillies = Math.abs(releaseDate.getTime() - javaClass.getCreationDate().getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        int weeks = (int)diff/7;
        javaClass.setAge(weeks);

    }



}
