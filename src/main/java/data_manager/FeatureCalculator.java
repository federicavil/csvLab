package data_manager;

import model.Commit;
import model.JavaClassFile;
import model.Release;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public List<Release> calculateLOC() throws InterruptedException {
        List<LocThread> threads = new ArrayList<>();
        for(Release release: this.releases){
            for(JavaClassFile file: release.getClasses().values()){
                LocThread t = new LocThread(file,file.getFullHistory().get(0));
                threads.add(t);
                t.start();
            }
        }
        for(LocThread thread: threads)
            thread.join();

        return this.releases;
    }

    public List<Release> calculateNumberOfRevisions(){
        for(Release release: this.releases){
            for(JavaClassFile file: release.getClasses().values()){
                file.setNumberOfRevisions(file.getRelatedCommits().size());
            }
        }
        return this.releases;
    }

    class LocThread extends Thread {
        private Commit commit;
        private JavaClassFile file;
        LocThread(JavaClassFile file, Commit commit) {
            this.file = file;
            this.commit = commit;
        }

        public void run() {
            List<String> lines;
            try {
                lines = new DataRetriever(projectName,projectLocation).retrieveFileContent(commit,file.getName());
                file.setLoc(lines.size());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
