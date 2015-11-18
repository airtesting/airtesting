package lazyassociativeclassifier;

import java.io.Serializable;
import java.util.List;

/**
 * A cache to store the extracted associative rules
 * 
 * @author Adriano Veloso (algorithm and original C++ implementation)
 * @author Gesse Dafe (Java implementation)
 */
public class LACRulesCache implements Serializable
{
        private static final long serialVersionUID = -1340440155675141479L;

        private LRU<List<Integer>, List<LACRule>> rulesPerFeatures = new LRU<List<Integer>, List<LACRule>>(50000);

        /**
         * Gets all rules that are applicable to instances that have the given
         * combination of features.
         * 
         * @param featuresCombination
         */
        List<LACRule> getRules(List<Integer> featuresCombination)
        {
                return rulesPerFeatures.get(featuresCombination);
        }

        /**
         * Stores a list of rules that are applicable to a given combination of
         * features
         * 
         * @param featuresCombination
         * @param rulesForClass
         */
        public void storeRules(List<Integer> featureCombinations, List<LACRule> rulesForClass)
        {
                rulesPerFeatures.put(featureCombinations, rulesForClass);
        }
}
