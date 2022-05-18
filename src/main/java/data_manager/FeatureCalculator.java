package data_manager;

import model.Commit;
import model.JavaClassFile;
import model.Release;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class FeatureCalculator {

    private List<Release> releases;
    private String projectName;
    private String projectLocation;

    public FeatureCalculator(List<Release> releases){
        this.releases = releases;
    }

    public FeatureCalculator(List<Release> releases, String projectName, String projectLocation) {
        this.releases = releases;
        this.projectName = projectName;
        this.projectLocation = projectLocation;
    }

    public void calculateLOC(Release release) throws IOException, InterruptedException {
        JavaClassFile[] classes = release.getClasses().values().toArray(new JavaClassFile[0]);
        DataRetriever retriever = new DataRetriever(projectName,projectLocation);

        int nThread = 2000;
        int i = 0;
        while(i < classes.length){
            List<LocThread> threads = new ArrayList<>();
            for(int j = 0; j < nThread; j++){
                if(i < classes.length){
                    LocThread thread = new LocThread(classes[i],retriever);
                    threads.add(thread);
                    i++;
                    thread.start();
                }

            }
            for(LocThread thread: threads){
                thread.join();
            }
        }
    }

    public void updateFeatures(JavaClassFile javaClass, Commit commit){
        javaClass.incrementNR();
        javaClass.addAuthor(commit.getAuthor());
        for(String file: commit.getClassAdded()){
            if(!file.equals(javaClass.getName()))
                javaClass.addToChgSet(file);
        }
        for(String file: commit.getClassModified()){
            if(!file.equals(javaClass.getName()))
                javaClass.addToChgSet(file);
        }
        int sumChg = commit.getClassAdded().size() + commit.getClassModified().size() -1;
        if(sumChg > javaClass.getMaxChgSet())
            javaClass.setMaxChgSet(sumChg);
        int[] avgChgSet = javaClass.getAvgChgSet();
        javaClass.getAvgChgSet()[0] = ((avgChgSet[1]*avgChgSet[0])+sumChg)/(avgChgSet[1]+1);
        javaClass.getAvgChgSet()[1]++;
    }

    public void calculateAge(JavaClassFile javaClass, Date releaseDate){
        long diffInMillies = Math.abs(releaseDate.getTime() - javaClass.getCreationDate().getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        int weeks = (int)diff/7;
        javaClass.setAge(weeks);

    }


    class LocThread extends Thread {
        private JavaClassFile file;
        private DataRetriever retriever;

        LocThread(JavaClassFile file, DataRetriever retriever) {
            this.file = file;
            this.retriever = retriever;
        }

        @Override
        public void run() {
            List<String> lines;
            try {
                lines = retriever.retrieveFileContent(file.getFullHistory().get(0), file.getName());
                file.setLoc(lines.size());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
