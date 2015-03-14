package com.saakshin.bachao;


import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Button;
import android.widget.ImageButton;
import android.view.View.*;
import android.view.View;
import android.view.Window;
import android.content.Intent;

public class MainActivity extends Activity {
	private ImageButton alertButton;
	private Button settingsButton;
	String provider;
	
@Override
protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		getElementsById();
		buttonClickListener();	    	
	    
}
  


private void getElementsById(){
  alertButton = (ImageButton) findViewById(R.id.button1);
  settingsButton = (Button) findViewById(R.id.menu_settings);
}


private void buttonClickListener(){
	 alertButton.setOnClickListener(new OnClickListener()
     {
         @Override
         public void onClick(View v)
         {
        	 alertToFriend();
         }
     });
	 
	 settingsButton.setOnClickListener(new OnClickListener()
	 {
		 @Override
		 public void onClick(View w)
		 { 
			 showSettings();
		 }
	 });
}
	 
public void alertToFriend(){
		 Intent myIntent = new Intent(this, alertActivity.class);
		 this.startActivity(myIntent);
}

public void showSettings()
{
	Intent intent;
	if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
		 intent = new Intent(this,settingsActivity.class);
	}
	else 
	{
		intent = new Intent(this,SettingsActivityICS.class);
	}
		 startActivity(intent);
}

}
