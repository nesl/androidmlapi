package edu.ucla.nesl.mca.classifier;

import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.ucla.nesl.mca.feature.Feature;
import edu.ucla.nesl.mca.feature.FeaturePool;
import edu.ucla.nesl.mca.xdr.XDRSerializable;

public abstract class Classifier implements XDRSerializable{
    // Currently only support multiple in, single out
    protected ArrayList<Feature> m_inputs = null;
    protected Feature m_output = null;
    
    public void getMiningSchema(Element model, FeaturePool featurePool) 
            throws Exception {
        NodeList fieldList = model.getElementsByTagName("MiningField");
        for (int i = 0; i < fieldList.getLength(); i++) {
            Node miningField = fieldList.item(i);
            if (miningField.getNodeType() == Node.ELEMENT_NODE) {
                Element miningFieldEl = (Element)miningField;
                String fieldName = miningFieldEl.getAttribute("name");
                String usage = miningFieldEl.getAttribute("usageType");

                if (usage.compareTo("active") == 0 ||
                    usage.compareTo("predicted") == 0) {
                    Feature feature;
                    try {
                        feature = featurePool.get(fieldName);
                    } catch (IllegalArgumentException e) {
                        throw new Exception("Can't find mining field: " + fieldName 
                                + " in the data dictionary.");
                    }
                    
                    if (usage.compareTo("active") == 0) {
                        m_inputs.add(feature);
                    }
                    else { // predicted
                        m_output = feature;
                    }
                }
            }
        }
    }

    public abstract void getModel(Element modelEl, FeaturePool m_featurePool) throws Exception;
}
