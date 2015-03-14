// Activity is load contacts list 

package com.saakshin.bachao;



import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import android.text.Editable;
import android.text.TextWatcher;


import android.view.MenuItem;
import android.view.Gravity;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;


import java.util.ArrayList;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


public class contactslistActivity extends ActionBarActivity
{
    ArrayAdapter<String> adapter;
    ListView listView;
    Button image;
    RelativeLayout rl;
    int itemid;
    String name,contactemail,contactnumber,contactid;
    public static final String PREFS = "BachchaoPrefsFile";
    SharedPreferences settings;
     public static int MAX_CNUMBER = 3;
    Bundle settingsb;
    boolean contactexists = true;
    ActionBar actionBar;
    int num;
    boolean alreadyexists;
    TextView nametxt;
    ArrayList<contactsresults> contactsResults ;
	EditText Search;
	int textlength;
	contactsresults fullObject;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.from_contacts);
      
        setActionBarUI();
       
      	   
        findViewsById();
        contactnumber="";
        settingsb= getIntent().getExtras();
        Search = (EditText) findViewById(R.id.searchcontacts);
		itemid = settingsb.getInt("Triggeritem");
	    contactexists = settingsb.getBoolean("contactexists");
	    num = settingsb.getInt("triggernum");
	    
	    
	    if(contactexists==false)
	    {    	
	    actionBar.setTitle(" "+ getString(R.string.replacecontact));
	    addContactlayout();	    	 
	    }
	    
	    
		settings = getSharedPreferences(PREFS, 0);
       
        final ProgressDialog progDialog = ProgressDialog.show(this, "",
        		  "" + getString(R.string.contactsloading), true);
   
        new Thread() {
            public void run() {
                try {
                	contactsResults = getContactsResult();
                	 runOnUiThread(new Runnable() { 
                		    @Override 
                		    public void run() { 
                		    	 listonclick();
                		} 
                		 });
                	
                     
                     
                } catch (Exception e) {
                }
                progDialog.dismiss();
               
            }
        }.start();
        
       
        searchcode();
    }   
    
private void listonclick() {
    
    listView.setAdapter(new contactslistAdapter(this, contactsResults));
    listView.setOnItemClickListener(new OnItemClickListener()
    {
    	@Override
    	  public void onItemClick(AdapterView<?> parent, View view,
    	    int position, long id) {
 
    		
    		
    		Object selectedItem = listView.getItemAtPosition(position);
    		 fullObject = (contactsresults)selectedItem;
    		 
    	          
    	                	name = fullObject.getName();
    	                	contactnumber = fullObject.getPhone();
    	                	contactid = fullObject.getId();    		 
    	                	view.setSelected(true);
    	                	alreadyexists = false;
    	                	for(int i =0 ; i< MAX_CNUMBER ; i++)
    	                	{
    	                		if(settings.getString("contactid"+i,null) != null)
    	                			if(settings.getString("contactid"+i, null).contentEquals( contactid))
    	                			{
    	                				alreadyexists = true;  
    	                				break;
    	                			}
    	                		if(settingsb.getString("contact"+i) != null)
    	                			if(settingsb.getString("contact"+i).contentEquals(contactid))
    	                			{
    	                				alreadyexists=true;
    	                				break;
    	                			}
    	                	}
    		
    	                	if(((contactnumber!="")&&(contactnumber.length()!=1)&&(contactnumber.length()<=13)&& (alreadyexists != true)) )
    	                	{
    	                		Intent data = new Intent();
    	                		data.putExtra("triggeritem", itemid);
    			 data.putExtra("contactid",contactid );
    			 data.putExtra("contactname",name );
    			 setResult(RESULT_OK, data);
    			 if (contactexists==false)
    			 {
    				 image.setText(name);
    				 nametxt.setText("");
    			 	 nametxt.setText(name);
    			 }	  
    	   		  else
    	   		  {
    	   			addContactlayout();
    	   			image.setText(name);
    	   			nametxt.setText("");
    	   			nametxt.setText(name);
    	   		  }
    			
    		 }
    		 else if(contactnumber.length()==0)
    		 {
    			 Toast.makeText(getApplicationContext(), " " + getString(R.string.cannotaddcontact), Toast.LENGTH_LONG).show();
    		 }else if((contactnumber.length()==1)||(contactnumber.length()>13))
    		 {
    			 Toast.makeText(getApplicationContext(), " " + getString(R.string.cannotaddinvalid), Toast.LENGTH_LONG).show();
    		 }
    		 else if(alreadyexists)
    		 { 
    			 Toast.makeText(getApplicationContext()," " + getString(R.string.contactallreadyinlist), Toast.LENGTH_LONG).show();
    			 if(settingsb.getString("contactname")==name)
 			 {
    			 
 			
    			 
 				 Intent data = new Intent();
 				 data.putExtra("triggeritem", itemid);
 				 data.putExtra("contactid",contactid );
 				 data.putExtra("contactname",name );
 				 setResult(RESULT_OK, data);
 				
 					 image.setText(name);
 					 nametxt.setText("");
 					 nametxt.setText(name);
 			 }
 			
 			 
 			 }
    		/*Search.setText("");*/
    		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    		imm.hideSoftInputFromWindow(Search.getWindowToken(), 0);
    	
  
    	  
    	
    	}
    });
	}

private void searchcode() {
	 Search.addTextChangedListener(new TextWatcher()
	    {
	    	public void afterTextChanged(Editable s)
	    	{
	                                                                    // Abstract Method of TextWatcher Interface.
	    	}
	    	public void beforeTextChanged(CharSequence s,
	    			int start, int count, int after)
	    	{
	    		// Abstract Method of TextWatcher Interface.
	    	}
	    	public void onTextChanged(CharSequence s,
	    			int start, int before, int count)
	    	{
	    		
	    		textlength = Search.getText().length();
	    		if( Search.getText().length()!=0)
	    		{ 
	    			ArrayList<contactsresults> searchcontactsResults = getContactsResults(Search.getText().toString());
	            
	    			listView.setAdapter(new contactslistAdapter(getApplicationContext(), searchcontactsResults));
	    		}
	    		else 
	    		{
	    			ArrayList<contactsresults> contactsResults = getContactsResult();
	                
	    	        listView.setAdapter(new contactslistAdapter(getApplicationContext(), contactsResults));
	    		}
	    	}
	    });
		
	}
    
    private void setActionBarUI() {
    	actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.headersettingspage));
        actionBar.setTitle(" " + getString(R.string.addcontact));
  		actionBar.setIcon(R.drawable.return_icon_settingspage);
  		actionBar.show();
  		actionBar.setHomeButtonEnabled(true);
	}


	private void addContactlayout() {
    	
   	 rl = (RelativeLayout)findViewById(R.id.chosencontact);
   	 LinearLayout lly = new LinearLayout(this);
   	 lly.setOrientation(LinearLayout.VERTICAL);  	  
   	 nametxt = new TextView(this);
  	 nametxt.setId(10);
  	 nametxt.setText(settingsb.getString("contactname"));
  	 nametxt.setTextColor(Color.parseColor("#848482"));
  	 nametxt.setTextSize(16);
  	 nametxt.setPadding(10, 0, 15, 20);
  	 TextView name = new TextView(this);
  	 name.setId(2);
  	name.setText(" " + getString(R.string.assignedcontact));
  	 name.setTextColor(Color.parseColor("#303030"));
  	 name.setPadding(0, 0, 15, 0);
	 name.setTextSize(16);
  
   	 image = new Button(this);
   	 image.setId(1);
   	 image.setPadding(0, 80, 0, 0);
   	 image.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
   	 image.setTextColor(Color.WHITE);
   	 image.setText(settingsb.getString("contactname"));
   	 image.setTextSize(12);
   	 LayoutParams params01 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	  	params01.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    params01.addRule(RelativeLayout.ALIGN_TOP);
	    params01.rightMargin=30;
	    lly.addView(name,params01);
	    LayoutParams params02 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	  	params02.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    params02.addRule(RelativeLayout.BELOW,name.getId());
	    params01.rightMargin=35;
	    lly.addView(nametxt,params02);  
   	 LayoutParams params2 = new RelativeLayout.LayoutParams(100,100);
   	    params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
   	    params2.rightMargin = 50;
   	    params2.topMargin = 20;
   	    params2.bottomMargin = 20;
   	 rl.addView(image,params2);
   	 image.setBackgroundResource(R.drawable.savedcontactbox);
   	 LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
   	  	params1.addRule(RelativeLayout.CENTER_VERTICAL);
   	  	params1.addRule(RelativeLayout.CENTER_IN_PARENT);
   	    params1.addRule(RelativeLayout.ALIGN_BASELINE , image.getId());
   	    params1.addRule(RelativeLayout.LEFT_OF, image.getId());
   	    params1.rightMargin = 15; 	    
   	    rl.addView(lly, params1);
   	 TextView placeholder = new TextView(this);
   	 	placeholder.setId(3);
   	 placeholder.setText(" " + getString(R.string.contactlist));
   	 	placeholder.setTextColor(Color.parseColor("#752620"));
   	 	placeholder.setTextSize(18);
	   LayoutParams params3 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	   		params3.addRule(RelativeLayout.BELOW,image.getId());
	   		params3.leftMargin = 40;
	   rl.addView(placeholder,params3);
   	 
	   contactexists=false;
	}


	private void findViewsById() {
        listView = (ListView) findViewById(android.R.id.list);
    }
    
    private ArrayList<contactsresults> getContactsResults(String searchname){
    	Cursor curs;
    	ArrayList<contactsresults> results = new ArrayList<contactsresults>();
    	
    	        Uri lkup = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, searchname);
    	        curs = getContentResolver().query(lkup, null,null,null, null);
    	
    	contactsresults cr1 = new contactsresults();
    	if (!curs.moveToFirst())
        {
        	Toast.makeText(getApplicationContext(), "" + getString(R.string.nomatchingcontacts), Toast.LENGTH_SHORT).show();
        	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    		imm.hideSoftInputFromWindow(Search.getWindowToken(), 0);
    		return results;
        }
    	
        while (curs.moveToNext())
        {
        	String image_uri= null;
            String name=curs.getString(curs.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String phoneNumber ="";
            String id = curs.getString(
                    curs.getColumnIndex(BaseColumns._ID));
            
            //getting the phone numbers 
            if (Integer.parseInt(curs.getString(
                    curs.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                 Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);
  	        while (pCur.moveToNext()) {
  	        	phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
  	        
  	        } 
  	      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
  	        image_uri = curs.getString(curs.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
  	      
  	        pCur.close();
	        
	        
      	                 
          
            if(phoneNumber != "")
            {
            	cr1 = new contactsresults();
            	cr1.setName(name);
            	cr1.setPhone(phoneNumber);
            	cr1.setId(id);
            	if(image_uri!= null)
            		cr1.setPhoto(Uri.parse(image_uri));
            	else
            		cr1.setPhoto(null);
            	results.add(cr1);
            }
            }
        }
        curs.close();
       
       
        return results;
        
    }
    
    private ArrayList<contactsresults> getContactsResult(){
    	Cursor curs;
    	ArrayList<contactsresults> results = new ArrayList<contactsresults>();
    	
    		
       curs = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,null,null,  ContactsContract.Contacts.DISPLAY_NAME + " ASC");
    	
    	
    	contactsresults cr1 = new contactsresults();
        while (curs.moveToNext())
        {
        	String image_uri= null;
            String name=curs.getString(curs.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String phoneNumber ="";
            String id = curs.getString(
                    curs.getColumnIndex(BaseColumns._ID));
           
            //getting the phone numbers 
            if (Integer.parseInt(curs.getString(
                    curs.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                 Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);
  	        while (pCur.moveToNext()) {
  	        	phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
  	        
  	        } 
  	      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
  	        image_uri = curs.getString(curs.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
  	      
  	        pCur.close();
	        
	        
      	                 
          
            if(phoneNumber != "")
            {
            	cr1 = new contactsresults();
            	cr1.setName(name);
            	cr1.setPhone(phoneNumber);
            	cr1.setId(id);
            	if(image_uri!= null)
            		cr1.setPhoto(Uri.parse(image_uri));
            	else
            		cr1.setPhoto(null);
            	results.add(cr1);
            }
            }
        }
        curs.close();
        
        return results;
    }
    
    
    public boolean onOptionsItemSelected(MenuItem item) 
	{
	   switch (item.getItemId()) 
	   {
	     case android.R.id.home:
	    	 	saveContact();
	    	
		
	    	 finish();
		        return true;
		     default:
		        return super.onOptionsItemSelected(item);
		   }
		}
    private void saveContact() {
    	 SharedPreferences.Editor editor = settings.edit();
    	 if(((contactnumber!="")&&(contactnumber.length()!=1)&&(contactnumber.length()<=13)&& (alreadyexists != true)) )
    	 {
				editor.putString("contactname"+(num-1),name);
				editor.putString("contactnumber"+(num-1),contactnumber);
				editor.putString("contactid"+(num-1),contactid);
		 editor.commit();
    	 }
		
	}

	@Override
	public void onBackPressed() {
    	saveContact();
    	finish();
    	super.onBackPressed();
    }
	
}