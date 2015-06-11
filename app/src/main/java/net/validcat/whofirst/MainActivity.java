package net.validcat.whofirst;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initUI();
    }

    private void initUI() {
        ((TextView) findViewById(R.id.logo_text)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/clicker.ttf"));
        ((TextView) findViewById(R.id.tv_title)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/fredoka.ttf"));
        ((Button) findViewById(R.id.btn_start)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/fredoka.ttf"));
        ((Button) findViewById(R.id.btn_settings)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/fredoka.ttf"));
	}

	public void onClickStart(View v) {
    	startActivity(new Intent(this, TouchActivity.class));
    }
    
    public void onClickSettings(View v) {
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}

// Funny Font 
//TODO Graphics
//TODO XML: Style
//TODO more than 10 
// TouchActivity: Vibration patterns
//TODO TouchActivity: Colors array
// TouchActivity: Fingers size
// TouchActivity: Counter
// TouchActivity: Counter animation
// TouchActivity: No finger, reset Default

//TODO Settings: count 3,2,1
//TODO Settings: color mode: array, random, black-white
