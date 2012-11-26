package edu.ucla.nesl.mca;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class MainActivity extends Activity implements OnSharedPreferenceChangeListener {
	TextView text1;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text1 = (TextView)findViewById(R.id.debugInfoText);
        text1.setText("I am modified!");
        final Context context = this;
        
        Button button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				text1.setText("Service started!");
				Intent intent = new Intent(context, MainService.class);
				startService(intent);
			}        	
        }
        );
        MainService.getSystemPrefs(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void test(View v) {
        // Perform action on click
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		Log.i("MainActivity", "key=" + key);
		TextView info = (TextView)findViewById(R.id.classifierInfo);
		
		if (key.equals("JSON_TEST")) {
			String str = MainService.getClassifierInfo(this);
			String str1 = sharedPreferences.getString(key, "test");
			Log.i("MainActivity", "length=" + str.length() + " content=" + str + "content1=" + str1);
			info.setText(str);
		}
	}
}
