//package edu.ucla.nesl.mca;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.HashSet;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.IBinder;
//import android.util.Log;
//import edu.ucla.nesl.mca.classifier.ClassifierBuilder;
//import edu.ucla.nesl.mca.classifier.DecisionTree;
//import edu.ucla.nesl.mca.feature.Feature;
//import edu.ucla.nesl.mca.feature.FeaturePool;
//import edu.ucla.nesl.mca.feature.SensorProfile;
//
//public class MainService extends Service implements SensorEventListener {
//	public static final String TAG = "MainService";
//	public static final String MAIN_CONFIG = "main_config";
//	public static final String DISPLAY_RESULT = "dispResult";
//	private DecisionTree classifier;
//	private HashSet<Integer> featureManager;
//	public static int count = 0;
//	public static int windowSize = Integer.MAX_VALUE;
//	private double[] accBufferX, accBufferY, accBufferZ;
//
//	@Override
//	public void onCreate() {
//
//	}
//
//	@Override
//	public IBinder onBind(Intent arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		Log.i("MainService", "Received start id " + startId + ": " + intent);
//		// We want this service to continue running until it is explicitly
//		// stopped, so return sticky.
//		String fileName = intent.getExtras().getString("JSONFile");
//		featureManager = new HashSet<Integer>();
//		// Get the JSON input file
//		File sdcard = Environment.getExternalStorageDirectory();
//		File file = new File(sdcard, fileName);
//		try {
//			/* Build the classifier */
//			classifier = (DecisionTree) ClassifierBuilder.BuildFromFile(file);
////			Toast.makeText(this, "classifier type=" + classifier.getType(),
////					Toast.LENGTH_LONG).show();
//			Log.i("MainService", "classifier first feature: "
//					+ classifier.getInputs().get(1).getName());
//			Log.i("MainService", "classifier second feature: "
//					+ classifier.getInputs().get(2).getName());
//			
//			/* After building classifier, check each feature and run each probe (start corresponding sensor manager) */
//			Log.i("MainService", "decision tree first node: " + classifier.getRootFeature().getName());
//			FeaturePool list = classifier.getInputs();
//			Log.i("MainService", "size=" + list.getM_index().size());
//			for (int index : list.getM_index()) {
//				Feature feature = list.getFeature(index);
//				int sensorID = feature.getSensor();
//				Log.i("MainService", "feature ID=" + feature.getId() + " sensor id=" + sensorID);
//				if (!featureManager.contains(sensorID)) {
//					if (feature.getWindowSize() <= windowSize) {
//						windowSize = feature.getWindowSize();
//					}
//					if (sensorID == SensorProfile.ACCELEROMETER) {
//						featureManager.add(sensorID);
//						accBufferX = new double[100];
//						accBufferY = new double[100];
//						accBufferZ = new double[100];
//						SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//						Sensor accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//						if (!manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)) {
//							Log.i("MainService", "cannot register");
//						}
//					}
//					else if (sensorID == SensorProfile.GPS) {
//						
//					}
//					else {
//						
//					}
//				}
//			}
//			Log.i("MainService", "windowsize=" + windowSize);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return START_STICKY;
//	}
//
//	@Override
//	public void onAccuracyChanged(Sensor arg0, int arg1) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onSensorChanged(SensorEvent event) {
//		// TODO Auto-generated method stub
//		accBufferX[count] = event.values[0];
//		accBufferY[count] = event.values[1];
//		accBufferZ[count] = event.values[2];
//		//Log.i("MainService", "Acc data: X " + event.values[0] + " Y " + event.values[1] + " Z " + event.values[2]);
//		count++;
//		if (count == windowSize) {	        
//			/* Start the evaluation of the classifier */
//			Bundle data = new Bundle();
//			data.putDoubleArray("AccX", accBufferX);
//			data.putDoubleArray("AccY", accBufferY);
//			data.putDoubleArray("AccZ", accBufferZ);
//			FeaturePool list = classifier.getInputs();
//			for (int index : list.getM_index()) {
//				Feature feature = list.getFeature(index);
//				if (feature.getSensor() == SensorProfile.ACCELEROMETER) {
//					feature.setData(data);
//				}
//			}
//			Object result = classifier.evaluate();			
//			Intent intent = new Intent(DISPLAY_RESULT);
//			intent.putExtra("mode", result.toString());
//	        sendBroadcast(intent);
//	        
//			count = 0;
//			Arrays.fill(accBufferX, 0);
//			Arrays.fill(accBufferY, 0);
//			Arrays.fill(accBufferZ, 0);
//		}
//	}
//}

package edu.ucla.nesl.mca;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import edu.mit.media.funf.IOUtils;
import edu.mit.media.funf.configured.ConfiguredPipeline;
import edu.mit.media.funf.configured.FunfConfig;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.storage.BundleSerializer;
import edu.ucla.nesl.mca.classifier.ClassifierBuilder;
import edu.ucla.nesl.mca.classifier.DecisionTree;
import edu.ucla.nesl.mca.feature.Feature;
import edu.ucla.nesl.mca.feature.FeaturePool;
import edu.ucla.nesl.mca.feature.SensorProfile;

public class MainService extends ConfiguredPipeline {
	public static final String TAG = "MainService";
	public static final String MAIN_CONFIG = "main_config";
	public static final String ACTION_RUN_ONCE = "RUN_ONCE";
	public static final String START_CLASSIFICATION = "START_CLASSIFICATION";
	public static final String RUN_ONCE_PROBE_NAME = "PROBE_NAME";
	public static final String DISPLAY_RESULT = "dispResult";
	private DecisionTree classifier;
	private HashSet<Integer> featureManager;
	private HashMap<Feature, Feature> triggerMap;
	public static int count = 0;
	public static int windowSize = Integer.MAX_VALUE;
	private double[] accBufferX, accBufferY, accBufferZ;
	
	@Override
	protected void onHandleIntent(Intent intent) {	
		Log.i("MainService", "Intent received");
		if (intent.getAction().equals(START_CLASSIFICATION)) {
//			String probeName = intent.getStringExtra(RUN_ONCE_PROBE_NAME);
//			Log.i(TAG, probeName);
//			runProbeOnceNow(probeName);
			
			String fileName = intent.getExtras().getString("JSONFile");
			featureManager = new HashSet<Integer>();
			triggerMap = new HashMap<Feature, Feature>();
			// Get the JSON input file
			File sdcard = Environment.getExternalStorageDirectory();
			File file = new File(sdcard, fileName);
			try {
				/* Build the classifier */
				classifier = (DecisionTree) ClassifierBuilder.BuildFromFile(file);
//				Toast.makeText(this, "classifier type=" + classifier.getType(),
//						Toast.LENGTH_LONG).show();
				Log.i("MainService", "classifier first feature: "
						+ classifier.getInputs().get(1).getName());
				Log.i("MainService", "classifier second feature: "
						+ classifier.getInputs().get(2).getName());
				
				/* After building classifier, check each feature and run each probe (start corresponding sensor manager) */
				Log.i("MainService", "decision tree first node: " + classifier.getRootFeature().getName());
				FeaturePool list = classifier.getInputs();
				Log.i("MainService", "size=" + list.getM_index().size());

				for (int index : list.getM_index()) {
					Feature feature = list.getFeature(index);
					int sensorID = feature.getSensor();
					Log.i("MainService", "feature ID=" + feature.getId() + " sensor id=" + sensorID + " trigger?" + (feature.getTrigger() != null));
					if (!featureManager.contains(sensorID)) {
						if (feature.getTrigger() != null) {
							int tID = feature.getTriggerFeature();
							Feature tFeature = null;
							for (int j : list.getM_index()) {
								if (list.getFeature(j).getId() == tID){
									tFeature = list.getFeature(j);
								}
							}
							triggerMap.put(tFeature, feature);
							Log.i("MainService", sensorID + " not started");
							Log.i("MainService", tFeature.getName() + " can trigger " + feature.getName());
						}
						else {
							if (feature.getWindowSize() <= windowSize) {
								windowSize = feature.getWindowSize();
							}
							if (sensorID == SensorProfile.ACCELEROMETER) {
								featureManager.add(sensorID);
								Log.i("MainService", sensorID + " started");
								accBufferX = new double[100];
								accBufferY = new double[100];
								accBufferZ = new double[100];
								//runProbeOnceNow(AccelerometerSensorProbe.class.getName());
							}
							else if (sensorID == SensorProfile.GPS) {
								
							}
							else {
								
							}
						}
						
					}
				}
				Log.i("MainService", "windowsize=" + windowSize);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			super.onHandleIntent(intent);
		}
	}
	
	@Override
	public BundleSerializer getBundleSerializer() {
		return null;
	}

	@Override
	public void onDataReceived(Bundle data) {
		//super.onDataReceived(data);
		//Log.i("MainService", "data received");
		//Toast.makeText(this, "data received!", Toast.LENGTH_SHORT).show();
		float[] dx = data.getFloatArray("X");
		float[] dy = data.getFloatArray("Y");
		float[] dz = data.getFloatArray("Z");
		Log.i("MainService", "Acc data: x=" + dx[0] +" y=" + dy[0] + " z=" + dz[0]);
		
//		accBufferX[count] = dx[0];
//		accBufferY[count] = dy[0];
//		accBufferZ[count] = dx[0];
//		count++;
//		
//		if (count == windowSize) {	        
//			/* Start the evaluation of the classifier */
//			Bundle resData = new Bundle();
//			resData.putDoubleArray("AccX", accBufferX);
//			resData.putDoubleArray("AccY", accBufferY);
//			resData.putDoubleArray("AccZ", accBufferZ);
//			FeaturePool list = classifier.getInputs();
//			for (int index : list.getM_index()) {
//				Feature feature = list.getFeature(index);
//				if (feature.getSensor() == SensorProfile.ACCELEROMETER) {
//					feature.setData(resData);
//				}
//			}
//			Object result = classifier.evaluate();			
//			Intent intent = new Intent(DISPLAY_RESULT);
//			intent.putExtra("mode", result.toString());
//	        sendBroadcast(intent);
//	        
//			count = 0;
//			Arrays.fill(accBufferX, 0);
//			Arrays.fill(accBufferY, 0);
//			Arrays.fill(accBufferZ, 0);
//		}
	}
	
	@Override
	public void onStatusReceived(Probe.Status status) {
		super.onStatusReceived(status);
		// Fill this in with extra behaviors on status received
	}
	
	@Override
	public void onDetailsReceived(Probe.Details details) {
		super.onDetailsReceived(details);
		// Fill this in with extra behaviors on details received
	}
	
	@Override
	public FunfConfig getConfig() {
		return getMainConfig(this);
	}

	/**
	 * Easy access to Funf config.  
	 * As long as this service is running, changes will be automatically picked up.
	 * @param context
	 * @return
	 */
	public static FunfConfig getMainConfig(Context context) {
		FunfConfig config = getConfig(context, MAIN_CONFIG);
		if (config.getName() == null) {			
			String jsonString = getStringFromAsset(context, "default_config.json");
			Toast.makeText(context, jsonString, Toast.LENGTH_SHORT).show();
			if (jsonString == null) {
				Log.e(TAG, "Error loading default config.  Using blank config.");
				jsonString = "{}";
			}
			try {
				config.edit().setAll(jsonString).commit();
			} catch (JSONException e) {
				Log.e(TAG, "Error parsing default config", e);
			}
		}
		return config;
	}
	
	public static String getStringFromAsset(Context context, String filename) {
		InputStream is = null;
		try {
			is = context.getAssets().open(filename);
			return IOUtils.inputStreamToString(is, Charset.defaultCharset().name());
		} catch (IOException e) {
			Log.e(TAG, "Unable to read asset to string", e);
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Log.e(TAG, "Unable to close asset input stream", e);
				}
			}
		}
	}
	
	public void runProbeOnceNow(final String probeName) {
		FunfConfig config = getMainConfig(this);
		ArrayList<Bundle> updatedRequests = new ArrayList<Bundle>();
		Bundle[] existingRequests = config.getDataRequests(probeName);
		if (existingRequests != null) {
			for (Bundle existingRequest : existingRequests) {
				updatedRequests.add(existingRequest);
			}
		}
		
		Bundle oneTimeRequest = new Bundle();
		oneTimeRequest.putLong(Probe.Parameter.Builtin.PERIOD.name, 0L);
		updatedRequests.add(oneTimeRequest);
		
		Intent request = new Intent(Probe.ACTION_REQUEST);
		request.setClassName(this, probeName);
		request.putExtra(Probe.CALLBACK_KEY, getCallback());
		request.putExtra(Probe.REQUESTS_KEY, updatedRequests);
		startService(request);
	}
}
