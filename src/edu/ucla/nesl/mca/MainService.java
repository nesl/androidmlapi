package edu.ucla.nesl.mca;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import edu.ucla.nesl.mca.classifier.ClassifierBuilder;
import edu.ucla.nesl.mca.classifier.DecisionTree;
import edu.ucla.nesl.mca.feature.Feature;
import edu.ucla.nesl.mca.feature.FeaturePool;
import edu.ucla.nesl.mca.feature.SensorProfile;

public class MainService extends Service implements SensorEventListener {
	public static final String TAG = "MainService";
	public static final String MAIN_CONFIG = "main_config";
	public static final String DISPLAY_RESULT = "dispResult";
	private DecisionTree classifier;
	private HashSet<Integer> featureManager;
	public static int count = 0;
	private double[] accBufferX, accBufferY, accBufferZ;

	@Override
	public void onCreate() {

	}

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
		String fileName = intent.getExtras().getString("JSONFile");
		featureManager = new HashSet<Integer>();
		// Get the JSON input file
		File sdcard = Environment.getExternalStorageDirectory();
		File file = new File(sdcard, fileName);
		try {
			/* Build the classifier */
			classifier = (DecisionTree) ClassifierBuilder.BuildFromFile(file);
//			Toast.makeText(this, "classifier type=" + classifier.getType(),
//					Toast.LENGTH_LONG).show();
			Log.i("MainService", "classifier first feature: "
					+ classifier.getInputs().get(1).getName());
			Log.i("MainService", "classifier second feature: "
					+ classifier.getInputs().get(2).getName());
			
			/* After building classifier, check each feature and run each probe (start corresponding sensor manager) */
			Log.i("MainService", "decision tree first node: "
					+ classifier.getRootFeature().getName());
			FeaturePool list = classifier.getInputs();
			Log.i("MainService", "size=" + list.getM_index().size());
			for (int index : list.getM_index()) {
				Feature feature = list.getFeature(index);
				int sensorID = feature.getSensor();
				Log.i("MainService", "feature ID=" + feature.getId() + " sensor id=" + sensorID);
				if (!featureManager.contains(sensorID)) {
					if (sensorID == SensorProfile.ACCELEROMETER) {
						featureManager.add(sensorID);
						accBufferX = new double[100];
						accBufferY = new double[100];
						accBufferZ = new double[100];
						SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
						Sensor accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
						if (!manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)) {
							Log.i("MainService", "cannot register");
						}
					}
					else if (sensorID == SensorProfile.GPS) {
						
					}
					else {
						
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return START_STICKY;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		accBufferX[count] = event.values[0];
		accBufferY[count] = event.values[1];
		accBufferZ[count] = event.values[2];
		//Log.i("MainService", "Acc data: X " + event.values[0] + " Y " + event.values[1] + " Z " + event.values[2]);
		count++;
		if (count == 100) {	        
			/* Start the evaluation of the classifier */
			Bundle data = new Bundle();
			data.putDoubleArray("AccX", accBufferX);
			data.putDoubleArray("AccY", accBufferY);
			data.putDoubleArray("AccZ", accBufferZ);
			FeaturePool list = classifier.getInputs();
			for (int index : list.getM_index()) {
				Feature feature = list.getFeature(index);
				if (feature.getSensor() == SensorProfile.ACCELEROMETER) {
					feature.setData(data);
				}
			}
			Object result = classifier.evaluate();			
			Intent intent = new Intent(DISPLAY_RESULT);
			intent.putExtra("mode", result.toString());
	        sendBroadcast(intent);
	        
			count = 0;
			Arrays.fill(accBufferX, 0);
			Arrays.fill(accBufferY, 0);
			Arrays.fill(accBufferZ, 0);
		}
		
	}
}
