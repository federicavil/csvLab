package weka;

import model.JavaClassFile;
import model.Release;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ArffCreator {
    private File file;
    private String title;
    private String[] header = new String[]{"LOC numeric","NR numeric","NAuth numeric",
            "Age numeric","ChgSetSize numeric","MAX_ChgSetSize numeric","AVG_ChgSetSize numeric",
           "Churn numeric","MAX_Churn numeric","AVG_Churn numeric","Bugginess {'true', 'false'}"};

    public ArffCreator(String filePath, String title) {
        this.file = new File(filePath);
        this.title = title;
    }


    public void writeData(List<Release> releases) throws IOException {
        FileWriter fileWriter = new FileWriter(this.file);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        //Titolo
        printWriter.println("@relation "+this.title);
        //Header
        for(String attribute: this.header){
            printWriter.println("@attribute "+attribute);
        }
        // Dati
        printWriter.println("@data");
        for(Release release: releases){
            for(JavaClassFile javaClass: release.getClasses().values()){
                String data = javaClass.getLoc() +","+
                        javaClass.getNumberOfRevisions() +","+
                        javaClass.getAuthorsNumber() +","+
                        javaClass.getAge() +","+
                        javaClass.getChgSetSize() +","+
                        javaClass.getMaxChgSet() +","+
                        javaClass.getAvgSetSize() +","+
                        javaClass.getChurn() +","+
                        javaClass.getMaxChurn() +","+
                        javaClass.getAvgChurnVal() +","+
                        javaClass.isBuggy();
                printWriter.println(data);
            }
        }

        printWriter.close();
    }

}
