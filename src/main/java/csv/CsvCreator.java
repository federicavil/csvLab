package csv;

import model.Release;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvCreator {

    private CsvFile file;

    public CsvCreator(String filepath, String[] header) throws IOException {
        this.file = new CsvFile(filepath);
        this.file.setHeader(header);
    }

    public void writeDataOnCsv(List<Release> releases) throws IOException {
        List<String[]> data = new ArrayList<>();
        System.out.println("DATI");
        Boolean isBuggy = null;
        int counter = 0;
        for(Release release: releases){
            for(String className: release.getClassBugginess().keySet()){
                isBuggy = release.getClassBugginess().get(className);
                data.add(new String[] {release.getName(), className, isBuggy.toString() });
                if(isBuggy){
                    counter++;
                    System.out.println(release.getName() + " "+className);
                }
            }
        }
        this.file.addData(data);
        this.file.closeFile();
        System.out.println("SCRITTE " + counter);
    }

}
