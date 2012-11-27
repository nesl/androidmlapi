package edu.ucla.nesl.mca.feature;

import java.io.IOException;
import java.util.HashMap;

import edu.ucla.nesl.mca.xdr.XDRDataInput;
import edu.ucla.nesl.mca.xdr.XDRDataOutput;
import edu.ucla.nesl.mca.xdr.XDRSerializable;

public class FeaturePool implements XDRSerializable{
    /**
     * TODO: feature pool requires synchronization between server and client
     * Once client update feature set, need to retrain
     */
    private HashMap<String, Integer> m_nameMap ;
    private HashMap<Integer, Feature> m_features;
    
    public FeaturePool() {
    	m_nameMap = new HashMap<String, Integer>();
    	m_features = new HashMap<Integer, Feature>();
    }
    
    public void addFeature(Feature newFeature) {
    	m_features.put(newFeature.id, newFeature);
    }
    
    public Feature getFeature(int id) {
    	return m_features.get(id);
    }
    
    // Delete Feature may cause nameMap index error, not implemented
    
    public Feature get(Integer id) {
        return m_features.get(id);
    }
    
    public Feature get(String featureName) {
        Integer id = getFeatureID(featureName);
        if (id == null)
            return null;
        else
            return get(id);
    }
    
    public int size() {
        return m_features.size();
    }

    public boolean contains(String featureName) {
        // m_nameMap and m_features should be in sync so no need to check both
        return m_nameMap.containsKey(featureName);
        /*
        Integer id = getFeatureID(featureName);
        if (id == null)
            return false;
        else
            return contains(id);
         */
    }

    public boolean contains(Integer id) {
        return m_features.containsKey(id);
    }
    
    public boolean contains(Feature f) {
        return contains(f.id);
    }

    /**
     * Add newFeature to the pool
     *
     * @param newFeature the Feature to be added
     * @return the id of the feature, if the feature already exists
     *         return its id if both same, return null if different
     * @throws Exception if the model type is unsupported/unknown
     */
    // 
    // Return the index on success or exists
    public Integer add(Feature newFeature) {
        Integer id = getFeatureID(newFeature.name);
        
        if (id == null) {
            m_features.put(newFeature.id, newFeature);
            m_nameMap.put(newFeature.name, newFeature.id);
            return newFeature.id;
        }
        else {
            Feature oldFeature = get(id);
            if (newFeature == oldFeature)
                return id;
            else
                return null;
        }

    }
    
    public Integer getFeatureID(String featureName) {
        return m_nameMap.get(featureName);
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
