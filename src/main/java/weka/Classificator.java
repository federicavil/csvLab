package weka;

import model.Release;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.lazy.IBk;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.converters.ConverterUtils.DataSource;


public class Classificator {

    private Classifier wekaClassifier;
    private static final String EXTENSION = ".arff";
    private double precision;
    private double recall;
    private double kappa;
    private double auc;

    public Classificator(Enum<Classificators> classificator){
        switch(classificator.toString()){
            case "RANDOMFOREST":
                this.wekaClassifier = new RandomForest();
                break;
            case "NAIVEBAYES":
                this.wekaClassifier = new NaiveBayes();
                break;
            case "IBK":
                this.wekaClassifier = new IBk();
                break;
            default:
                this.wekaClassifier = new ZeroR();
                break;
        }

        this.precision = 0.0;
        this.recall = 0.0;
        this.kappa = 0.0;
        this.auc = 0.0;
    }

    public List<String[]> walkForwardEvaluation(List<Release> releases) throws Exception {
        String trainingFile = "training-set";
        String testingFile = "testing-set";
        List<String[]> results = new ArrayList<>();
        for(int i = 1; i < releases.size(); i++){
            // Crea il training
            ArffCreator trainingSet = new ArffCreator(trainingFile+ EXTENSION, trainingFile);
            ArffCreator testingSet = new ArffCreator(testingFile+ EXTENSION, testingFile);
            String[] result = new String[4];
            trainingSet.writeData(releases.subList(0,i),true);
            testingSet.writeData(List.of(releases.get(i)),false);
            evaluate(trainingFile, testingFile, i);
            result[0] = String.valueOf(this.precision);
            result[1] = String.valueOf(this.recall);
            result[2] = String.valueOf(this.kappa);
            result[3] = String.valueOf(this.auc);
            results.add(result);
        }
        return results;
    }

    public void evaluate(String trainingFile, String testingFile, int i) throws Exception {
        DataSource source1 = new DataSource(trainingFile+ EXTENSION);
        Instances training = source1.getDataSet();
        DataSource source2 = new DataSource(testingFile+ EXTENSION);
        Instances testing = source2.getDataSet();
        int numAttr = training.numAttributes();
        training.setClassIndex(numAttr - 1);
        testing.setClassIndex(numAttr - 1);

        this.wekaClassifier.buildClassifier(training);

        Evaluation eval = new Evaluation(testing);

        eval.evaluateModel(this.wekaClassifier, testing);
        this.precision = (this.precision*(i-1) + eval.precision(1))/i;
        this.recall = (this.recall*(i-1) + eval.recall(1))/i;
        this.kappa = (this.kappa*(i-1) + eval.kappa())/i;
        this.auc = (this.auc*(i-1) + eval.areaUnderROC(1))/i;

    }

    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public double getKappa() {
        return kappa;
    }

    public double getAuc() {
        return auc;
    }
}
