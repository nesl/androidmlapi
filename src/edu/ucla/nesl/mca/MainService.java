package edu.ucla.nesl.mca;

import static edu.mit.media.funf.AsyncSharedPrefs.async;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import edu.mit.media.funf.configured.ConfiguredPipeline;
import edu.mit.media.funf.storage.BundleSerializer;
import edu.ucla.nesl.mca.classifier.*;

public class MainService extends ConfiguredPipeline  {
	private DecisionTree classifier;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("MainService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        File sdcard = Environment.getExternalStorageDirectory();
        
        //Get the text file
        File file = new File(sdcard,"mlapi/JSON_Test.txt");
        try {
			classifier = (DecisionTree) ClassifierBuilder.BuildFromFile(file);

			//recordData(classifier.getJson());
			//Toast.makeText(this, "json data=" + classifier.getJson(), Toast.LENGTH_SHORT).show();
			Toast.makeText(this, "classifier type=" + classifier.getType(), Toast.LENGTH_LONG).show();
			Log.i("MainService", "classifier first feature: " + classifier.getInputs().get(1).getName());
			Log.i("MainService", "classifier second feature: " + classifier.getInputs().get(2).getName());
			// after build classifier, check each feature and run each probe
			Log.i("MainService", "decision tree first node: " + classifier.getRootFeature().getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        clearData();
        
        return START_STICKY;
    }
    
    public void clearData() {
    	SharedPreferences.Editor editor = getSystemPrefs().edit();
    	editor.clear();
    	editor.commit();
    }
    
    public void recordData(String data) {
    	SharedPreferences.Editor editor = getSystemPrefs().edit();
    	editor.putString("JSON_TEST", data);
    	boolean flag = editor.commit();
    	
    	Log.i("MainService", "Commit success? " + flag + " size=" + getSystemPrefs().getAll().size());
    }
    
    public static String getClassifierInfo(Context context) {
    	Log.i("MainService", "get method called size=" + getSystemPrefs(context).getAll().size());
    	return getSystemPrefs(context).getString("JSON_TEST", "abc");
    }
    
	@Override
	public void onDataReceived(Bundle data) {
		super.onDataReceived(data);
		// let the classifier to re-evaluate
	}
	
	@Override
    public void onCreate() {
		
	}

	@Override
	public BundleSerializer getBundleSerializer() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public SharedPreferences getSystemPrefs() {
		return getSystemPrefs(this);
	}
	
	public static SharedPreferences getSystemPrefs(Context context) {
		return async(context.getSharedPreferences(MainService.class.getName() + "_system", MODE_PRIVATE));
	}
}
