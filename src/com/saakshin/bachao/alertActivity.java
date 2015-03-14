//the activity launched on pressing bachchao button 
package com.saakshin.bachao;


import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;


import com.saakshin.bachao.LocationFinder;

import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;

import android.view.Window;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;





public class alertActivity extends Activity {
	SharedPreferences settings;
	int updatetime;
	public static final String PREFS = "BachchaoPrefsFile";
	private static final int MAX_CNUMBER = 3;
	boolean gpsStatus = false;
	boolean networkStatus = false;
	double longitude;
	double latitude;
    boolean previousloc = false;
  
	String alertmsg,username,addressstr;
	private static final int REQUEST_CODE = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		gpsSwitch();
		 settings = getSharedPreferences(PREFS, 0);
	       updatetime =  settings.getInt("locationupdateinterval", 0);
	       LocationFinder gps = new LocationFinder(this);
	       alertmsg = settings.getString("alertmsg", "Help!I am in an emergency at" );
	       username = settings.getString("username", "");
	      
         // check if GPS enabled
         if(gps.locationavailable()){
        	 
        		  latitude = gps.getLatitude();
        		  longitude = gps.getLongitude();
        		  if(latitude==0&&longitude==0)
        		  {
        			  previousloc= true;
        			  gps.getLastLocation();
        			  latitude=gps.getLatitude();
        			  longitude = gps.getLongitude();
        		  }
        		  
         }else{
        	 
        	 // this is an exploitation of the bug but an user should be asked before setting the gps on 

        	final Intent poke = new Intent();
             poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
             poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
             poke.setData(Uri.parse("3")); 
             sendBroadcast(poke);
             Toast.makeText(getApplicationContext(), ""+gps, Toast.LENGTH_SHORT).show();
             latitude = gps.getLatitude();
             longitude = gps.getLongitude();
             
             if(latitude==0&&longitude==0)
             {
            	 previousloc=true;
            	 gps.getLastLocation();
   			  	 latitude=gps.getLatitude();
   			  	 longitude = gps.getLongitude();
   			  	 
   		  	}
            
            }
        
         if(settings.getBoolean("address", false))
     	{   
        	 setMobileDataEnabled();
         
        	 if(isInternetOn())
        	 {
        		 address();
        	 }
     	}
         
         String msg = alertmsg;
     	if(settings.getBoolean("address", false))
     	{   
     		if(isInternetOn())
     		{
     			
     			if(addressstr!=null)
     			msg += " "+addressstr;
     		}
     	}
     	if(settings.getBoolean("latlong", true))
     	{
     		if(latitude!=0.0 || longitude!=0.0)
     		{
     			if(addressstr==null)
    			{
    				msg += " https://maps.google.com/maps?q=" +latitude+","+longitude+".";
    			}
    			else
    			msg += " "+latitude+","+longitude+".";
     			
     			if(previousloc)
     	     	{
     	     		msg +="(last known location)";
     	     	}
     	         
     		}
     	}else if((!isInternetOn())||(addressstr==null))
     	{
     		if(latitude!=0.0 || longitude!=0.0)
     		{
     			msg += " https://maps.google.com/maps?q=" +latitude+","+longitude+".";
     			if(previousloc)
     	     	{
     	     		msg +="(last known location)";
     	     	}
     	         
     		}
     	}
     	
     	
         
         
         for(int i =0 ; i< MAX_CNUMBER ; i++)
   		{
        	
        	if(settings.getString("contactnumber"+i, null)!=null)
           sendsms(settings.getString("contactnumber"+i, null),msg+" -"+username);
   		
   		}
         
			 Intent myIntent = new Intent(this, digitalwitness.class);
			 Bundle b = new Bundle();
		     b.putDouble("latitude", latitude);
		     b.putDouble("longitude",longitude);
		     myIntent.putExtras(b);
		     myIntent.putExtra("android.intent.extra.durationLimit", 650000);
			this.startActivityForResult(myIntent,0);
			
			
}
	  private void gpsSwitch()
	   	{
	   		LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
	   		gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	   		networkStatus = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	   			
	   		if (!gpsStatus && !networkStatus) {	
	   			final Intent poke = new Intent();
	               poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
	               poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	               poke.setData(Uri.parse("3")); 
	               sendBroadcast(poke);
	   			
	   		}
	   	}

		protected void sendsms(String Phnumber,String message)
		{
			
		try {
		
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(Phnumber, null, message, null, null);
	
		} catch (Exception e) {

			Toast.makeText(getApplicationContext(),	"" + getString(R.string.smsfailed),Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		}
		
		protected void address()
		{
			
			Geocoder revaddress = new Geocoder(getBaseContext(), Locale.getDefault());
			try {
				Toast.makeText(getApplicationContext(), ""+latitude+","+longitude, Toast.LENGTH_SHORT).show();
				List<Address> list = revaddress.getFromLocation(latitude, longitude, 1);
				addressstr = null;
				if (list != null && list.size() > 0) {
                    Address address = list.get(0);
                    // sending back first address line and locality
                    addressstr = address.getAddressLine(0) + ", " + address.getLocality();
                  
                }
						
			} catch (IOException e) {
				addressstr =null;
				
				e.printStackTrace();
			}
			 
		}
		
		public final boolean isInternetOn() {
			 ConnectivityManager cm =
				        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

				    return cm.getActiveNetworkInfo() != null && 
				       cm.getActiveNetworkInfo().isConnectedOrConnecting();
			}

		
			private void setMobileDataEnabled() {
			    final ConnectivityManager conman = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			    Class conmanClass = null;
				try {
					conmanClass = Class.forName(conman.getClass().getName());
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    Field iConnectivityManagerField = null;
				try {
					iConnectivityManagerField = conmanClass.getDeclaredField("mService");
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    iConnectivityManagerField.setAccessible(true);
			    Object iConnectivityManager = null;
				try {
					iConnectivityManager = iConnectivityManagerField.get(conman);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    Class iConnectivityManagerClass = null;
				try {
					iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    Method setMobileDataEnabledMethod = null;
				try {
					setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    setMobileDataEnabledMethod.setAccessible(true);

			    try {
					setMobileDataEnabledMethod.invoke(iConnectivityManager, true);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			  finish();
		  }
		}

		
}
