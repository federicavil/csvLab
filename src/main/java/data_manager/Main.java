package data_manager;

import csv.CsvCreator;
import model.Release;

import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class Main {

    private static final String PROJECTNAME = "BOOKKEEPER";
    private static final String PROJECTLOCATION = "C:/Users/Federica/git/bookkeeper_ml";

    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
        Double proportion = Proportion.coldStart();
        //Double proportion = 0.0;
        ProjectManager manager = new ProjectManager(PROJECTNAME, PROJECTLOCATION);
        // Prendo i dati sulla bugginess delle classi in ogni release
        List<Release> releases = manager.getBugginess(proportion);
        if(releases == null){
            System.out.println("PROBLEMA");
        }
        System.out.println("Calcolata bugginess");
        // Calcolo le features delle classi
        Instant start = Instant.now();
        releases = manager.getFeatures();
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
