package edu.ucla.nesl.mca;

import edu.ucla.nesl.mca.R;
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
import android.widget.*;

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
				Intent intent = new Intent(context, MainService.class);
				intent.putExtra("JSONFile", "mlapi/JSON_IndoorTest.txt");
				startService(intent);
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
    		Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
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
//		Log.i("MainActivity", "key=" + key);
//		TextView info = (TextView)findViewById(R.id.classifierInfo);
//		
//		if (key.equals("JSON_TEST")) {
//			String str = MainService.getClassifierInfo(this);
//			String str1 = sharedPreferences.getString(key, "test");
//			Log.i("MainActivity", "length=" + str.length() + " content=" + str + "content1=" + str1);
//			info.setText(str);
//		}
	}
}
