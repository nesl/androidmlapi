package edu.ucla.nesl.mca.classifier;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;
import edu.ucla.nesl.mca.feature.BuiltInFeature;
import edu.ucla.nesl.mca.feature.Feature;
import edu.ucla.nesl.mca.feature.Feature.OPType;
import edu.ucla.nesl.mca.feature.FeaturePool;
import edu.ucla.nesl.mca.feature.Trigger;
import edu.ucla.nesl.mca.xdr.XDRSerializable;

public abstract class Classifier implements XDRSerializable {
	// Currently only support multiple in, single out
	protected String name = null;
	protected String type = null;
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
		//Log.i("Classifier", "set json string=" + jsonString);
		try {
			JSONObject object = (JSONObject) new JSONTokener(jsonString).nextValue();
			object.getString("Name");
			JSONArray featureList = object.getJSONArray("Feature List");
			JSONObject model = object.getJSONObject("Model");
			for (int i = 0; i < featureList.length(); i++) {
				JSONObject featureObj = featureList.getJSONObject(i);
				Feature feature = new Feature();
				feature.setId(featureObj.getInt("ID"));
				feature.setOpType(BuiltInFeature.getOPType(feature.getId()));
				feature.setName(featureObj.getString("Name"));
				if (featureObj.has("WindowSize")) {
					feature.setWindowSize(featureObj.getInt("WindowSize"));
				}
				if (featureObj.has("isResult") && featureObj.getBoolean("isResult")) {
					feature.setResult(true);
					JSONArray res = featureObj.getJSONArray("Result");
					for (int j = 0; j < res.length(); j++) {
						feature.addMembership(res.getString(j));
					}
					feature.setOpType(Feature.OPType.NOMINAL);
					m_output = feature;
				} 
				else {
					feature.setResult(false);
					feature.setSensor(featureObj.getInt("SensorID"));
					if (featureObj.has("TriggerOn")) {
						JSONObject triggerObj = featureObj.getJSONObject("TriggerOn");
						int featureID = triggerObj.getInt("Feature");
						Log.i("Classifier", "Trigger on found id" + featureID);
						Log.i("Classifier", "trigger type " + BuiltInFeature.getOPType(featureID) + " real type " + OPType.REAL);
						if (BuiltInFeature.getOPType(featureID) == OPType.REAL) {
							String operator = triggerObj.getString("Operation");
							double value = triggerObj.getDouble("Value");
							int duration = triggerObj.getInt("Duration");
							Log.i("Classifier", "Trigger on is " + operator);
							feature.setTriggerOn(new Trigger(featureID, operator, value, duration));
						}
						else if (BuiltInFeature.getOPType(featureID) == OPType.NOMINAL) {
							String value = triggerObj.getString("Value");
							int duration = triggerObj.getInt("Duration");
							feature.setTriggerOn(new Trigger(featureID, value, duration));
						}						
					}
					if (featureObj.has("TriggerOff")) {
						JSONObject triggerObj = featureObj.getJSONObject("TriggerOff");
						int featureID = triggerObj.getInt("Feature");
						Log.i("Classifier", "Trigger off found id" + featureID);
						if (BuiltInFeature.getOPType(featureID) == OPType.REAL) {
							String operator = triggerObj.getString("Operation");
							double value = triggerObj.getDouble("Value");
							int duration = triggerObj.getInt("Duration");
							Log.i("Classifier", "Trigger off is " + operator);
							feature.setTriggerOff(new Trigger(featureID, operator, value, duration));
						}
						else if (BuiltInFeature.getOPType(featureID) == OPType.NOMINAL) {
							String value = triggerObj.getString("Value");
							int duration = triggerObj.getInt("Duration");
							feature.setTriggerOff(new Trigger(featureID, value, duration));
						}	
					}
					//System.out.println(feature.id + " " + feature.name + " " + feature.sensor);
					m_inputs.addFeature(feature);
				}
			}

			this.getModel(model);

		} catch (Exception e) {
			Log.i("Classifier", e.toString());
			throw new Exception(e);
		}
	}

	

	public FeaturePool getInputs() {
		return m_inputs;
	}

	public Feature getOutput() {
		return m_output;
	}
	
	public String getType() {
		return type;
	}
	
	public abstract Object evaluate();
	protected abstract void getModel(JSONObject modelObj) throws JSONException;
}
