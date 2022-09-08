package data_manager;

import file_manager.CsvCreator;
import model.Release;
import weka.Classificator;
import weka.Classificators;
import weka.CostSensitivityType;
import weka.SamplingType;

import java.util.logging.Level;
import java.util.logging.Logger;


import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class Main {

    private static final String PROJECTNAME = "SYNCOPE";
    private static final String PROJECTLOCATION = "C:/Users/Federica/git/syncope_ml";

    public static void main(String[] args) throws IOException, ParseException {
        // Calcolo il valore di proportion tramite cold start
        Double proportion = Proportion.coldStart(PROJECTNAME);
        ProjectManager manager = new ProjectManager(PROJECTNAME, PROJECTLOCATION);
        // Prendo i dati sulla bugginess delle classi in ogni release
        List<Release> releases = manager.getBugginess(proportion);

        if(releases.isEmpty()){
            Logger.getLogger("Logger").log(Level.SEVERE, "Error");
        }
        try {
            //Creo il file csv
            CsvCreator dataFile = new CsvCreator("bugginess_"+PROJECTNAME.toLowerCase()+".csv",new String[]{"Release","Class","LOC","NR","NAuth",
                    "Age","ChgSetSize","MAX_ChgSetSize","AVG_ChgSetSize",
                    "Churn","MAX_Churn","AVG_Churn","Bugginess"});
            dataFile.writeDataOnCsv(releases);

            CsvCreator metricsFile = new CsvCreator("weka_"+PROJECTNAME.toLowerCase()+".csv",
                    new String[]{"Dataset","Classifier","#TrainingRelease","%Training", "sensitivity","featureSelection",
                            "sampling","TP","FP","TN","FN","Precision","Recall","Kappa","AUC"});

            List<String[]> results;
            String[] technics;
            String backwardSearch = "Backward search";
            String oversampling = "oversampling";
            String undersampling = "undersampling";
            String sensitivity_learning = "sensitivity learning";
            String sensitivity_threshold = "sensitivity_threshold";
            Classificator naiveBayes;
            Classificator ibk;
            Classificator randomForest;

            //Validation without feature selection and without sampling
            technics = new String[]{"no","no selection","no"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME, releases, null, false,null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME, releases, null, false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, null,false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with feature selection and without sampling
            technics = new String[]{"no",backwardSearch,"no"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, null,true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, null, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, null, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation without feature selection and with undersampling
            technics = new String[]{"no","no",undersampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, null, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, null, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, null, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation without feature selection and with oversampling
            technics = new String[]{"no","no",oversampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, null, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, null, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, null, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with cost sensitivity learning (CFN = 10*CFP)
            technics = new String[]{sensitivity_learning,"no","no"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with cost sensitivity threshold
            technics = new String[]{sensitivity_learning,"no","no"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, false, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with feature selection and with undersampling
            technics = new String[]{"no",backwardSearch,undersampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, null, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, null, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, null, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with feature selection and with oversampling
            technics = new String[]{"no",backwardSearch,oversampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, null, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, null, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, null, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with feature selection and cost sensitivity learning(CFN = 10*CFP)
            technics = new String[]{sensitivity_learning,backwardSearch,"no"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with feature selection and cost sensitivity threshold
            technics = new String[]{sensitivity_learning,backwardSearch,"no"};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, true, null);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with oversampling and cost sensitivity learning(CFN = 10*CFP)
            technics = new String[]{sensitivity_learning,"no",oversampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with oversampling and cost sensitivity threshold
            technics = new String[]{sensitivity_threshold,"no",oversampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, false, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);


            //Validation with undersampling and cost sensitivity learning (CFN = 10*CFP)
            technics = new String[]{sensitivity_learning,"no",undersampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with undersampling and cost sensitivity threshold
            technics = new String[]{sensitivity_threshold,"no",undersampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, false, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);


            //Validation with feature selection with oversampling and cost sensitivity learning (CFN = 10*CFP)
            technics = new String[]{sensitivity_learning,backwardSearch,oversampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with feature selection with oversampling and cost sensitivity threshold
            technics = new String[]{sensitivity_threshold,backwardSearch,oversampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, true, SamplingType.OVERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with feature selection with undersampling and cost sensitivity learning(CFN = 10*CFP)
            technics = new String[]{sensitivity_learning,backwardSearch,undersampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_LEARNING, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);

            //Validation with feature selection with undersampling and cost sensitivity threshold
            technics = new String[]{sensitivity_threshold,backwardSearch,undersampling};
            naiveBayes = new Classificator(Classificators.NAIVEBAYES);
            results = naiveBayes.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.NAIVEBAYES,technics,results);

            ibk = new Classificator(Classificators.IBK);
            results = ibk.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.IBK,technics,results);

            randomForest = new Classificator(Classificators.RANDOMFOREST);
            results = randomForest.walkForwardEvaluation(PROJECTNAME,releases, CostSensitivityType.SENSITIVITY_THRESHOLD, true, SamplingType.UNDERSAMPLING);
            metricsFile.writeDataOnCsv(PROJECTNAME, Classificators.RANDOMFOREST,technics,results);


            metricsFile.closeFile();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
