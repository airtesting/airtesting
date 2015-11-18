package lazyassociativeclassifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import weka.core.Instance;
import weka.core.Instances;

/**
 * A simple way to store and lookup instances that have a given combination of
 * features. For each feature on training set, we store an entry on a
 * {@link Map}. <br/>
 * This entry is a set of integers. Each integer is the index of an {@link Instance}
 * that contains those features.
 * 
 * @author Adriano Veloso (algorithm and original C++ implementation)
 * @author Gesse Dafe (Java implementation)
 */
public class FeatureOccurrences implements Serializable
{
	private static final long serialVersionUID = -3942414672290094335L;

	private Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
	private final LRU<List<Integer>, List<Integer>> cache = new LRU<List<Integer>, List<Integer>>(10000);

	/**
	 * Creates the map of features for all entries in {@link Instances}.
	 * 
	 * @param instances
	 */
	void createMap(Instances instances)
	{
		Map<Integer, Set<Integer>> tempMap = new HashMap<Integer, Set<Integer>>();
		
		int numInstances = instances.numInstances();
		
		for (int currentPosition = 0; currentPosition < numInstances; currentPosition++)
		{
			Instance currentInstance = instances.get(currentPosition);
			List<Integer> indexedFeatures = instanceFeaturesAsIntList(currentInstance);
			int size = indexedFeatures.size();
			for (int currentFeaturePosition = 0; currentFeaturePosition < size; currentFeaturePosition++)
			{
				int currentIndexedFeature = indexedFeatures.get(currentFeaturePosition);
				Set<Integer> instancesContainingFeature = tempMap.get(currentIndexedFeature);
				if (instancesContainingFeature == null)
				{
					instancesContainingFeature = new TreeSet<Integer>();
					tempMap.put(currentIndexedFeature, instancesContainingFeature);
				}
				instancesContainingFeature.add(currentPosition);
			}
		}
		
		for(Integer key : tempMap.keySet())
		{
			Set<Integer> set = tempMap.get(key);
			if(set != null)
			{
				List<Integer> list = new ArrayList<Integer>(set.size());
				list.addAll(set);
				map.put(key, list);
			}
		}
	}

	@Override
	public String toString()
	{
		return map.toString();
	}

	/**
	 * Gets all instances which contain all given features
	 * 
	 * @param featuresIndexes
	 * @return
	 */
	public List<Integer> instancesWithFeatures(List<Integer> featuresIndexes)
	{
		List<Integer> instances = cache.get(featuresIndexes);

		if (instances == null)
		{
			if (featuresIndexes.size() == 1)
			{
				instances = map.get(featuresIndexes.get(0));
			}
			else
			{
				instances = new ArrayList<Integer>();

				Integer feature = featuresIndexes.get(0);
				List<Integer> instancesForCurrFeat = map.get(feature);

				if (instancesForCurrFeat == null || instancesForCurrFeat.size() == 0)
				{
					return Collections.emptyList();
				}
				else
				{
					List<Integer> otherfeatures = new ArrayList<Integer>();
					otherfeatures.addAll(featuresIndexes);
					otherfeatures.remove(0);

					instances.addAll(instancesForCurrFeat);
					List<Integer> rest = instancesWithFeatures(otherfeatures);
					if(rest != null)
					{
						instances = calculateIntersection(instances, rest);
						cache.put(featuresIndexes, instances);
					}
					else
					{
						List<Integer> empty = Collections.emptyList();
						cache.put(featuresIndexes, empty);
					}
				}
			}
		}

		if(instances == null)
		{
			instances = Collections.emptyList();
		}
		
		return instances;
	}

	/**
	 * Calculates the intersection one two lists sorted in ascending order. Both
	 * lists must not have duplicated elements.
	 * 
	 * @param oneList
	 * @param otherList
	 */
	private List<Integer> calculateIntersection(List<Integer> oneList, List<Integer> otherList)
	{
		List<Integer> intersection = new ArrayList<Integer>();

		int oneIndex = 0;
		int oneSize = oneList.size();

		int otherIndex = 0;
		int otherSize = otherList.size();

		while (oneIndex < oneSize && otherIndex < otherSize)
		{
			int oneElem = oneList.get(oneIndex);
			int otherElem = otherList.get(otherIndex);

			if (oneElem == otherElem)
			{
				intersection.add(oneElem);
				oneIndex++;
				otherIndex++;
			}
			else if (oneElem < otherElem)
			{
				oneIndex = linearSearch(otherElem, oneIndex, oneSize - 1, oneList);
			}
			else
			{
				otherIndex = linearSearch(oneElem, otherIndex, otherSize - 1, otherList);
			}
		}

		return intersection;
	}

	/**
	 * Executes a linear search for the given element on the list
	 * 
	 * @param elem
	 * @param start
	 * @param end
	 * @param list
	 * @return the index of the element if it exists. Otherwise, returns the
	 *         index of the first greater element.
	 */
	private int linearSearch(int elem, int start, int end, List<Integer> list)
	{
		int last = list.get(end);

		if (elem > last)
		{
			return end + 1;
		}
		else
		{
			int size = list.size();
			while (start < size && list.get(start) < elem)
			{
				start++;
			}
			return start;
		}
	}
	
	public List<Integer> instanceFeaturesAsIntList(Instance instance)
	{
		List<Integer> feats = new ArrayList<Integer>();
		
		int numAtts = instance.numAttributes();
		for(int i = 0; i < numAtts; i++)
		{
			int featIndex = (int) instance.value(i);
			boolean missing = instance.isMissing(i);
			boolean isClass = i == instance.classIndex();
			
			if(!isClass && !missing)
			{
				feats.add(featIndex);
			}
		}
		
		return feats;
	}
}