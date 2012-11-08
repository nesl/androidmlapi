package edu.ucla.nesl.mobileClassifier;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import weka.classifiers.pmml.consumer.GeneralRegression;
import weka.classifiers.pmml.consumer.NeuralNetwork;
import weka.classifiers.pmml.consumer.Regression;
import weka.classifiers.pmml.consumer.RuleSetModel;
import weka.classifiers.pmml.consumer.SupportVectorMachineModel;
import weka.classifiers.pmml.consumer.TreeModel;
import weka.core.Instances;
import weka.core.pmml.MiningSchema;
import weka.core.pmml.PMMLModel;
import weka.core.pmml.PMMLFactory.ModelType;

public class PMMLConverter {

    protected enum ModelType {
      UNKNOWN_MODEL ("unknown"),
      REGRESSION_MODEL ("Regression"),
      GENERAL_REGRESSION_MODEL ("GeneralRegression"),
      NEURAL_NETWORK_MODEL ("NeuralNetwork"),
      TREE_MODEL ("TreeModel"),
      RULESET_MODEL("RuleSetModel"),
      SVM_MODEL ("SupportVectorMachineModel");
      
      private final String m_stringVal;
      
      ModelType(String name) {
        m_stringVal = name;
      }
      
      public String toString() {
        return m_stringVal;
      }
    }
    
    private FeaturePool m_featurePool;
    
    public PMMLConverter(FeaturePool featurePool)
    {
        m_featurePool = featurePool;
    }

    /**
     * Get the type of model
     *
     * @param doc the Document encapsulating the PMML
     * @return the type of model
     */
    protected static ModelType getModelType(Document doc) {
      NodeList temp = doc.getElementsByTagName("RegressionModel");
      if (temp.getLength() > 0) {
        //return ModelType.REGRESSION_MODEL;
      }

      temp = doc.getElementsByTagName("GeneralRegressionModel");
      if (temp.getLength() > 0) {
        //return ModelType.GENERAL_REGRESSION_MODEL;
      }
      
      temp = doc.getElementsByTagName("NeuralNetwork");
      if (temp.getLength() > 0) {
        //return ModelType.NEURAL_NETWORK_MODEL;
      }
      
      temp = doc.getElementsByTagName("TreeModel");
      if (temp.getLength() > 0) {
        return ModelType.TREE_MODEL;
      }
      
      temp = doc.getElementsByTagName("RuleSetModel");
      if (temp.getLength() > 0) {
        //return ModelType.RULESET_MODEL;
      }
      
      temp = doc.getElementsByTagName("SupportVectorMachineModel");
      if (temp.getLength() > 0) {
        //return ModelType.SVM_MODEL;
      }

      return ModelType.UNKNOWN_MODEL;
    }

    /**
     * Get the Element that contains the PMML model
     *
     * @param doc the Document encapsulating the PMML
     * @param modelType the type of model
     * @throws Exception if the model type is unsupported/unknown
     */
    protected static Element getModelElement(Document doc, ModelType modelType) 
      throws Exception {
      NodeList temp = null;
      Element model = null;
      switch (modelType) {
      case REGRESSION_MODEL:
        temp = doc.getElementsByTagName("RegressionModel");
        break;
      case GENERAL_REGRESSION_MODEL:
        temp = doc.getElementsByTagName("GeneralRegressionModel");
        break;
      case NEURAL_NETWORK_MODEL:
        temp = doc.getElementsByTagName("NeuralNetwork");
        break;
      case TREE_MODEL:
        temp = doc.getElementsByTagName("TreeModel");
        break;
      case RULESET_MODEL:
        temp = doc.getElementsByTagName("RuleSetModel");
        break;
      case SVM_MODEL:
        temp = doc.getElementsByTagName("SupportVectorMachineModel");
        break;
      default:
        throw new Exception("[PMMLFactory] unknown/unsupported model type.");
      }

      if (temp != null && temp.getLength() > 0) {
        Node modelNode = temp.item(0);
        if (modelNode.getNodeType() == Node.ELEMENT_NODE) {
          model = (Element)modelNode;
        }
      }

      return model;
    }

    protected Classifier getModel(ModelType modelType, Element modelEl) 
            throws Exception {
        
        Classifier model = null;
        switch (modelType) {
        case REGRESSION_MODEL:
            //pmmlModel = new Regression(model, dataDictionary, miningSchema);
            //System.out.println(pmmlM);
            break;
        case GENERAL_REGRESSION_MODEL:
            //pmmlModel = new GeneralRegression(model, dataDictionary, miningSchema);
            //System.out.println(pmmlM);
            break;
        case NEURAL_NETWORK_MODEL:
            //pmmlModel = new NeuralNetwork(model, dataDictionary, miningSchema);
            break;
        case TREE_MODEL:
            model = new DecisionTree();
            break;
        case RULESET_MODEL:
            //pmmlModel = new RuleSetModel(model, dataDictionary, miningSchema);
            break;
        case SVM_MODEL:
            //pmmlModel = new SupportVectorMachineModel(model, dataDictionary, miningSchema);
            break;
        default:
            throw new Exception("Unknown model type!!");
        }
        
        // Get MiningSchema (Output implied)
        model.getMiningSchema(modelEl, m_featurePool);
        
        // Get model structure
        model.getModel(modelEl, m_featurePool);
      
        return model;
    }
    
    private static boolean isPMML(Document doc) {
        NodeList tempL = doc.getElementsByTagName("PMML");
        if (tempL.getLength() == 0) {
            return false;
        }
        
        return true;
    }
        
    public Classifier getModelFromStream(InputStream stream) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(stream);
        stream.close();
        doc.getDocumentElement().normalize();
        
        if (!isPMML(doc)) {
            throw new IllegalArgumentException("Source is not a PMML file!!");
        }
        
        // Get DataDictionary (feature list)
        getFeatureList();
        
        // Get model type
        ModelType modelType = getModelType(doc);
        if (modelType == ModelType.UNKNOWN_MODEL) {
          throw new Exception("Unsupported PMML model type");
        }
        Element modelEl = getModelElement(doc, modelType);

        return getModel(modelType, modelEl);
    }
    
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
