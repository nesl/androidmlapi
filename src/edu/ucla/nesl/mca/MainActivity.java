package edu.ucla.nesl.mca;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
				Toast.makeText(context, "Start to infer!", Toast.LENGTH_SHORT).show();
				Intent runOnceIntent = new Intent(context, MainService.class);
				runOnceIntent.setAction(MainService.START_CLASSIFICATION);
				runOnceIntent.putExtra("JSONFile", "mlapi/JSON_MultipleModelTest.txt");
				//runOnceIntent.putExtra(MainService.RUN_ONCE_PROBE_NAME, AccelerometerSensorProbe.class.getName());
				startService(runOnceIntent);
			}        	
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainService.DISPLAY_RESULT);
        filter.addAction(MainService.UPDATE_DATA);
        filter.addAction(MainService.UPDATE_LOCATION);
        registerReceiver(receiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() { 
    	@Override
		public void onReceive(Context context, Intent intent) { 
    		if (intent.getAction().equals(MainService.DISPLAY_RESULT)) {
    			String res = intent.getCharSequenceExtra("mode").toString();
        		TextView mode = (TextView)findViewById(R.id.classifierInfo);
        		mode.setText(res);
        		String res1 = intent.getCharSequenceExtra("indoor").toString();
        		TextView indoor = (TextView)findViewById(R.id.TextView03);
        		if (res1.equals("1.0")) {
        			indoor.setText("Outdoor");
        		}
        		else {
        			indoor.setText("Indoor");
        		}
        		
        		String res2 = intent.getCharSequenceExtra("gps").toString();
        		TextView gpsStatus= (TextView)findViewById(R.id.TextView02);
        		gpsStatus.setText(res2);
    		}
    		else if (intent.getAction().equals(MainService.UPDATE_DATA)) {
    			String strX = intent.getStringExtra("x");
    			String strY = intent.getStringExtra("y");
    			String strZ = intent.getStringExtra("z");
    			TextView textX = (TextView)findViewById(R.id.TextView10);
    			TextView textY = (TextView)findViewById(R.id.TextView11);
    			TextView textZ = (TextView)findViewById(R.id.TextView12);
    			textX.setText(strX);
    			textY.setText(strY);
    			textZ.setText(strZ);
    		}
    		else if (intent.getAction().equals(MainService.UPDATE_LOCATION)) {
    			TextView textLa = (TextView)findViewById(R.id.textView2);
    			TextView textLo = (TextView)findViewById(R.id.TextView06);
    			Log.i("MainActivity", intent.getStringExtra("lat"));
    			if (intent.getStringExtra("lat").equals("None")) {
    				textLa.setText("Off");
        			textLo.setText("Off");
    			}
    			else if (intent.getStringExtra("lat").equals("No Signal")) {
    				textLa.setText("No Signal");
        			textLo.setText("No Signal");
    			}
    			else {
    				String strLa = Double.valueOf(intent.getDoubleExtra("lat", 0.0)).toString();
        			String strLo = Double.valueOf(intent.getDoubleExtra("lot", 0.0)).toString();
    				textLa.setText(strLa);
        			textLo.setText(strLo);
    			}
    		}
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
