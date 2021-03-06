package file_manager;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvFile {
    private File file   ;
    private CSVWriter csvWriter;

    public CsvFile(String path) throws IOException {
        this.file = new File(path);
        this.initializeWriter();
    }

    private void initializeWriter() throws IOException {
        FileWriter fileWriter = new FileWriter(this.file);
        this.csvWriter = new CSVWriter(fileWriter);
    }

    public void setHeader(String[] header){
        this.addData(header);
    }

    public void addData(String[] data){
        this.csvWriter.writeNext(data);
    }

    public void addData(List<String[]> data){
        this.csvWriter.writeAll(data);
    }

    public void closeFile() throws IOException {
        this.csvWriter.close();
    }
}
