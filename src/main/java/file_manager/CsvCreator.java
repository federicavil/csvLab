package file_manager;

import model.JavaClassFile;
import model.Release;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvCreator {

    private CsvFile file;
    private String[] header;

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
        int counter = 0;
        for(Release release: releases){
            for(JavaClassFile javaClass: release.getClasses().values()){
                boolean[] bugginess = javaClass.isBuggy();
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
                        String.valueOf(javaClass.getMaxChurn()),
                        String.valueOf(javaClass.getAvgChurnVal()),
                        String.valueOf(bugginess[bugginess.length-1])});
            }
        }
        this.file.addData(data);
        this.closeFile();
    }

    public void writeDataOnCsv(String project, String classifier, List<String[]> results){
        List<String[]> data = new ArrayList<>();
        for(int i = 0; i < results.size(); i++){
            data.add(ArrayUtils.addAll(new String[]{project,String.valueOf(i+1),classifier,},results.get(i)));

        }
        this.file.addData(data);
    }

    public void closeFile() throws IOException {
        this.file.closeFile();
    }
}
