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
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import edu.mit.media.funf.IOUtils;
import edu.mit.media.funf.configured.ConfiguredPipeline;
import edu.mit.media.funf.configured.FunfConfig;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.probe.builtin.AccelerometerSensorProbe;
import edu.mit.media.funf.storage.BundleSerializer;
import edu.ucla.nesl.mca.classifier.ClassifierBuilder;
import edu.ucla.nesl.mca.classifier.DecisionTree;
import edu.ucla.nesl.mca.feature.AlgorithmUtil;
import edu.ucla.nesl.mca.feature.Feature;
import edu.ucla.nesl.mca.feature.Feature.OPType;
import edu.ucla.nesl.mca.feature.FeaturePool;
import edu.ucla.nesl.mca.feature.SensorProfile;

public class MainService extends ConfiguredPipeline {
	public static final String TAG = "MainService";
	public static final String MAIN_CONFIG = "main_config";
	public static final String ACTION_RUN_ONCE = "RUN_ONCE";
	public static final String START_CLASSIFICATION = "START_CLASSIFICATION";
	public static final String UPDATE_DATA = "updateData";
	public static final String UPDATE_LOCATION = "updateData";
	public static final String RUN_ONCE_PROBE_NAME = "PROBE_NAME";
	public static final String DISPLAY_RESULT = "dispResult";
	private DecisionTree classifier;
	private HashSet<Integer> featureManager;
	private HashMap<Feature, Feature> triggerMapOn;
	private ArrayList<Feature> resultTriggerOn;
	private HashMap<Feature, Feature> triggerMapOff;
	private ArrayList<Feature> resultTriggerOff;
	public static int count = 0;
	public static int windowSize = Integer.MAX_VALUE;
	private double[] accBufferX, accBufferY, accBufferZ;
	private long [] onTimeCount = new long[20];
	private long [] offTimeCount = new long[20];
	LocationManager locationManager = null;

	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			// Called when a new location is found by the network location
			// provider.
			double lat = location.getLatitude();
			double logt = location.getLongitude();
			double alt = location.getAltitude();

			Log.i("MainService", "GPS!!!!! " + " latitute=" + lat
					+ " logitute=" + logt + " altitute=" + alt);
			Intent intent2 = new Intent(UPDATE_LOCATION);
			intent2.setAction(UPDATE_LOCATION);
			intent2.putExtra("lat", lat);
			intent2.putExtra("lot", logt);
			sendBroadcast(intent2);
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	};

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("MainService", "Intent received");
		if (intent.getAction().equals(START_CLASSIFICATION)) {
			// String probeName = intent.getStringExtra(RUN_ONCE_PROBE_NAME);
			// Log.i(TAG, probeName);
			// runProbeOnceNow(probeName);

			String fileName = intent.getExtras().getString("JSONFile");
			featureManager = new HashSet<Integer>();
			triggerMapOn = new HashMap<Feature, Feature>();
			resultTriggerOn = new ArrayList<Feature>();
			resultTriggerOff = new ArrayList<Feature>();
			// Get the JSON input file
			File sdcard = Environment.getExternalStorageDirectory();
			File file = new File(sdcard, fileName);
			try {
				/* Build the classifier */
				classifier = (DecisionTree) ClassifierBuilder
						.BuildFromFile(file);
				// Toast.makeText(this, "classifier type=" +
				// classifier.getType(),
				// Toast.LENGTH_LONG).show();
				Log.i("MainService", "classifier first feature: "
						+ classifier.getInputs().get(1).getName());
				Log.i("MainService", "classifier second feature: "
						+ classifier.getInputs().get(2).getName());

				/*
				 * After building classifier, check each feature and run each
				 * probe (start corresponding sensor manager)
				 */
				Log.i("MainService", "decision tree first node: "
						+ classifier.getRootFeature().getName());
				FeaturePool list = classifier.getInputs();
				Log.i("MainService", "size=" + list.getM_index().size());

				for (int index : list.getM_index()) {
					Feature feature = list.getFeature(index);
					int sensorID = feature.getSensor();
					Log.i("MainService", "feature ID=" + feature.getId() + " sensor id=" + sensorID + " triggerOn?" + (feature.getTriggerOn() != null) + " triggerOff?" + (feature.getTriggerOff() != null));
					if (!featureManager.contains(sensorID)) {
						if (feature.getTriggerOn() != null || feature.getTriggerOff() != null) {
							if (feature.getTriggerOn() != null) {
								int tOn = feature.getTriggerOn().getFeature();
								if (tOn > 0) {
									Feature tFeature = null;
									for (int j : list.getM_index()) {
										if (list.getFeature(j).getId() == tOn) {
											tFeature = list.getFeature(j);
										}
									}
									triggerMapOn.put(tFeature, feature);
									// Log.i("MainService",
									// tFeature.getTriggerFeature() + " " +
									// tFeature.getTriggerRealOperator() + " " +
									// tFeature.getTriggerThreshold());
									Log.i("MainService", sensorID + " not started");
									Log.i("MainService", tFeature.getName() + " can trigger " + feature.getName());
								} 
								else {
									resultTriggerOn.add(feature);
								}
							} 
							if (feature.getTriggerOff() != null) {
								int tOn = feature.getTriggerOff().getFeature();
								if (tOn > 0) {
									Feature tFeature = null;
									for (int j : list.getM_index()) {
										if (list.getFeature(j).getId() == tOn) {
											tFeature = list.getFeature(j);
										}
									}
									triggerMapOff.put(tFeature, feature);
									Log.i("MainService", tFeature.getName() + " can trigger stopping " + feature.getName());
								} 
								else {
									resultTriggerOff.add(feature);
								}
							} 
						}
						else {
							if (feature.getWindowSize() <= windowSize) {
								windowSize = feature.getWindowSize();
							}
							if (sensorID == SensorProfile.ACCELEROMETER) {
								featureManager.add(sensorID);
								Log.i("MainService", sensorID + " started");
								accBufferX = new double[windowSize];
								accBufferY = new double[windowSize];
								accBufferZ = new double[windowSize];
								runProbeOnceNow(AccelerometerSensorProbe.class.getName());
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onDataReceived(Bundle data) {
		// super.onDataReceived(data);
		Log.i("MainService", "data received");
		// Toast.makeText(this, "data received!", Toast.LENGTH_SHORT).show();
		float[] dx = data.getFloatArray("X");
		float[] dy = data.getFloatArray("Y");
		float[] dz = data.getFloatArray("Z");
		
		Intent intent1 = new Intent(UPDATE_DATA);
		intent1.setAction(UPDATE_DATA);
		intent1.putExtra("x", Float.valueOf(dx[0]).toString());
		intent1.putExtra("y", Float.valueOf(dy[0]).toString());
		intent1.putExtra("z", Float.valueOf(dz[0]).toString());
		sendBroadcast(intent1);
		// Log.i("MainService", "Acc data: x=" + dx[0] +" y=" + dy[0] + " z=" +
		// dz[0]);

		for (int i = 0; i < dx.length; i++) {
			accBufferX[count] = dx[i];
			accBufferY[count] = dy[i];
			accBufferZ[count] = dz[i];
			count++;
			double var = 0.0;
			if (count == windowSize) {
				/* Start the evaluation of the classifier */
				Bundle resData = new Bundle();
				resData.putDoubleArray("AccX", accBufferX);
				resData.putDoubleArray("AccY", accBufferY);
				resData.putDoubleArray("AccZ", accBufferZ);
				FeaturePool list = classifier.getInputs();
				for (int index : list.getM_index()) {
					Feature feature = list.getFeature(index);
					if (feature.getSensor() == SensorProfile.ACCELEROMETER) {
						feature.setData(resData);
					}
				}

				/* See if we need to trigger some sensor */
				Iterator it = triggerMapOn.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<Feature, Feature> pairs = (Map.Entry<Feature, Feature>) it.next();
					Feature triggee = pairs.getValue();
					Feature trigger = pairs.getKey();
					Object obj = trigger.evaluate(0);

					if (obj instanceof Double) {
						var = (Double) obj;
					}
					if(triggee.getTriggerOn() != null) {
						// Log.i("MainService", "Current trigger value: " + var);
						if(triggee.getTriggerOn().getType() == OPType.REAL) {
							if (triggee.getTriggerOn().getRealOp().evaluate(var, triggee.getTriggerOn().getThreshold())) {
								if (triggee.getTriggerOn().getDuration() == 0.0 || onTimeCount[triggee.getId()] == triggee.getTriggerOn().getDuration()) {
									if (!featureManager.contains(triggee.getSensor())) {
										featureManager.add(triggee.getSensor());
										/* Does it satisfy the duration requirement ?? */
										onTimeCount[triggee.getId()] = 0;
										if (triggee.getSensor() == SensorProfile.GPS) {
											Log.i("MainService", "Current trigger value: " + var);
											locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
											locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
										} 
										else if (triggee.getSensor() == SensorProfile.ACCELEROMETER) {

										} 
										else {

										}
									}
								}
								else {
									onTimeCount[triggee.getId()]++;
									Log.i("MainService", "current count " + onTimeCount[triggee.getId()]);
								}
							}
							else {
								onTimeCount[triggee.getId()] = 0;
							}
						}
						else if(triggee.getTriggerOn().getType() == OPType.NOMINAL) {
							
						}
						
					}
					/* Trigger off part, still need to rewrite */
//					if (triggee.getTriggerOff() != null) {
//						// Log.i("MainService", "Current trigger value: " + var);
//						if(triggee.getTriggerOff().getType() == OPType.REAL) {
//							if (triggee.getTriggerOff().getRealOp().evaluate(var, triggee.getTriggerOff().getThreshold())) {
//								if (featureManager.contains(triggee.getSensor())) {
//									featureManager.remove(triggee.getSensor());
//									if (triggee.getSensor() == SensorProfile.GPS) {
//										Log.i("MainService", "Current STOP trigger value: " + var);
//										locationManager.removeUpdates(locationListener);
//									} 
//									else if (triggee.getSensor() == SensorProfile.ACCELEROMETER) {
//
//									} 
//									else {
//
//									}
//								}
//							} 
//						}
//						else if(triggee.getTriggerOn().getType() == OPType.NOMINAL) {
//							if (triggee.getTriggerOff() != null && triggee.getTriggerOff().getDuration() == 0.0) {
//								
//							}
//							if (triggee.getTriggerOff() != null && triggee.getTriggerOff().getDuration() == 0.0) {
//								
//							}
//						}
//					}
					
				}

				Object result = classifier.evaluate();
				
				/* If the result can trigger to start/stop some sensors */
				for (Feature triggee:resultTriggerOn) {
					triggee.getId();
				}
				for (Feature triggee:resultTriggerOff) {
					if(triggee.getTriggerOff() != null) {
						if(triggee.getTriggerOff().getType() == OPType.REAL) {
							if (triggee.getTriggerOff().getRealOp().evaluate(var, triggee.getTriggerOff().getThreshold())) {
								if (triggee.getTriggerOff().getDuration() == 0.0 || offTimeCount[triggee.getId()] == triggee.getTriggerOff().getDuration()) {
									if (featureManager.contains(triggee.getSensor())) {
										featureManager.remove(triggee.getSensor());
										/* Does it satisfy the duration requirement ?? */
										onTimeCount[triggee.getId()] = 0;
										if (triggee.getSensor() == SensorProfile.GPS) {
											locationManager.removeUpdates(locationListener);
											AlgorithmUtil.setIndoor();
											Log.i("MainService", "Set indoor to be true");
										} 
										else if (triggee.getSensor() == SensorProfile.ACCELEROMETER) {

										} 
										else {

										}
									}
								}
								else {
									offTimeCount[triggee.getId()]++;
								}
							}
							else {
								offTimeCount[triggee.getId()] = 0;
							}
						}
						else if(triggee.getTriggerOff().getType() == OPType.NOMINAL) {
							if(triggee.getTriggerOff().getValue().equals(result.toString())) {
								if (triggee.getTriggerOff().getDuration() == 0.0 || offTimeCount[triggee.getId()] == triggee.getTriggerOff().getDuration()) {
									if (featureManager.contains(triggee.getSensor())) {
										featureManager.remove(triggee.getSensor());
										/* Does it satisfy the duration requirement ?? */
										onTimeCount[triggee.getId()] = 0;
										if (triggee.getSensor() == SensorProfile.GPS) {
											locationManager.removeUpdates(locationListener);
											AlgorithmUtil.setIndoor();
											Log.i("MainService", "Set indoor to be true");
										} 
										else if (triggee.getSensor() == SensorProfile.ACCELEROMETER) {

										} 
										else {

										}
									}
								}
								else {
									offTimeCount[triggee.getId()]++;
									Log.i("MainService", "current count for off" + offTimeCount[triggee.getId()]);
								}
							}
							else {
								offTimeCount[triggee.getId()] = 0;
							}
						}
					}
				}
				
				
				Intent intent = new Intent(DISPLAY_RESULT);
				intent.setAction(DISPLAY_RESULT);
				intent.putExtra("mode", result.toString());
				intent.putExtra("indoor", Double.valueOf(var).toString());
				if (featureManager.contains(SensorProfile.GPS)) {
					intent.putExtra("gps", "GPS On");
				}
				else {
					intent.putExtra("gps", "GPS Off");
				}
				
				sendBroadcast(intent);

				count = 0;
				Arrays.fill(accBufferX, 0);
				Arrays.fill(accBufferY, 0);
				Arrays.fill(accBufferZ, 0);
			}
		}
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
	 * Easy access to Funf config. As long as this service is running, changes
	 * will be automatically picked up.
	 * 
	 * @param context
	 * @return
	 */
	public static FunfConfig getMainConfig(Context context) {
		FunfConfig config = getConfig(context, MAIN_CONFIG);
		if (config.getName() == null) {
			String jsonString = getStringFromAsset(context,
					"default_config.json");
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
			return IOUtils.inputStreamToString(is, Charset.defaultCharset()
					.name());
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
