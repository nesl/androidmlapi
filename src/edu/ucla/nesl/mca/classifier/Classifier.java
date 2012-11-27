package edu.ucla.nesl.mca.classifier;

import java.io.IOException;

import org.json.*;

import android.util.Log;

import edu.ucla.nesl.mca.feature.Feature;
import edu.ucla.nesl.mca.feature.FeaturePool;
import edu.ucla.nesl.mca.xdr.XDRSerializable;

public abstract class Classifier implements XDRSerializable {
	// Currently only support multiple in, single out
	protected String name = null;
	protected FeaturePool m_inputs = null;
	protected Feature m_output = null;
	protected String json = null;

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public static String getJSONModelType(String jsonString) throws IOException {
		try {
			JSONObject object = (JSONObject) new JSONTokener(jsonString)
					.nextValue();
			JSONObject model = object.getJSONObject("Model");
			return model.getString("Type");
		} catch (JSONException e) {
			throw new IOException(e);
		}
	}

	public void parseJSON(String jsonString) throws Exception {
		this.json = jsonString;
		m_inputs = new FeaturePool();
		Log.i("Classifier", "set json string=" + jsonString);
		try {
			JSONObject object = (JSONObject) new JSONTokener(jsonString).nextValue();
			object.getString("Name");
			JSONArray featureList = object.getJSONArray("Feature List");
			JSONObject model = object.getJSONObject("Model");
			for (int i = 0; i < featureList.length(); i++) {
				JSONObject featureObj = featureList.getJSONObject(i);
				Feature feature = new Feature();
				feature.id = featureObj.getInt("ID");
				feature.opType = BuiltInClassifier.getOPType(feature.id);
				feature.name = featureObj.getString("Name");		
				if (featureObj.has("isResult") && featureObj.getBoolean("isResult")) {
					feature.isResult = true;
					JSONArray res = featureObj.getJSONArray("Result");
					for (int j = 0; j < res.length(); j++) {
						feature.addMembership(res.getString(j));
					}
					feature.opType = Feature.OPType.NOMINAL;
					m_output = feature;
				} 
				else {
					feature.isResult = false;
					feature.sensor = featureObj.getInt("SensorID");		
					//System.out.println(feature.id + " " + feature.name + " " + feature.sensor);
					m_inputs.add(feature);
				}
			}

			this.getModel(model);

		} catch (Exception e) {
			throw new Exception(e);
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
