// class to store values of contact to enable custom UIof contacts
package com.saakshin.bachao;

import android.net.Uri;

public class contactsresults {
	private String name = "";
	 private String phone = "";
	 private String email = "";
	 private String id = "";
	 private Uri photo_uri = null;
	 public contactsresults(){
		 super();
	 }
	 public contactsresults(String contactName, String contactPhoneNumber, String email,String id,Uri photo) 
	    {
	        super();
	        this.name = contactName;
	        this.phone = contactPhoneNumber;
	        this.email = email;
	        this.id =id;
	        this.photo_uri = photo;
	    }
	 
	 public void setName(String name) {
	  this.name = name;
	 }
     
	 public String getName() {
	  return name;
	 }

	 public void setEmail(String email) {
	  this.email = email;
	 }

	 public String getEmail() {
	  return email;
	 }

	 public void setPhone(String phone) {
	  this.phone = phone;
	 }

	 public String getPhone() {
	  return phone;
	 }
	 
	 public void setId(String id) {
		  this.id = id;
		 }

		 public String getId() {
		  return id;
		 }
		 public void setPhoto(Uri photo_uri ) {
			  this.photo_uri = photo_uri;
			 }

			 public Uri getPhoto() {
			  return photo_uri;
			 }
}
