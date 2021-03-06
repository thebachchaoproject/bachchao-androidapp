// adapter to handle custom UI of the contacts list 
package com.saakshin.bachao;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;



import android.view.LayoutInflater;
import android.content.Context;

public class contactslistAdapter extends BaseAdapter {
	private static ArrayList<contactsresults> contactsArrayList;
	 
	 private LayoutInflater mInflater;
	 Context mContext;
	 
 public contactslistAdapter(Context context, ArrayList<contactsresults> results) {
		  contactsArrayList = results;
		  mInflater = LayoutInflater.from(context);
		  mContext = context;
		 }
	 
	@Override
	public int getCount() {
		return contactsArrayList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return contactsArrayList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup parent) {
		ViewHolder holder;
		  if (arg1 == null) {
		   arg1 = mInflater.inflate(R.layout.custom_contactlist_element,parent,false);
		   holder = new ViewHolder();
		   holder.txtName = (TextView) arg1.findViewById(R.id.name);
		   holder.imageuri = (ImageView)arg1.findViewById(R.id.picture);
		   arg1.setTag(holder);
		  } else {
		   holder = (ViewHolder) arg1.getTag();
		  }
		  
		  holder.txtName.setText(contactsArrayList.get(arg0).getName());
		  if(contactsArrayList.get(arg0).getPhoto()!= null)
		  holder.imageuri.setImageURI(contactsArrayList.get(arg0).getPhoto());
		  else
		  holder.imageuri.setImageDrawable(mContext.getResources().getDrawable(R.drawable.contactimagecolor));
		  return arg1;
	}
	
	static class ViewHolder {
		ImageView imageuri;
		TextView txtName;

		 }

}

