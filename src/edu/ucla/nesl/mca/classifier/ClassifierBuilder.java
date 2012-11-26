package edu.ucla.nesl.mca.classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
//import java.io.PrintWriter;
//
//import android.os.Environment;

public class ClassifierBuilder {
    public static Classifier BuildFromFile (File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        String jsonString = "";
        try {
			BufferedReader fileInput = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = fileInput.readLine()) != null) {
				jsonString += line + "\n";
			}
			fileInput.close();
        }
        finally {
            stream.close();
        }
        
//        File sdCard = Environment.getExternalStorageDirectory();
//        File outputFile = new File(sdCard, "/mlapi/jsonResult.txt");
//        //boolean flag = outputFile.createNewFile();
//        PrintWriter f = new PrintWriter(outputFile);
//        f.write(jsonString);
//        f.close();
//        Log.i("ClassifierBuilder", "file written " + outputFile.getAbsolutePath());
        

        return Build(jsonString);
      }
    
    public static Classifier Build(String jsonString) throws IOException {
//        String modelType = Classifier.getJSONModelType(jsonString);
//        Classifier cl;
//        if (modelType.equals("TREE")){
//            cl = new DecisionTree();
//        }
//        else{
//        	Log.i("ClassifierBuilder", "[ClassifierBuilder] cannot recognize the model type.");
//        	throw new IOException("[ClassifierBuilder] cannot recognize the model type.");
//        }
//        
//        try {
//			cl.parseJSON(jsonString);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        Classifier cl = new DecisionTree();
        try {
			cl.parseJSON(jsonString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
