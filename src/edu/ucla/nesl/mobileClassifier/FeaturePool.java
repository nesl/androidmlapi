package edu.ucla.nesl.mobileClassifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import edu.ucla.nesl.xdr.XDRDataInput;
import edu.ucla.nesl.xdr.XDRDataOutput;
import edu.ucla.nesl.xdr.XDRSerializable;

public class FeaturePool implements XDRSerializable{
    /**
     * TODO: feature pool requires synchronization between server and client
     * Once client update feature set, need to retrain
     */
    private ArrayList<Feature> m_features = new ArrayList<Feature>();
    private HashMap<String, Integer> m_nameMap = new HashMap<String, Integer>();
    
    // Delete Feature may cause nameMap index error, not implemented
    
    public Feature get(int index) throws IndexOutOfBoundsException {
        return m_features.get(index);
    }
    
    public Feature get(String featureName) throws IllegalArgumentException{
        int index = getFeatureIndex(featureName);
        if (index == -1)
            throw new IllegalArgumentException();
        else
            return get(index);
    }
    
    public int size() {
        return m_features.size();
    }

    /**
     * Add newFeature to the pool
     *
     * @param newFeature the Feature to be added
     * @return the index of the feature, if the feature already exists
     *         return its index if both same, return -1 if different
     * @throws Exception if the model type is unsupported/unknown
     */
    // 
    // Return the index on success or exists
    public int addFeature(Feature newFeature) {
        int index = getFeatureIndex(newFeature.name);
        
        if (index == -1) {
            m_features.add(newFeature);
            index = m_features.size() - 1;
            m_nameMap.put(newFeature.name, index);
            return index;
        }
        else {
            Feature oldFeature = get(index);
            if (newFeature == oldFeature)
                return index;
            else
                return -1;
        }

    }
    
    public int getFeatureIndex(String featureName) {
        Integer index = m_nameMap.get(featureName);
        if (index == null)
            return -1;
        else
            return index.intValue();
    }

    @Override
    public void writeXDR(XDRDataOutput out) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void readXDR(XDRDataInput in) throws IOException {
        // TODO Auto-generated method stub
        
    }
}
