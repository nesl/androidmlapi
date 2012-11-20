package edu.ucla.nesl.mca.classifier;

import java.io.IOException;
import java.util.ArrayList;

import org.json.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.ucla.nesl.mca.feature.Feature;
import edu.ucla.nesl.mca.feature.FeaturePool;
import edu.ucla.nesl.mca.xdr.XDRSerializable;

public abstract class Classifier implements XDRSerializable{
    // Currently only support multiple in, single out
    protected FeaturePool m_inputs = null;
    protected Feature m_output = null;
    
    public static String getJSONModelType(String jsonString) throws IOException {
        try {
            JSONObject object = (JSONObject) new JSONTokener(jsonString).nextValue();
            JSONObject model = object.getJSONObject("Model");
            return model.getString("Type");
            
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }
    
    public void parseJSON(String jsonString) throws IOException {
        try {
            JSONObject object = (JSONObject) new JSONTokener(jsonString).nextValue();
            String name = object.getString("Name");
            JSONArray featureList = object.getJSONArray("Feature List");
            JSONObject model = object.getJSONObject("Model");
            
            for (int i = 0; i < featureList.length(); i++) {
                JSONObject feature = featureList.getJSONObject(i);
                boolean isResult = false;
                if (object.has("isResult")) {
                    isResult = object.getBoolean("isResult");
                }
                
                Feature f = new Feature();
            }
            
            this.getModel(model);
            
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }
    
    public Object evaluate() {
        return null;
    }
    
    public FeaturePool getInputs() {
        return m_inputs;
    }
    
    public Feature getOutput() {
        return m_output;
    }

    protected abstract void getModel(JSONObject modelObj) throws JSONException;
}
