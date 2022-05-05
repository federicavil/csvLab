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
        for(Release release: releases){
            for(String className: release.getClasses().keySet()){
                data.add(new String[] {release.getName(), className, release.getClasses().get(className).toString()});
            }
        }
        this.file.addData(data);
    }
}
