package edu.ucla.nesl.mca.feature;

import java.io.IOException;
import java.util.ArrayList;
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
    private ArrayList<Integer> m_index;
    
    // we should add some data structure to map features to sensors, and feature to names
    
    public FeaturePool() {
    	m_nameMap = new HashMap<String, Integer>();
    	m_features = new HashMap<Integer, Feature>();
    	m_index = new ArrayList<Integer>();
    }
    
    public void addFeature(Feature newFeature) {
    	m_features.put(newFeature.getId(), newFeature);
    	m_index.add(newFeature.getId());
    }
    
    public Feature getFeature(int id) {
    	return m_features.get(id);
    }
        
    public ArrayList<Integer> getM_index() {
		return m_index;
	}

	public void setM_index(ArrayList<Integer> m_index) {
		this.m_index = m_index;
	}

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
        return contains(f.getId());
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
        Integer id = getFeatureID(newFeature.getName());
        
        if (id == null) {
            m_features.put(newFeature.getId(), newFeature);
            m_nameMap.put(newFeature.getName(), newFeature.getId());
            return newFeature.getId();
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
