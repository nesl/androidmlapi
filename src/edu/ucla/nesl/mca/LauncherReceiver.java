package edu.ucla.nesl.mca;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LauncherReceiver extends BroadcastReceiver {
	
	private static boolean launched = false;
	
	public static void launch(Context context) {
		Log.e("Test", "TestTestTest");
		startService(context, MainService.class); // Ensure main funf system is running
		launched = true;
	}
	
	public static void startService(Context context, Class<? extends Service> serviceClass) {
		Intent i = new Intent(context.getApplicationContext(), serviceClass);
		context.getApplicationContext().startService(i);
	}
	
	public static boolean isLaunched() {
		return launched;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		launch(context);
	}
}
