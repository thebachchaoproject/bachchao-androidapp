//settings UI page

package com.saakshin.bachao;


import java.util.Set;

import android.location.LocationManager;
import android.net.Uri;

import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract.PhoneLookup;
import android.widget.Button;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.telephony.TelephonyManager;

import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.EditorInfo;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;





public class settingsActivity extends ActionBarActivity {
	private static final int REQUEST_CODE = 0;
	Button contact1, contact2, contact3,incduration,decduration ; 
	TextView durval;
	CheckBox address , latlong;
	EditText alertmsg , updatemsg,username;
	public static final String PREFS = "BachchaoPrefsFile";
	public static int MAX_CNUMBER = 3;
	public static int itemclicked = 0;
	 SharedPreferences settings;
	 Intent returndata;
	 contactsarray[] contactsinfo;
	  boolean gpsStatus = false;
	  boolean networkStatus = false;
	  int durationvalindex;
	  EditText phoneNumber;
	  String Phoneno;

	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			
		setContentView(R.layout.settings_view); 
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.settingsheader));
		actionBar.setIcon(R.drawable.return_icon_settingspage);
		actionBar.setTitle("");
		actionBar.show();
		actionBar.setHomeButtonEnabled(true);
		
		
		   contactsinfo= new contactsarray[MAX_CNUMBER];
		   for (int i=0;i< contactsinfo.length;i++)
			 contactsinfo[i] = new contactsarray();  
		   settings = getSharedPreferences(PREFS, 0);
		   setInfo();
	      
	       getElementsById();
	       buttonClickListener();
	       gpsSwitch();	   
	}

	
	private void getElementsById()
	{
		contact1 = (Button)findViewById(R.id.contact1);
		contact2 = (Button)findViewById(R.id.contact2);
		contact3 = (Button)findViewById(R.id.contact3);
		incduration = (Button)findViewById(R.id.increasedur);
		decduration = (Button)findViewById(R.id.decreasedur);
		address = (CheckBox)findViewById(R.id.checkBox2);
		latlong = (CheckBox)findViewById(R.id.checkBox1);
		
		if(contactsinfo[0].contactname!=null )
		{
			contact1.setText(contactsinfo[0].contactname);
			contact1.setBackgroundDrawable(null);
			contact1.setBackgroundDrawable(getResources().getDrawable(R.drawable.savedcontactbox));
		}else
		contact1.setText("+");
		if(contactsinfo[1].contactname!=null)
		{
			contact2.setText(contactsinfo[1].contactname);
			contact2.setBackgroundDrawable(null);
			contact2.setBackgroundDrawable(getResources().getDrawable(R.drawable.savedcontactbox));
		}else
			contact2.setText("+");
		if(contactsinfo[2].contactname!=null)
		{
			contact3.setText(contactsinfo[2].contactname);
			contact3.setBackgroundDrawable(null);
			contact3.setBackgroundDrawable(getResources().getDrawable(R.drawable.savedcontactbox));
		}else
			contact3.setText("+");
		
	 
		durationvalindex = settings.getInt("locationupdateindex", 0);
		durval = (TextView)findViewById(R.id.duration);
		Resources res = getResources();
		TypedArray durationarray = res.obtainTypedArray(R.array.updateInterval);
		String durationval = durationarray.getString(durationvalindex);
		durval.setText(durationval);
		
		alertmsg = (EditText)findViewById(R.id.customalertmsg);
		String custommsg = settings.getString("alertmsg", "Help!I am in an emergency at ");
		alertmsg.setText(custommsg);
		String upmsg = settings.getString("updatemsg", "I am at");
		updatemsg =(EditText)findViewById(R.id.trackmsg);
		updatemsg.setText(upmsg);
		
		username = (EditText)findViewById(R.id.sendername);
		String usernm = settings.getString("username","");
		username.setText(usernm);
		
		Boolean addressreq = settings.getBoolean("address",false);
		Boolean latlongreq = settings.getBoolean("latlong", true);
		address.setChecked(addressreq);
		latlong.setChecked(latlongreq);
		
		registerForContextMenu(contact1);
	    registerForContextMenu(contact2);
	    registerForContextMenu(contact3);
	}
	
	
	private void setInfo()
	{
		for(int i =0 ; i< MAX_CNUMBER ; i++)
		{
			contactsinfo[i].contactname = settings.getString("contactname"+i, null);
			contactsinfo[i].contactid = settings.getString("contactid"+i, null);
		}
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
	
	
	private void buttonClickListener()
	{
		contact1.setOnClickListener(new OnClickListener()
	     {
			 @Override
			public void onClick(View w)
			 { 
				 
				 loadcontacts(w.getId(), contact1.getText().toString(),1);
				 
			 }
			
	     });
		
		contact2.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View w)
			{
				
				loadcontacts(w.getId(),contact2.getText().toString(),2);
			}
		});
		
		contact3.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View w)
				{
					
					loadcontacts(w.getId(),contact3.getText().toString(),3);
					
				}
		});
		contact1.setOnLongClickListener(new OnLongClickListener() { 
	        @Override
	        public boolean onLongClick(View v) {
	        	itemclicked = 1;
	        	if (contact1.getText()!="")
	        	openContextMenu(v);
	            return true;
	        }
	    });
		contact2.setOnLongClickListener(new OnLongClickListener() { 
	        @Override
	        public boolean onLongClick(View v) {
	        	itemclicked = 2;
	        	if (contact2.getText()!="")
	        	openContextMenu(v);
	            return true;
	        }
	    });
		contact3.setOnLongClickListener(new OnLongClickListener() { 
	        @Override
	        public boolean onLongClick(View v) {
	        	itemclicked = 3;
	        	if (contact3.getText()!="")
	        	openContextMenu(v);
	            return true;
	        }
	    });
	    
		incduration.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View w)
			{
				if(durationvalindex< 5 )
				{
					Resources res = getResources();
					SharedPreferences.Editor editor = settings.edit();
					editor.putInt("locationupdateindex",++durationvalindex);
					editor.putInt("timeelapsed",-1);
					TypedArray durationval = res.obtainTypedArray(R.array.updateIntervalValues);
					int durationvalue = durationval.getInt(durationvalindex,0);
					editor.putInt("locationupdateinterval", durationvalue);
					editor.commit();
					
					TypedArray durationarray = res.obtainTypedArray(R.array.updateInterval);
					String durvl = durationarray.getString(durationvalindex);
					durval.setText(durvl);
					if(!durvl.contains("Never"))
					Toast.makeText(getApplicationContext(), ""+getString(R.string.dursmswarnpre)+" "+durvl+" "+getString(R.string.dursmswarnpost), Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
		decduration.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View w)
			{
				if(durationvalindex > 0 )
				{
					Resources res = getResources();
					SharedPreferences.Editor editor = settings.edit();
					editor.putInt("locationupdateindex",--durationvalindex);
					TypedArray durationval = res.obtainTypedArray(R.array.updateIntervalValues);
					int durationvalue = durationval.getInt(durationvalindex,0);
					editor.putInt("locationupdateinterval", durationvalue);
					editor.putInt("timeelapsed",-1);
					editor.commit();
					
					TypedArray durationarray = res.obtainTypedArray(R.array.updateInterval);
					String durvl = durationarray.getString(durationvalindex);
					durval.setText(durvl);
					if(!durvl.contains("Never"))
					Toast.makeText(getApplicationContext(),  ""+getString(R.string.dursmswarnpre)+" "+durvl+" "+getString(R.string.dursmswarnpost), Toast.LENGTH_SHORT).show();
				}
				
			}
		});
				
		address.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View w)
			{
			  
			   if (address.isChecked()) {
				Toast.makeText(getApplicationContext(), ""+getString(R.string.internetconnectset), Toast.LENGTH_SHORT).show ();
				  
			}
			}
			});
		
	}
	
	private void rungpsService()
	{
		 
		AlarmManager alarmmgr=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
	    Intent i=new Intent(getApplicationContext(), Alarm.class);
	    PendingIntent pi=PendingIntent.getBroadcast(getApplicationContext(), 0,
	                                              i, 0);
	    
	    alarmmgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
	                      SystemClock.elapsedRealtime(),
	                      900000,
	                      pi);
	}
	
	private void loadcontacts(int id,String contactname, int num)
	{
		Intent myIntent = new Intent(this, contactslistActivity.class);
		Bundle b = new Bundle();
        b.putInt("Triggeritem", id);
        b.putString("contactname", contactname);
        
        for(int i =0 ; i< MAX_CNUMBER ; i++)
		{
        	b.putString("contact"+i, contactsinfo[i].contactid );
		}
        b.putInt("triggernum",num);
        b.putBoolean("contactexists",contactsinfo[num-1].contactid==null);
         myIntent.putExtras(b);
		 this.startActivityForResult(myIntent,REQUEST_CODE);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	  
	   switch (item.getItemId()) 
	   {
	     case android.R.id.home:
	    	 saveSettings();
	    	 finish();
	        return true;
	     default:
	        return super.onOptionsItemSelected(item);
	   }
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
	    if (data.hasExtra("triggeritem")) {
	   
	    	Button contactbutton = (Button)findViewById(data.getExtras().getInt("triggeritem"));
	    	if(data.getExtras().getString("contactid")!= null)
	    		contactbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.savedcontactbox));
	    		contactbutton.setText(data.getExtras().getString("contactname"));
	   
	   returndata = data;
	  if (returndata != null)
		{
			
			int i;
			
			if (returndata.getExtras().getInt("triggeritem") == R.id.contact1)
			     i = 0 ;
			 else if(returndata.getExtras().getInt("triggeritem")==R.id.contact2)
				i=1;
			else 
				i= 2; 	
		
			 contactsinfo[i].contactname =  returndata.getExtras().getString("contactname");
			 contactsinfo[i].contactid =  returndata.getExtras().getString("contactid");
	    
	   
		}
	    }
	  }
	} 
	@Override
	public void onBackPressed() {
		saveSettings();	
    	finish();
    	super.onBackPressed();
    }


	private void saveSettings() {
		SharedPreferences.Editor editor = settings.edit();
		String msg = alertmsg.getText().toString();
		editor.putString("alertmsg",msg );
		msg = updatemsg.getText().toString();
		editor.putString("updatemsg", msg);
		String usernm = username.getText().toString();
		editor.putString("username",usernm);
		Boolean addressreq = address.isChecked();
		
		editor.putBoolean("address",addressreq);
		Boolean latlongreq = latlong.isChecked();
		
		editor.putBoolean("latlong",latlongreq);
		editor.commit();	
		rungpsService();	
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	        ContextMenuInfo menuInfo) {

	    
	    MenuItem remove = menu.add("" + getString(R.string.delete));

	    remove.setOnMenuItemClickListener(new OnMenuItemClickListener() {
	        @Override
	        public boolean onMenuItemClick(MenuItem item) {
				RemoveContact();
	        	return true;
	           
	    }});

	    super.onCreateContextMenu(menu, v, menuInfo);
	}


private void RemoveContact() {
	

	switch(itemclicked){
		case 1: 	{

				contact1.setText("+");
				contact1.setBackgroundDrawable(null);
				contact1.setBackgroundDrawable(getResources().getDrawable(R.drawable.addcontactbox));

			break;
			}
		case 2:{
			contact2.setText("+");
			contact2.setBackgroundDrawable(null);
			contact2.setBackgroundDrawable(getResources().getDrawable(R.drawable.addcontactbox));
			
			break;
		}
		case 3:{
			contact3.setText("+");
			contact3.setBackgroundDrawable(null);
			contact3.setBackgroundDrawable(getResources().getDrawable(R.drawable.addcontactbox));
			break;	
		}
		
		}  

	SharedPreferences.Editor editor = settings.edit();
	editor.remove("contactname"+(itemclicked-1));
	editor.remove("contactnumber"+(itemclicked-1));
	editor.remove("contactid"+(itemclicked-1));
	editor.commit();
	contactsinfo[itemclicked-1].contactid = null;
}
	
public boolean contactExists( String number ,String id, int item) {
    Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
    String[] mPhoneNumberProjection = { PhoneLookup._ID, PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME };
    Cursor cur = this.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
    try {
    	
        if (cur.moveToFirst()) {
        	String thisid =cur.getString(
                    cur.getColumnIndex(PhoneLookup._ID));
        	if(thisid==id)
            return true;
        	else
        	{
        		contactsinfo[item-1].contactid = null;
        		return false;
        	}
        	
        }
    } finally {
        if (cur != null)
            cur.close();
    }
   
    return false;
}


private void retrievePhoneNumber() {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
                String phoneNo = null;
                phoneNumber = (EditText) findViewById(R.id.phonenumber);
                
                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                phoneNo = (String) getMy10DigitPhoneNumber(telephonyManager);
                
                if(phoneNo.length() != 0){
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putStringSet("PhoneNumber", (Set<String>) phoneNumber);
                    editor.commit();
                    phoneNumber.setText(phoneNo);
                    
                    
                }else{
                    Phoneno = settings.getString("PhoneNumber", "");
                    phoneNumber.setText(Phoneno);
                    if(Phoneno.length()==0 && Phoneno.length() < 10){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(settingsActivity.this);
                    
                    // Setting Dialog Title
                    alertDialog.setTitle("Phone Number");
                    
                    // Setting Dialog Message
                    alertDialog.setMessage("Enter Phone Number");
                    
                    final EditText input = new EditText(settingsActivity.this);
                    input.setHeight(100);
                    input.setWidth(340);
                    input.setGravity(Gravity.LEFT);

                    input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                      alertDialog.setView(input);
                      
                      // Setting Icon to Dialog
                      alertDialog.setIcon(R.drawable.ic_launcher);
                      
                   // Setting Negative "NO" Button
                      alertDialog.setNegativeButton("Cancel",
                              new DialogInterface.OnClickListener() {
                                  public void onClick(DialogInterface dialog, int which) {
                                      // Write your code here to execute after dialog
                                      dialog.cancel();
                                  }
                              });
                      
                   // Setting Positive "Yes" Button
                      alertDialog.setPositiveButton("Save",
                              new DialogInterface.OnClickListener() {
                                  public void onClick(DialogInterface dialog,int which) {
                                      // Write your code here to execute after dialog

                                      String phoneNum = input.getText().toString();
                                      SharedPreferences.Editor editor = settings.edit();
                                        editor.putString("PhoneNumber", phoneNum);
                                        editor.commit();
                                      if(phoneNum.length() >= 10 && phoneNum.length() < 14){
                                          Toast.makeText(settingsActivity.this, "Phone No: " + phoneNum, Toast.LENGTH_LONG).show();
                                          
                                      phoneNumber.setText(phoneNum);
                                      }else{
                                          Toast.makeText(settingsActivity.this, "Invalid Number", Toast.LENGTH_LONG).show();
                                          
                                          
                                      }
                                  }
                              });
                      
                   

                      // closed

                      // Showing Alert Message
                      alertDialog.show();
                    }
                }
    }


    private String getMy10DigitPhoneNumber(TelephonyManager telephonyManager) {
        // TODO Auto-generated method stub
        String s = getMyPhoneNumber();
        return s.substring(0);
    }


    private String getMyPhoneNumber() {
        // TODO Auto-generated method stub
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getLine1Number();
    }
	
}
	
	
