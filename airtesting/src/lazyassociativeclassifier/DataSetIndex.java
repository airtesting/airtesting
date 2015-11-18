package lazyassociativeclassifier;

import java.io.Serializable;
import java.util.List;

import weka.core.Instance;
import weka.core.Instances;

public class DataSetIndex implements Serializable
{
        private static final long serialVersionUID = 2080451725087652107L;

        private ClassOccurrences classOccurrences;
        private FeatureOccurrences featureOccurrences;

        public DataSetIndex(Instances data)
        {
                classOccurrences = new ClassOccurrences();
                classOccurrences.createMap(data);
                
                featureOccurrences = new FeatureOccurrences();
                featureOccurrences.createMap(data);
        }

        public List<Integer> getInstancesOfClass(int classIndex)
        {
                return classOccurrences.getInstancesOfClass(classIndex);
        }

        public List<Integer> getInstancesWithFeatures(List<Integer> featuresCombination)
        {
                return featureOccurrences.instancesWithFeatures(featuresCombination);
        }

        public List<Integer> instanceFeaturesAsIntList(Instance instance)
        {
                return featureOccurrences.instanceFeaturesAsIntList(instance);
        }
}
