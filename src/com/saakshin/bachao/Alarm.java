// class to handle wake ups based on location update 
package com.saakshin.bachao;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;

import android.os.PowerManager;
import android.telephony.SmsManager;



public class Alarm extends BroadcastReceiver 
{    
	SharedPreferences settings;
	public static final String PREFS = "BachchaoPrefsFile";
	private static final int MAX_CNUMBER = 3;
	 int updatetime;
     int timeelapsed;
     String updatemsg;
		String addressstr;
		double latitude;
		double longitude;
		LocationFinder gps;
     @Override
     public void onReceive(Context context, Intent intent) 
     {   
         PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
         PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
         wl.acquire();
         settings = context.getSharedPreferences(PREFS, 0);
	     updatetime =  settings.getInt("locationupdateinterval", 0);
	     timeelapsed = settings.getInt("timeelapsed", -1); 
	      updatemsg = settings.getString("updatemsg", "I am at");
	     if (updatetime != 0)
	     {
	    	 if(timeelapsed == updatetime*6*600000 || timeelapsed == -1)
	    	 {  
	    		 gps = new LocationFinder(context);
	    		 if(timeelapsed!=-1)
	    		 readLocation(context);
	    		 timeelapsed = 900000;
	    		 
	    	 }else
	    	 {
	    		
	    		 timeelapsed +=900000 ;
	    		 
		 
	    	 }
	    	 SharedPreferences.Editor editor= settings.edit();
 			 editor.putInt("timeelapsed",timeelapsed);
 			 editor.commit();
 		
	     }
         wl.release();
     }

 public void SetAlarm(Context context, int updatetime)
 {
     AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
     Intent i = new Intent(context, Alarm.class);
     PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
     am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 10 *6 , pi); // Millisec * Second * Minute
 }

 public void CancelAlarm(Context context)
 {
     Intent intent = new Intent(context, Alarm.class);
     PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
     AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
     alarmManager.cancel(sender);
 }
  
 private void readLocation(Context context)
 {
	  
      
	     
      // check if GPS enabled
      if(gps.locationavailable()){
           latitude = gps.getLatitude();
           longitude = gps.getLongitude();
         
  	}
  
      
        if((latitude==0&&longitude==0) )
        {
       	
       	 gps.getLastLocation();
			  	 latitude=gps.getLatitude();
			  	 longitude = gps.getLongitude();
        }
       
  	
      if(settings.getBoolean("address", false))
		{
  	  
  	   if(isOnline(context))
	        {
  		
			address(context);
	        }
		}
    for(int i =0 ; i< MAX_CNUMBER ; i++)
		{
  	  String msg = updatemsg;
    		if(settings.getBoolean("address", false))
    		{   
    		        if(isOnline(context))
    		        {
    				
    				if(addressstr!=null)
    				msg += " "+addressstr;
    		        }
    			
    		}
    		if(settings.getBoolean("latlong", true))
    		{
    			if(latitude!=0.0 || longitude!=0.0)
    			if(addressstr==null)
    			{
    				msg += " https://maps.google.com/maps?q=" +latitude+","+longitude+".";
    			}
    			else
    			msg += " "+latitude+","+longitude+".";
    		}else if(!isOnline(context)||(addressstr==null))
    		{
    			if(latitude!=0.0 || longitude!=0.0)
    				msg += " https://maps.google.com/maps?q=" +latitude+","+longitude+".";
    			
    		}
    	
    		if(settings.getString("contactnumber"+i, null)!=null)
    		sendsms(settings.getString("contactnumber"+i, null),msg, context);
		
		}
 }
 protected void sendsms(String Phnumber,String message,Context context)
	{
		
	try {
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(Phnumber, null, message, null, null);
		
		} catch (Exception e) {		
		e.printStackTrace();
		}
}
 protected void address(Context context)
{
		Geocoder revaddress = new Geocoder(context, Locale.getDefault());
		try {
    		

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
	
 public boolean isOnline(Context context) {
	
	 ConnectivityManager cm =
	        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

	
	    return cm.getActiveNetworkInfo() != null && 
	       cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
 
}

