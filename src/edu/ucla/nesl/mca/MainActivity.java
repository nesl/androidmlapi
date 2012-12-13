package edu.ucla.nesl.mca;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnSharedPreferenceChangeListener {	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context context = this;
        
        Button button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent(context, MainService.class);
//				intent.putExtra("JSONFile", "mlapi/JSON_IndoorTest.txt");
//				startService(intent);
				Intent runOnceIntent = new Intent(context, MainService.class);
				runOnceIntent.setAction(MainService.START_CLASSIFICATION);
				runOnceIntent.putExtra("JSONFile", "mlapi/JSON_GPSTriggerTest.txt");
				//runOnceIntent.putExtra(MainService.RUN_ONCE_PROBE_NAME, AccelerometerSensorProbe.class.getName());
				startService(runOnceIntent);
			}        	
        });
        registerReceiver(receiver, new IntentFilter(MainService.DISPLAY_RESULT));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() { 
    	@Override
		public void onReceive(Context context, Intent intent) { 
    		String res = intent.getCharSequenceExtra("mode").toString();
    		TextView mode = (TextView)findViewById(R.id.classifierInfo);
    		mode.setText(res);
    		String res1 = intent.getCharSequenceExtra("indoor").toString();
    		TextView indoor = (TextView)findViewById(R.id.TextView02);
    		indoor.setText(res1);
    		//Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
    	} 
    }; 
    
    @Override
	protected void onResume() { 
    	super.onResume(); 
    	registerReceiver(receiver, new IntentFilter(MainService.DISPLAY_RESULT));
    }
    
    @Override
    protected void onPause() {
    	super.onResume(); 
    	unregisterReceiver(receiver); 
    }
    
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
	}
}
