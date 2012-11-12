package edu.ucla.nesl.mobileClassifier;

import java.util.HashMap;

public class FeaturePool {
    /**
     * TODO: feature pool requires synchronization between server and client
     * Once client update feature set, need to retrain
     */
    private HashMap<String, Long> m_nameMap = new HashMap<String, Long>();
    private HashMap<Long, Feature> m_features = new HashMap<Long, Feature>();
    
    // Delete Feature may cause nameMap index error, not implemented
    
    public Feature get(Long guid) {
        return m_features.get(guid);
    }
    
    public Feature get(String featureName) {
        Long guid = getFeatureGUID(featureName);
        if (guid == null)
            return null;
        else
            return get(guid);
    }
    
    public int size() {
        return m_features.size();
    }

    public boolean contains(String featureName) {
        // m_nameMap and m_features should be in sync so no need to check both
        return m_nameMap.containsKey(featureName);
        /*
        Long guid = getFeatureGUID(featureName);
        if (guid == null)
            return false;
        else
            return contains(guid);
         */
    }

    public boolean contains(Long guid) {
        return m_features.containsKey(guid);
    }
    
    public boolean contains(Feature f) {
        return contains(f.GUID);
    }

    /**
     * Add newFeature to the pool
     *
     * @param newFeature the Feature to be added
     * @return the GUID of the feature, if the feature already exists
     *         return its GUID if both same, return null if different
     * @throws Exception if the model type is unsupported/unknown
     */
    // 
    // Return the index on success or exists
    public Long add(Feature newFeature) {
        Long guid = getFeatureGUID(newFeature.name);
        
        if (guid == null) {
            m_features.put(newFeature.GUID, newFeature);
            m_nameMap.put(newFeature.name, newFeature.GUID);
            return newFeature.GUID;
        }
        else {
            Feature oldFeature = get(guid);
            if (newFeature == oldFeature)
                return guid;
            else
                return null;
        }

    }
    
    public Long getFeatureGUID(String featureName) {
        return m_nameMap.get(featureName);
    }
}
