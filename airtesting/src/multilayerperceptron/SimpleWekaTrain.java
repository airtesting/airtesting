package multilayerperceptron;
import java.io.FileReader;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;


public class SimpleWekaTrain {
	
	public void simpleWekaTrain(String filepath){
		try{
			//Reading training arff file
			FileReader trainreader = new FileReader(filepath);
			Instances train = new Instances(trainreader);
			train.setClassIndex(train.numAttributes()-1);

			//Instance of NN
			MultilayerPerceptron mlp = new MultilayerPerceptron();

			//Setting Parameters
			mlp.setGUI(false);
			mlp.setAutoBuild(true);
			mlp.setDebug(false);
			mlp.setDecay(false);
			mlp.setDoNotCheckCapabilities(false);
			mlp.setHiddenLayers("a");
			mlp.setLearningRate(0.3);
			mlp.setMomentum(0.2);
			mlp.setNominalToBinaryFilter(true);
			mlp.setNormalizeAttributes(true);
			mlp.setNormalizeNumericClass(true);
			mlp.setReset(true);
			mlp.setSeed(0);
			mlp.setTrainingTime(500);
			mlp.setValidationSetSize(0);
			mlp.setValidationThreshold(20);
			mlp.buildClassifier(train);
			
			Evaluation eval = new Evaluation(train);
			eval.evaluateModel(mlp, train);

			System.out.println(eval.errorRate()); //Printing Training Mean root squared Error
			System.out.println(eval.toSummaryString()); //Summary of Training
		
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	
	public static void main(String[] args){
		SimpleWekaTrain swt = new SimpleWekaTrain();
		swt.simpleWekaTrain("D:\\Users\\Administrador\\git\\airtesting\\airtesting\\file\\exemploMLP.arff");
	}
	
	
}
