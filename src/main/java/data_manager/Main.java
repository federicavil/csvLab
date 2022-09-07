package data_manager;

import file_manager.CsvCreator;
import model.Release;
import weka.Classificator;
import weka.Classificators;
import weka.SamplingType;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class Main {

    private static final String PROJECTNAME = "BOOKKEEPER";
    private static final String PROJECTLOCATION = "C:/Users/Federica/git/bookkeeper_ml";

    public static void main(String[] args) throws IOException, ParseException {
        // Calcolo il valore di proportion tramite cold start
        Double proportion = Proportion.coldStart(PROJECTNAME);
        ProjectManager manager = new ProjectManager(PROJECTNAME, PROJECTLOCATION);
        // Prendo i dati sulla bugginess delle classi in ogni release
        List<Release> releases = manager.getBugginess(proportion);

        if(releases.isEmpty()){
            System.out.println("PROBLEMA");
        }
        try {
            //Creo il file csv
            CsvCreator dataFile = new CsvCreator("bugginess_"+PROJECTNAME.toLowerCase()+".csv",new String[]{"Release","Class","LOC","NR","NAuth",
                    "Age","ChgSetSize","MAX_ChgSetSize","AVG_ChgSetSize",
                    "Churn","MAX_Churn","AVG_Churn","Bugginess"});
            dataFile.writeDataOnCsv(releases);

            CsvCreator metricsFile = new CsvCreator("weka_"+PROJECTNAME.toLowerCase()+".csv",
                    new String[]{"Dataset","Classifier","#TrainingRelease","%Training", "balancing","featureSelection",
                            "Sensitivity","TP","FP","TN","FN","Precision","Recall","Kappa","AUC"});

            List<String[]> results;
            String[] technics;

            //Validation without feature selection and without sampling
            technics = new String[]{"no","no selection","no"};
            Classificator naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME, releases, false, false,null);
            metricsFile.writeDataOnCsv(PROJECTNAME, "NaiveBayes",technics,results);

            Classificator ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME, releases, false, false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, "IBk",technics,results);

            Classificator randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, false,false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, "RandomForest",technics,results);

            //Validation with feature selection and without sampling
            technics = new String[]{"no","Backward search","no"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, false,true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, "NaiveBayes",technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, false, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, "IBk",technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, false, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, "RandomForest",technics,results);

            //Validation without feature selection and with undersampling
            technics = new String[]{"no","no","undersampling"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, false, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "NaiveBayes",technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, false, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "IBk",technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, false, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "RandomForest",technics,results);

            //Validation without feature selection and with oversampling
            technics = new String[]{"no","no","oversampling"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, false, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "NaiveBayes",technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, false, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "IBk",technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, false, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "RandomForest",technics,results);

            //Validation with cost sensitivity (CFN = 10*CFP)
            technics = new String[]{"cost sensitivity","no","no"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, true, false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, "NaiveBayes",technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, true, false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, "IBk",technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, true, false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, "RandomForest",technics,results);
            System.out.println("validation smote");

            //Validation with feature selection and with undersampling
            technics = new String[]{"no","Backward search","undersampling"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, false, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "NaiveBayes",technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, false, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "IBk",technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, false, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "RandomForest",technics,results);

            //Validation with feature selection and with oversampling
            technics = new String[]{"no","Backward search","oversampling"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, false, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "NaiveBayes",technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, false, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "IBk",technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, false, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "RandomForest",technics,results);

            //Validation with feature selection and cost sensitivity (CFN = 10*CFP)
            technics = new String[]{"cost sensitivity","Backward search","no"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, true, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, "NaiveBayes",technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, true, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, "IBk",technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, true, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, "RandomForest",technics,results);

            //Validation with oversampling and cost sensitivity (CFN = 10*CFP)
            technics = new String[]{"cost sensitivity","no","oversampling"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, true, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "NaiveBayes",technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, true, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "IBk",technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, true, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "RandomForest",technics,results);

            //Validation with undersampling and cost sensitivity (CFN = 10*CFP)
            technics = new String[]{"cost sensitivity","no","undersampling"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, true, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "NaiveBayes",technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, true, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "IBk",technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, true, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "RandomForest",technics,results);

            //Validation with feature selection with oversampling and cost sensitivity (CFN = 10*CFP)
            technics = new String[]{"cost sensitivity","Backward search","oversampling"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, true, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "NaiveBayes",technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, true, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "IBk",technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, true, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, "RandomForest",technics,results);

            metricsFile.closeFile();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
