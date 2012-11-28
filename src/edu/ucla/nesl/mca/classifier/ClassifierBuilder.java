package edu.ucla.nesl.mca.classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import android.util.Log;

public class ClassifierBuilder {
	public static Classifier BuildFromFile(File file) throws IOException {
		String jsonString = "";
		BufferedReader fileInput = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = fileInput.readLine()) != null) {
			jsonString += line + "\n";
		}
		fileInput.close();
		return Build(jsonString);
	}

	public static Classifier Build(String jsonString) throws IOException {
		String modelType = Classifier.getJSONModelType(jsonString);
		Log.i("ClassifierBuilder", "model type: " + modelType);
		Classifier cl = null;
		if (modelType.equals("TREE")) {
			cl = new DecisionTree();
			cl.type = modelType;
			try {
				cl.parseJSON(jsonString);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.i("ClassifierBuilder", e.toString());
				e.printStackTrace();
			}
		} else {
			Log.i("ClassifierBuilder",
					"[ClassifierBuilder] cannot recognize the model type.");
			throw new IOException(
					"[ClassifierBuilder] cannot recognize the model type.");
		}
		return cl;
	}

	/**
	 * Test ClassifierBuilder
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
