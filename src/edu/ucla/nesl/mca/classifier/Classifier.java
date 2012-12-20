package edu.ucla.nesl.mca.classifier;

import org.json.JSONException;
import org.json.JSONObject;

import edu.ucla.nesl.mca.feature.Feature;
import edu.ucla.nesl.mca.feature.FeaturePool;
import edu.ucla.nesl.mca.feature.Trigger;
import edu.ucla.nesl.mca.xdr.XDRSerializable;

public abstract class Classifier implements XDRSerializable {
	// Currently only support multiple in, single out
	protected int id = 0;
	protected String name = null;
	protected String type = null;
	protected FeaturePool m_inputs = null;
	protected Feature m_output = null;
	protected String json = null;
	protected Trigger triggerOn;
	protected Trigger triggerOff;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Trigger getTriggerOn() {
		return triggerOn;
	}

	public void setTriggerOn(Trigger triggerOn) {
		this.triggerOn = triggerOn;
	}

	public Trigger getTriggerOff() {
		return triggerOff;
	}

	public void setTriggerOff(Trigger triggerOff) {
		this.triggerOff = triggerOff;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
	
	

//	public static String getJSONModelType(String jsonString) throws IOException {
//		try {
//			JSONObject object = (JSONObject) new JSONTokener(jsonString).nextValue();
//			JSONObject model = object.getJSONObject("Model");
//			return model.getString("Type");
//		} catch (JSONException e) {
//			throw new IOException(e);
//		}
//	}

//	public void parseJSON(String jsonString) throws Exception {
//		this.json = jsonString;
//		m_inputs = new FeaturePool();
//		//Log.i("Classifier", "set json string=" + jsonString);
//		
//
//			this.getModel(model);
//
//		} catch (Exception e) {
//			Log.i("Classifier", e.toString());
//			throw new Exception(e);
//		}
//	}

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FeaturePool getM_inputs() {
		return m_inputs;
	}

	public void setM_inputs(FeaturePool m_inputs) {
		this.m_inputs = m_inputs;
	}

	public Feature getM_output() {
		return m_output;
	}

	public void setM_output(Feature m_output) {
		this.m_output = m_output;
	}

	public void setType(String type) {
		this.type = type;
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
