package net.validcat.whofirst;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

//TODO Funny Font 
//TODO Graphics
//TODO XML: Style
//TODO more than 10 
//TODO TouchActivity: Vibration patterns
//TODO TouchActivity: Colors array
//TODO TouchActivity: Fingers size
//TODO TouchActivity: Counter
//TODO TouchActivity: Counter animation
//TODO TouchActivity: No finger, reset Default

//TODO Settings: count 3,2,1
//TODO Settings: color mode: array, random, black-white
