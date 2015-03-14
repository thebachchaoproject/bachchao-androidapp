package com.saakshin.bachao;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
 
public class Networkconnection {
     
    public static int CONNECTED = 1 ;
    public static int NOT_CONNECTED = 0;
     
     
    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
 
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
           return CONNECTED; 
        } 
        return NOT_CONNECTED;
    }


}
