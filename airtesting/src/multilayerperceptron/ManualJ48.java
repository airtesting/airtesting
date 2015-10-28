package multilayerperceptron;
import java.io.FileReader;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.j48.C45ModelSelection;
import weka.classifiers.trees.j48.C45PruneableClassifierTree;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;

public class ManualJ48 implements Classifier {

	private C45PruneableClassifierTree c45tree;

	// This method must be written since we extends the abstract
	// class Classifier.
	public void buildClassifier(Instances instances) throws Exception {
		// Create a model selection for the tree (allowing the creation of
		// subsets
		// with at least three instances).
		C45ModelSelection model = new C45ModelSelection(3, instances, false, false);
		// Create the classifier and build the tree using those instances.
		// The tree will be unpruned (therefore the confidence factor will be
		// zero),
		// Subtree raising will not be performed (since tree will be unpruned).
		// What its cleanup data, anyway? Documentation is weak :-(
		c45tree = new C45PruneableClassifierTree(model, false, 0, false, true, false);
		c45tree.buildClassifier(instances);
	}

	// Prints the tree.
	public void printTree() {
		System.out.println(c45tree);
	}

	// We *must* implement this method otherwise the Evaluation instance will
	// freak out.
	public double classifyInstance(Instance instance) throws Exception {
		return c45tree.classifyInstance(instance);
	}

	// The entry point on the app.
	public static void main(String[] args) {
		try {
			
			// Read the instances from a file.
			FileReader reader = new FileReader("\\D:\\teste.arff");
			Instances instances = new Instances(reader);
			instances.setClassIndex(4);
			
			// Create the tree and classifier.
			ManualJ48 thisClassifier = new ManualJ48();
			thisClassifier.buildClassifier(instances);
			
			// Print the tree.
			thisClassifier.printTree();
			
			
			// Let’s evaluate the tree.
			Evaluation evaluation = new Evaluation(instances);
			evaluation.evaluateModel(thisClassifier, instances);
			
			// How many correct instances?
			System.out.print(evaluation.correct() + "/");
			// How many instances?
			System.out.println(instances.numInstances());
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public double[] distributionForInstance(Instance arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Capabilities getCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}
}