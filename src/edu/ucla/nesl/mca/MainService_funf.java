package edu.ucla.nesl.mca;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.json.JSONException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import edu.mit.media.funf.IOUtils;
import edu.mit.media.funf.configured.ConfiguredPipeline;
import edu.mit.media.funf.configured.FunfConfig;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.probe.builtin.AccelerometerSensorProbe;
import edu.mit.media.funf.storage.BundleSerializer;

public class MainService_funf extends ConfiguredPipeline {
	
	public static final String TAG = "FunfBGCollector";
	public static final String MAIN_CONFIG = "main_config";
	public static final String START_DATE_KEY = "START_DATE";

	public static final String ACTION_RUN_ONCE = "RUN_ONCE";
	public static final String RUN_ONCE_PROBE_NAME = "PROBE_NAME";
	
	@Override
	protected void onHandleIntent(Intent intent) {
		runProbeOnceNow(AccelerometerSensorProbe.class.getName());
	}
	
	@Override
	public BundleSerializer getBundleSerializer() {
		return null;
	}

	@Override
	public void onDataReceived(Bundle data) {
		super.onDataReceived(data);
		Log.i("MainService_Funf", "data received");
		Toast.makeText(this, "data received!", Toast.LENGTH_SHORT).show();
		float[] dx = data.getFloatArray("X");
		float[] dy = data.getFloatArray("Y");
		float[] dz = data.getFloatArray("Z");
		Log.i("MainService_Funf", "Acc data: x=" + dx[1] +" y=" + dy[1] + " z=" + dz[1]);
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