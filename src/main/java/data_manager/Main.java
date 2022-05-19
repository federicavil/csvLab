package data_manager;

import csv.CsvCreator;
import model.Release;

import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class Main {

    private static final String PROJECTNAME = "SYNCOPE";
    private static final String PROJECTLOCATION = "C:/Users/Federica/git/syncope_ml";

    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
        Double proportion = Proportion.coldStart();
        ProjectManager manager = new ProjectManager(PROJECTNAME, PROJECTLOCATION);
        // Prendo i dati sulla bugginess delle classi in ogni release
        Instant start = Instant.now();
        List<Release> releases = manager.getBugginess(proportion);
        if(releases.isEmpty()){
            System.out.println("PROBLEMA");
        }
        System.out.println("Calcolata bugginess");
        // Calcolo le features delle classi

        //releases = manager.getFeatures();
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: "+ timeElapsed.toSeconds() +" seconds");

        try {
            //Creo il file csv
            CsvCreator file = new CsvCreator("bugginess_"+PROJECTNAME.toLowerCase()+".csv");
            file.writeDataOnCsv(releases);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
