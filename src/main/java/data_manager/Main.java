package data_manager;

import file_manager.CsvCreator;
import model.Release;
import weka.Classificator;
import weka.Classificators;

import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class Main {

    private static final String PROJECTNAME = "BOOKKEEPER";
    private static final String PROJECTLOCATION = "C:/Users/Federica/git/bookkeeper_ml";

    public static void main(String[] args) throws IOException, ParseException {
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

        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: "+ timeElapsed.toSeconds() +" seconds");

        try {
            //Creo il file csv
            CsvCreator dataFile = new CsvCreator("bugginess_"+PROJECTNAME.toLowerCase()+".csv",new String[]{"Release","Class","LOC","NR","NAuth",
                    "Age","ChgSetSize","MAX_ChgSetSize","AVG_ChgSetSize",
                    "Churn","MAX_Churn","AVG_Churn","Bugginess"});
            dataFile.writeDataOnCsv(releases);

            CsvCreator metricsFile = new CsvCreator("weka_"+PROJECTNAME.toLowerCase()+".csv",new String[]{"Dataset","NTrainingRelease",
                        "Classifier","Precision","Recall","Kappa","AUC"});

            List<String[]> results;

            Classificator naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(releases);
            metricsFile.writeDataOnCsv(PROJECTNAME, "NaiveBayes",results);

            Classificator ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(releases);
            metricsFile.writeDataOnCsv(PROJECTNAME, "IBk",results);

            Classificator randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(releases);
            metricsFile.writeDataOnCsv(PROJECTNAME, "RandomForest",results);

            metricsFile.closeFile();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
