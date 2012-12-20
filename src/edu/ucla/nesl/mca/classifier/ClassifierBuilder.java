package edu.ucla.nesl.mca.classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;
import edu.ucla.nesl.mca.feature.BuiltInFeature;
import edu.ucla.nesl.mca.feature.Feature;
import edu.ucla.nesl.mca.feature.Feature.OPType;
import edu.ucla.nesl.mca.feature.FeaturePool;
import edu.ucla.nesl.mca.feature.Trigger;

public class ClassifierBuilder {
	public static ArrayList<Classifier> BuildFromFile(File file) throws IOException {
		String jsonString = "";
		BufferedReader fileInput = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = fileInput.readLine()) != null) {
			jsonString += line + "\n";
		}
		fileInput.close();
		return Build(jsonString);
	}

	public static ArrayList<Classifier> Build(String jsonString) throws IOException {
		// String modelType = Classifier.getJSONModelType(jsonString);
		// Log.i("ClassifierBuilder", "model type: " + modelType);
		// Classifier cl = null;
		ArrayList<Classifier> result = new ArrayList<Classifier>();
		FeaturePool m_inputs = new FeaturePool();
		Feature m_output = null;
		try {
			JSONObject object = (JSONObject) new JSONTokener(jsonString).nextValue();
			object.getString("Name");
			JSONArray featureList = object.getJSONArray("Feature List");
			//JSONObject model = object.getJSONObject("Model");
			for (int i = 0; i < featureList.length(); i++) {
				JSONObject featureObj = featureList.getJSONObject(i);
				Feature feature = new Feature();
				feature.setId(featureObj.getInt("ID"));
				feature.setOpType(BuiltInFeature.getOPType(feature.getId()));
				feature.setName(featureObj.getString("Name"));
				if (featureObj.has("WindowSize")) {
					feature.setWindowSize(featureObj.getInt("WindowSize"));
				}
				if (featureObj.has("isResult")
						&& featureObj.getBoolean("isResult")) {
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
						JSONObject triggerObj = featureObj
								.getJSONObject("TriggerOn");
						int featureID = triggerObj.getInt("Feature");
						Log.i("Classifier", "Trigger on found id" + featureID);
						Log.i("Classifier", 
								"trigger type "
										+ BuiltInFeature.getOPType(featureID)
										+ " real type " + OPType.REAL);
						if (BuiltInFeature.getOPType(featureID) == OPType.REAL) {
							String operator = triggerObj.getString("Operation");
							double value = triggerObj.getDouble("Value");
							int duration = triggerObj.getInt("Duration");
							Log.i("Classifier", "Trigger on is " + operator);
							feature.setTriggerOn(new Trigger(featureID,
									operator, value, duration));
						} else if (BuiltInFeature.getOPType(featureID) == OPType.NOMINAL) {
							String value = triggerObj.getString("Value");
							int duration = triggerObj.getInt("Duration");
							feature.setTriggerOn(new Trigger(featureID, value,
									duration));
						}
					}
					if (featureObj.has("TriggerOff")) {
						JSONObject triggerObj = featureObj
								.getJSONObject("TriggerOff");
						int featureID = triggerObj.getInt("Feature");
						Log.i("Classifier", "Trigger off found id" + featureID);
						if (BuiltInFeature.getOPType(featureID) == OPType.REAL) {
							String operator = triggerObj.getString("Operation");
							double value = triggerObj.getDouble("Value");
							int duration = triggerObj.getInt("Duration");
							Log.i("Classifier", "Trigger off is " + operator);
							feature.setTriggerOff(new Trigger(featureID,
									operator, value, duration));
						} else if (BuiltInFeature.getOPType(featureID) == OPType.NOMINAL) {
							String value = triggerObj.getString("Value");
							int duration = triggerObj.getInt("Duration");
							feature.setTriggerOff(new Trigger(featureID, value,
									duration));
						}
					}
					// System.out.println(feature.id + " " + feature.name + " "
					// + feature.sensor);
					m_inputs.addFeature(feature);
				}
			}
			
			JSONArray modelList = object.getJSONArray("ModelList");
			for (int i = 0; i < modelList.length(); i++) {
				JSONObject curModel = modelList.getJSONObject(i);
				String type = curModel.getString("Type");
				int id = curModel.getInt("ID");
				if (type.equals("TREE")) {
					Classifier tree = new DecisionTree();
					tree.setId(id);
					tree.setType("TREE");
					tree.setM_inputs(m_inputs);
					tree.setM_output(m_output);
					tree.getModel(curModel);
					
					if (curModel.has("TriggerOn")) {
						JSONObject triggerObj = curModel.getJSONObject("TriggerOn");
						int featureID = triggerObj.getInt("Feature");
						Log.i("Classifier", "trigger on for classifier, featureID=" + featureID);
						//Log.i("Classifier", "Trigger on found id" + featureID);
						Log.i("Classifier", "trigger type " + BuiltInFeature.getOPType(featureID) + " real type " + OPType.REAL);
						if (BuiltInFeature.getOPType(featureID) == OPType.REAL) {
							String operator = triggerObj.getString("Operation");
							double value = triggerObj.getDouble("Value");
							int duration = triggerObj.getInt("Duration");
							//Log.i("Classifier", "Trigger on is " + operator);
							tree.setTriggerOn(new Trigger(featureID, operator, value, duration));
						} 
						else if (BuiltInFeature.getOPType(featureID) == OPType.NOMINAL) {
							String value = triggerObj.getString("Value");
							int duration = triggerObj.getInt("Duration");
							tree.setTriggerOn(new Trigger(featureID, value,	duration));
						}
					}
					if (curModel.has("TriggerOff")) {
						JSONObject triggerObj = curModel.getJSONObject("TriggerOff");
						int featureID = triggerObj.getInt("Feature");
						Log.i("Classifier", "Trigger off found id" + featureID);
						if (BuiltInFeature.getOPType(featureID) == OPType.REAL) {
							String operator = triggerObj.getString("Operation");
							double value = triggerObj.getDouble("Value");
							int duration = triggerObj.getInt("Duration");
							//Log.i("Classifier", "Trigger off is " + operator);
							tree.setTriggerOff(new Trigger(featureID,
									operator, value, duration));
						} else if (BuiltInFeature.getOPType(featureID) == OPType.NOMINAL) {
							String value = triggerObj.getString("Value");
							int duration = triggerObj.getInt("Duration");
							tree.setTriggerOff(new Trigger(featureID, value, duration));
						}
					}
					result.add(tree);
				}
			}

		} catch (Exception ex) {
			Log.i("ClassifierBuilder", ex.toString());
			ex.printStackTrace();
		}
		Log.i("ClassifierBuilder", "model count " + result.size());
		Log.i("ClassifierBuilder", "model0 type " + result.get(0).type);
		Log.i("ClassifierBuilder", "model1 type " + result.get(1).type);
		return result;
		// if (modelType.equals("TREE")) {
		// cl = new DecisionTree();
		// cl.type = modelType;
		// try {
		// cl.parseJSON(jsonString);
		// }
		// catch (Exception e) {
		// // TODO Auto-generated catch block
		// Log.i("ClassifierBuilder", e.toString());
		// e.printStackTrace();
		// }
		// }
		// else {
		// Log.i("ClassifierBuilder",
		// "[ClassifierBuilder] cannot recognize the model type.");
		// throw new
		// IOException("[ClassifierBuilder] cannot recognize the model type.");
		// }
		// return cl;
	}

	/**
	 * Test ClassifierBuilder
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
