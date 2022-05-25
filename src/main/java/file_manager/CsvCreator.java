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
                        String.valueOf(javaClass.getMaxChurn()),
                        String.valueOf(javaClass.getAvgChurnVal()),
                        isBuggy.toString() });
                if(isBuggy){
                    counter++;
                    System.out.println(release.getName() + " "+javaClass.getName());
                }
            }
        }
        this.file.addData(data);
        this.closeFile();
        System.out.println("SCRITTE " + counter);
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
