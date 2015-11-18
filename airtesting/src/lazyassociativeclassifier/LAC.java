package lazyassociativeclassifier;

/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
import java.util.Enumeration;
import java.util.Vector;


import weka.classifiers.AbstractClassifier;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;

/**
 * Implements the Lazy Associative Classifier for Weka environment.
 * 
 * @author Gesse Dafe (Java implementation)
 * @author Adriano Veloso (algorithm and original C++ implementation)
 */
public class LAC extends AbstractClassifier implements TechnicalInformationHandler, OptionHandler
{
        private static final long serialVersionUID = 4740958383832856257L;

        private double minConfidence = 0;
        private double minSupport = 0;
        private int maxRuleSize = 4;

        private LACRules rules;
        
        @Override
        public void buildClassifier(Instances data) throws Exception
        {
                DataSetIndex datasetIndex = new DataSetIndex(data);
                this.rules = new LACRules(data, datasetIndex, maxRuleSize - 1, minSupport, minConfidence);
        }

        @Override
        public double[] distributionForInstance(Instance instance) throws Exception
        {
                return rules.calculateProbabilities(instance);
        }
        
        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public Enumeration listOptions()
        {
                Vector newVector = new Vector();
                
                newVector.addElement(
                        new Option("\tDetermines the maximum length of a classification rule (its number of features plus 1, because class attribute is also considered). Mining large rules is costly, but sometimes the accuracy gain is worth. The default value for this option is 4.", "M", 1, "-M")
                );
                newVector.addElement(
                        new Option("\tDetermines the support threshold for pruning (default: 0).", "S", 1, "-S")
                );
                newVector.addElement(
                        new Option("\tDetermines the confidence threshold for pruning (default: 0).", "C", 1, "-C")
                );
                
                return newVector.elements();
        }
        
        @Override
        public void setOptions(String[] options) throws Exception
        {
                super.setOptions(options);

            String opt = Utils.getOption('M', options);
            if(opt != null && opt.trim().length() > 0)
            {
                this.maxRuleSize = Integer.parseInt(opt);
            }
            
            opt = Utils.getOption('C', options);
            if(opt != null && opt.trim().length() > 0)
            {
                this.minConfidence = Double.parseDouble(opt);
            }
            
            opt = Utils.getOption('S', options);
            if(opt != null && opt.trim().length() > 0)
            {
                this.minSupport = Double.parseDouble(opt);
            }
        }

        @Override
        public String[] getOptions()
        {
                return new String[] {
                        "-M", "" + this.maxRuleSize, 
                        "-C", "" + this.minConfidence, 
                        "-S", "" + this.minSupport
                };
        }
        
        @Override
        public Capabilities getCapabilities()
        {
            Capabilities result = super.getCapabilities();
            result.disableAll();

            result.enable(Capability.STRING_ATTRIBUTES);
            result.enable(Capability.NOMINAL_ATTRIBUTES);
            result.enable(Capability.MISSING_VALUES);

            result.enable(Capability.STRING_CLASS);
            result.enable(Capability.NOMINAL_CLASS);

            result.setMinimumNumberInstances(0);
            
            return result;
        }
        
        @Override
        public TechnicalInformation getTechnicalInformation()
        {
                TechnicalInformation result = new TechnicalInformation(Type.ARTICLE);
                result.setValue(Field.AUTHOR, "Adriano Veloso and Wagner Meira Jr. and Mohammed Zaki");
                result.setValue(Field.YEAR, "2006");
                result.setValue(Field.TITLE, "Lazy Associative Classification");
                result.setValue(Field.JOURNAL, "ICDM '06 Proceedings of the Sixth International Conference on Data Mining");
                result.setValue(Field.PAGES, "645-654");
                result.setValue(Field.PUBLISHER, "IEEE Computer Society Washington, DC, USA");
                result.setValue(Field.ISBN, "0-7695-2701-9");
                return result;
        }

        public double getMinConfidence()
        {
                return minConfidence;
        }

        public void setMinConfidence(double minConfidence)
        {
                this.minConfidence = minConfidence;
        }

        public double getMinSupport()
        {
                return minSupport;
        }

        public void setMinSupport(double minSupport)
        {
                this.minSupport = minSupport;
        }

        public int getMaxRuleSize()
        {
                return maxRuleSize;
        }

        public void setMaxRuleSize(int maxRuleSize)
        {
                this.maxRuleSize = maxRuleSize;
        }
}