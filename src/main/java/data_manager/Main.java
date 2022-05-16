package data_manager;

import csv.CsvCreator;
import model.Release;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class Main {

    private static final String PROJECTNAME = "SYNCOPE";
    private static final String PROJECTLOCATION = "C:/Users/Federica/git/syncope_ml";

    public static void main(String[] args) throws IOException, ParseException {
        Double proportion = Proportion.coldStart();
        //Double proportion = 0.0;
        ProjectManager manager = new ProjectManager(PROJECTNAME, PROJECTLOCATION);
        // Prendo i dati sulla bugginess delle classi in ogni release
        List<Release> releases = manager.getBugginess(proportion);
        if(releases == null){
            System.out.println("PROBLEMA");
        }
        try {
            //Creo il file csv
            CsvCreator file = new CsvCreator("bugginess_"+PROJECTNAME.toLowerCase()+".csv",new String[]{"Release","Class","Bugginess"});
            file.writeDataOnCsv(releases);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
