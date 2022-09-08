package weka;

import model.Release;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.CostMatrix;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.lazy.IBk;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SpreadSubsample;
import weka.filters.supervised.instance.SMOTE;


public class Classificator {

    private Classifier wekaClassifier;
    private static final String EXTENSION = ".arff";

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

    }

    public List<String[]> walkForwardEvaluation(String project, List<Release> releases, CostSensitivityType costSensitivity, boolean featureSelection, SamplingType sampling) throws Exception {
        String trainingFile = "training-set-"+project;
        String testingFile = "testing-set-"+project;
        List<String[]> results = new ArrayList<>();
        for(int i = 1; i < releases.size(); i++){
            // Crea il training
            ArffCreator trainingSet = new ArffCreator(trainingFile+ EXTENSION, trainingFile);
            ArffCreator testingSet = new ArffCreator(testingFile+ EXTENSION, testingFile);

            String[] result = new String[8];
            trainingSet.writeData(releases.subList(0,i),true);
            testingSet.writeData(List.of(releases.get(i)),false);

            Evaluation eval = evaluate(trainingFile, testingFile, costSensitivity, featureSelection, sampling);

            result[0] = String.valueOf(eval.numTruePositives(0));
            result[1] = String.valueOf(eval.numFalsePositives(0));
            result[2] = String.valueOf(eval.numTrueNegatives(0));
            result[3] = String.valueOf(eval.numFalseNegatives(0));
            result[4] = String.valueOf(eval.precision(0));
            result[5] = String.valueOf(eval.recall(0));
            result[6] = String.valueOf(eval.kappa());
            result[7] = String.valueOf(eval.areaUnderROC(0));

            results.add(result);
        }
        return results;
    }

    public Evaluation evaluate(String trainingFile, String testingFile, CostSensitivityType costSensitivity, boolean featureSelection, SamplingType sampling) throws Exception {
        DataSource source1 = new DataSource(trainingFile+ EXTENSION);
        Instances training = source1.getDataSet();
        DataSource source2 = new DataSource(testingFile+ EXTENSION);
        Instances testing = source2.getDataSet();

        if(featureSelection){
            //create AttributeSelection object
            AttributeSelection filter = new AttributeSelection();
            //create evaluator and search algorithm objects
            CfsSubsetEval eval = new CfsSubsetEval();
            GreedyStepwise search = new GreedyStepwise();
            //set the algorithm to search backward
            search.setSearchBackwards(true);
            filter.setEvaluator(eval);
            filter.setSearch(search);
            //specify the dataset
            filter.setInputFormat(training);
            //apply
            training = Filter.useFilter(training, filter);
            testing= Filter.useFilter(testing, filter);

        }

        int numAttr = training.numAttributes();
        training.setClassIndex(numAttr - 1);
        testing.setClassIndex(numAttr - 1);
        this.wekaClassifier.buildClassifier(training);

        if(sampling != null) {
            FilteredClassifier fc = new FilteredClassifier();
            fc.setClassifier(this.wekaClassifier);
            Filter filter = null;
            switch (sampling) {
                case UNDERSAMPLING -> {
                    filter = new SpreadSubsample();
                    String[] opts = new String[]{"-M", "1.0"};
                    filter.setOptions(opts);
                }
                case OVERSAMPLING -> {
                    filter = new Resample();
                    filter.setOptions(new String[]{"-B", "1.0", "-Z", "130.3"});
                    filter.setInputFormat(training);
                }
                case SMOTE -> {
                    filter = new SMOTE();
                    filter.setInputFormat(training);

                }
            }
            fc.setFilter(filter);
            fc.buildClassifier(training);
            this.wekaClassifier = fc;
        }

        if(costSensitivity != null){
            CostSensitiveClassifier classifier = new CostSensitiveClassifier();
            CostMatrix matrix = new CostMatrix(2);
            if(costSensitivity == CostSensitivityType.SENSITIVITY_LEARNING) {
                matrix.setCell(0, 1, 1.0);
                matrix.setCell(1, 0, 10.0);
                classifier.setCostMatrix(matrix);
                classifier.setMinimizeExpectedCost(false);
            }
            else{
                matrix.setCell(0, 1, 1.0);
                matrix.setCell(1, 0, 1.0);
                classifier.setCostMatrix(matrix);
                classifier.setMinimizeExpectedCost(true);
            }

            classifier.setClassifier(this.wekaClassifier);
            classifier.buildClassifier(training);
            this.wekaClassifier = classifier;
        }
        Evaluation eval = new Evaluation(testing);
        eval.evaluateModel(this.wekaClassifier, testing);

        return eval;
    }

}
