package lazyassociativeclassifier;

import java.io.Serializable;

/**
 * An associative rule extracted from the training data
 * 
 * @author Gesse Dafe (Java implementation)
 * @author Adriano Veloso (algorithm and original C++ implementation)
 */
public class LACRule implements Serializable
{
        private static final long serialVersionUID = 925206930131844401L;

        private final double confidence;
        private final double support;
        private final int predictedClass;

        /**
         * Constructs a new {@link LACRule}
         * 
         * @param support
         * @param confidence
         * @param predictedClass
         */
        LACRule(double support, double confidence, int predictedClass)
        {
                this.support = support;
                this.confidence = confidence;
                this.predictedClass = predictedClass;
        }

        /**
         * @return the support
         */
        double getSupport()
        {
                return support;
        }

        /**
         * @return the confidence
         */
        double getConfidence()
        {
                return confidence;
        }

        /**
         * @return the predictedClass
         */
        int getPredictedClass()
        {
                return predictedClass;
        }
}

