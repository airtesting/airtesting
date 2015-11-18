package lazyassociativeclassifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.core.Instance;
import weka.core.Instances;

/**
 * A simple way to store and lookup instances that belong to a given class.
 * For each class in training set, we store an entry on a
 * {@link Map}. <br/>
 * This entry is a set of integers. Each integer is the index of an {@link Instance}
 * that belongs to that class.
 * 
 * @author Adriano Veloso (algorithm and original C++ implementation)
 * @author Gesse Dafe (Java implementation)
 */
public class ClassOccurrences implements Serializable
{
        private static final long serialVersionUID = 4471128505943485021L;

        private Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
        
        /**
         * Creates the map of classes for all entries in {@link Instances}.
         * 
         * @param instances
         */
        void createMap(Instances instances)
        {
                int numInstances = instances.numInstances();
                
                for (int currentPosition = 0; currentPosition < numInstances; currentPosition++)
                {
                        Instance currentInstance = instances.get(currentPosition);
                        Integer clazz = (int) currentInstance.classValue();
                        if(clazz >= 0)
                        {
                                List<Integer> instancesByClass = map.get(clazz);
                                if (instancesByClass == null)
                                {
                                        instancesByClass = new ArrayList<Integer>();
                                        map.put(clazz, instancesByClass);
                                }
                                instancesByClass.add(currentPosition);
                        }
                }
        }
        
        /**
         * Returns the instances that belong to a given class
         * @param classIndex
         * @return
         */
        @SuppressWarnings("unchecked")
        List<Integer> getInstancesOfClass(int classIndex)
        {
                List<Integer> result = map.get(classIndex);
                return result != null ? result : Collections.EMPTY_LIST;
        }
}
