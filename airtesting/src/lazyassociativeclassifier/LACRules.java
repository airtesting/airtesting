package lazyassociativeclassifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import weka.core.Instance;
import weka.core.Instances;

/**
 * Manages the rules contained in the training data.
 * 
 * @author Gesse Dafe (Java implementation)
 * @author Adriano Veloso (algorithm and original C++ implementation)
 */
public class LACRules implements Serializable
{
        private static final long serialVersionUID = 5153978224002423432L;

        private final int maxRuleSize;
        private LACRulesCache cache = new LACRulesCache();
        private Instances trainingSet;
        private DataSetIndex trainingIndex;
        private double minSupport;
        private double minConfidence;
        
        /**
         * Restricted access constructor
         * @param training
         * @param trainingIndex
         * @param maxRuleSize
         * @param minSupport
         * @param minConfidence
         * @param outFile 
         * @throws Exception 
         */
        LACRules(Instances training, DataSetIndex trainingIndex, int maxRuleSize, double minSupport, double minConfidence) throws Exception
        {
                this.trainingSet = training;
                this.trainingIndex = trainingIndex;
                this.maxRuleSize = maxRuleSize;
                this.minSupport = minSupport;
                this.minConfidence = minConfidence;
        }

        /**
         * Gets the probability of the given test instance belonging to each class.
         * 
         * @param testInstance
         * @throws Exception 
         */
        double[] calculateProbabilities(Instance testInstance) throws Exception
        {
                double[] probs;
                double[] scores = calculateScores(testInstance);
                
                if(scores != null)
                {
                        probs = new double[scores.length];
                        double scoreSum = 0.0;
                        for (int i = 0; i < scores.length; i++)
                        {
                                scoreSum += scores[i];
                        }
                        
                        for (int i = 0; i < scores.length; i++)
                        {
                                probs[i] = scores[i] / scoreSum;
                        }
                }
                else
                {
                        int numClasses = trainingSet.classAttribute().numValues();
                        Enumeration<?> allClasses = trainingSet.classAttribute().enumerateValues();
                        probs = new double[numClasses];
                        while(allClasses.hasMoreElements())
                        {
                                String classValue = (String) allClasses.nextElement();
                                int classIndex = trainingSet.classAttribute().indexOfValue(classValue);
                                double count = trainingIndex.getInstancesOfClass(classIndex).size();
                                probs[classIndex] = (count / ((double) trainingSet.numInstances()));
                        }
                }

                return probs ;
        }

        /**
         * Calculates the scores for each class instance.
         * 
         * @param testInstance
         * @param currentClass
         * @return
         * @throws Exception 
         */
        private double[] calculateScores(Instance testInstance) throws Exception
        {
                List<Integer> testInstanceFeatures = new ArrayList<Integer>();
                testInstanceFeatures.addAll(trainingIndex.instanceFeaturesAsIntList(testInstance));
                Collections.sort(testInstanceFeatures);
                
                List<LACRule> allRulesForFeatures = new ArrayList<LACRule>(10000);
                int[] numPatterns = {0};
                for(int i = 0; i < testInstanceFeatures.size(); i++)
                {
                        List<Integer> featCombination = new ArrayList<Integer>();
                        featCombination.add(testInstanceFeatures.get(i));
                        extractRules(featCombination, testInstanceFeatures, allRulesForFeatures, numPatterns);
                }
                
                int numClasses = trainingSet.classAttribute().numValues();
                double[] scores = new double[numClasses];
                int numRules = allRulesForFeatures.size();
                if (numRules > 0)
                {
                        for (int i = 0; i < numRules; i++)
                        {
                                LACRule rule = allRulesForFeatures.get(i);
                                scores[rule.getPredictedClass()] = scores[rule.getPredictedClass()] + rule.getConfidence();
                        }
                }
                else
                {
                        scores = null;
                }
                
                return scores;
        }
        
        /**
         * Recursively generates all subsets of an array
         * @param numPatterns 
         * 
         * @param set
         * @param maxSubsetSize
         * @param subset
         * @param result
         * @return
         */
        private void extractRules(List<Integer> pattern, List<Integer> testFeatures, List<LACRule> extractedRules, int[] numPatterns)
        {
                numPatterns[0]++;
                List<LACRule> rules = getRules(pattern);
                        
                if(rules != null && rules.size() > 0)
                {
                        extractedRules.addAll(rules);
                        
                        if(pattern.size() < maxRuleSize)
                        {
                                List<List<Integer>> combinations = new ArrayList<List<Integer>>();
                                int size = testFeatures.size();
                                for (int i = 0; i < size; i++)
                                {
                                        int element = testFeatures.get(i);
                                        if (mustAddElement(element, pattern))
                                        {
                                                List<Integer> newFeatCombination = new ArrayList<Integer>(pattern.size() + 1);
                                                newFeatCombination.addAll(pattern);
                                                newFeatCombination.add(element);
                                                combinations.add(newFeatCombination);
                                        }
                                }
                                
                                int numCombinations = combinations.size();
                                for(int i = 0; i < numCombinations; i++)
                                {
                                        extractRules(combinations.get(i), testFeatures, extractedRules, numPatterns);
                                }
                        }
                }
        }
        
        private List<LACRule> getRules(List<Integer> featuresCombination)
        {
                List<LACRule> rulesForFeatures = cache.getRules(featuresCombination);

                if (rulesForFeatures == null)
                {
                        rulesForFeatures = doExtractRules(featuresCombination);
                        cache.storeRules(featuresCombination, rulesForFeatures);
                }
                
                return rulesForFeatures;
        }

        /**
         * Returns true is an element must be stored in the list
         * 
         * @param element
         * @param featuresCombination
         */
        private boolean mustAddElement(int element, List<Integer> featuresCombination)
        {
                return featuresCombination.size() < maxRuleSize && featuresCombination.get(featuresCombination.size() - 1) < element;
        }

        /**
         * Extracts the applicable rules for the given combination of features.
         * 
         * @param featuresCombination
         */
        private List<LACRule> doExtractRules(List<Integer> featuresCombination)
        {
                List<LACRule> rules = new ArrayList<LACRule>();

                List<Integer> instancesWithFeatures = trainingIndex.getInstancesWithFeatures(featuresCombination);
                int numClasses = trainingSet.classAttribute().numValues();
                int[] count = new int[numClasses];

                int size = instancesWithFeatures.size();

                if (size > 0)
                {
                        for (int i = 0; i < size; i++)
                        {
                                Integer instanceIndex = instancesWithFeatures.get(i);
                                Instance instance = trainingSet.get(instanceIndex);
                                int predictedClass = (int) instance.classValue();
                                count[predictedClass] = count[predictedClass] + 1;
                        }

                        for (int i = 0; i < numClasses; i++)
                        {
                                int classCount = count[i];
                                if (classCount > 0)
                                {
                                        double support = (double) classCount / (double) trainingSet.numInstances();
                                        double confidence = (double) classCount / (double) size;

                                        if(support > minSupport && confidence > minConfidence)
                                        {
                                                LACRule rule = new LACRule(support, confidence, i);
                                                rules.add(rule);
                                        }
                                }
                        }
                }

                return rules;
        }
}

