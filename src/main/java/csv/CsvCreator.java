package csv;

import model.JavaClassFile;
import model.Release;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvCreator {

    private CsvFile file;
    private String[] header = new String[]{"Release","Class","LOC","NR","NAuth",
            "Age","ChgSetSize","MAX_ChgSetSize","AVG_ChgSetSize",
            "Churn","MAX_Churn","AVG_Churn","Bugginess"};

    public CsvCreator(String filepath, String[] header) throws IOException {
        this.file = new CsvFile(filepath);
        this.file.setHeader(header);
    }

    public CsvCreator(String filepath) throws IOException {
        this.file = new CsvFile(filepath);
        this.file.setHeader(header);
    }

    public void writeDataOnCsv(List<Release> releases) throws IOException {
        List<String[]> data = new ArrayList<>();
        System.out.println("DATI");
        Boolean isBuggy = null;
        int counter = 0;
        for(Release release: releases){
            for(JavaClassFile javaClass: release.getClasses().values()){
                isBuggy = javaClass.isBuggy();
                data.add(new String[] {release.getName(),
                        javaClass.getName(),
                        String.valueOf(javaClass.getLoc()),
                        String.valueOf(javaClass.getNumberOfRevisions()),
                        String.valueOf(javaClass.getAuthorsNumber()),
                        String.valueOf(javaClass.getAge()),
                        String.valueOf(javaClass.getChgSetSize()),
                        String.valueOf(javaClass.getMaxChgSet()),
                        String.valueOf(javaClass.getAvgSetSize()),
                        String.valueOf(javaClass.getChurn()),
                        String.valueOf(javaClass.getMax_churn()),
                        String.valueOf(javaClass.getAvgChurnVal()),
                        isBuggy.toString() });
                if(isBuggy){
                    counter++;
                    System.out.println(release.getName() + " "+javaClass.getName());
                }
            }
        }
        this.file.addData(data);
        this.file.closeFile();
        System.out.println("SCRITTE " + counter);
    }

}
