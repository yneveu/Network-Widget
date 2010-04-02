package fr.gabuzomeu.networkwidget;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity {
	final String LOG_TAG="Network Widget";

	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	
	FlurryAgent.onStartSession( getBaseContext(), "9KD9EUVHIJTQH66FMS4X");
	
	
	addPreferencesFromResource(R.xml.preferences);
	
	
	//Add done button
	Button doneButton =new Button(this);
    doneButton.setText("Done");
    doneButton.setPadding(40, 0, 40, 0);
    doneButton.setOnClickListener(new OnClickListener() {
        
        private SharedPreferences sp;

    	public void onClick(View v) {
                    
    				Context context = getBaseContext();
    				setResult(RESULT_OK);
                    
                  //Get the custom preference
                	//Preference customPref = (Preference) findPreference("vibrateNotification");
                    int appWidgetId = -1;
                	
                	//Context context = ;
                    
                    Intent intent = getIntent();
                	Bundle extras = intent.getExtras();
                	if (extras != null) 
                	    appWidgetId = extras.getInt( AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                	
                	AppWidgetManager appWidgetManager = AppWidgetManager.getInstance( getBaseContext());
                	
                	
                	sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                	
                	String appearance = sp.getString( "selectedAppearance", "transparent");
                	
                	RemoteViews views;
                	
                	if( appearance.compareTo( "black") == 0 )
                		views = new RemoteViews( getBaseContext().getPackageName(), R.layout.network_widget_black);
                	else
                		views = new RemoteViews( getBaseContext().getPackageName(), R.layout.network_widget_transparent);
                	
                	
                	
                	
                	
                	
                	/****DUPLICATE CODE FROM WIDGET*/
                	/*###################################################*/
                	
                	String networkInfo="";
                	
                	ConnectivityManager connManager = (ConnectivityManager)getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);     
            		Resources res = getBaseContext().getResources();
            		Bitmap theImage = BitmapFactory.decodeResource(res, R.drawable.unknown_32);
            		int connType = -1;
                	
                	if( connManager.getActiveNetworkInfo() != null ){
            			connType = connManager.getActiveNetworkInfo().getType();
            			

            		
            			switch( connType){
            				case ConnectivityManager.TYPE_MOBILE:
            					theImage = BitmapFactory.decodeResource(res, R.drawable.mobile_32);
            					TelephonyManager telManager = (TelephonyManager) getBaseContext().getSystemService( Context.TELEPHONY_SERVICE);
            					//	CellLocation loc = (CellLocation) telManager.getCellLocation();
            					networkInfo = telManager.getNetworkOperatorName() + " " + telManager.getNetworkCountryIso().toUpperCase();
            					break;
            			
            				case ConnectivityManager.TYPE_WIFI:
            					theImage = BitmapFactory.decodeResource(res, R.drawable.wifi_32);
            					WifiManager wifiManager = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
            					networkInfo = wifiManager.getConnectionInfo().getSSID();
            					break;
            					
            				default:
            					theImage = BitmapFactory.decodeResource(res, R.drawable.unknown_32);
            					networkInfo = "Unknown";
            			};
                	}
                	
                	
                	theImage = Bitmap.createBitmap(theImage);
        			views.setImageViewBitmap( R.id.ImageViewType, theImage );	
        			views.setTextViewText( R.id.TextViewAdress, getLocalIpAddress() );
        			views.setTextViewText( R.id.TextViewNetwork , networkInfo );
        			
        			//Click on image to toggle wifi
        			Intent clIntent = new Intent( context,  NetworkWidget.class);
        			clIntent.setAction( NetworkWidget.WIFI_TOGGLE);
        	        PendingIntent pIntent =  PendingIntent.getBroadcast(context, 0, clIntent, 0);
        			views.setOnClickPendingIntent(R.id.ImageViewType, pIntent);
        			
        			//Click on text to open prefs activity
        			Intent prIntent = new Intent( Settings.ACTION_WIFI_SETTINGS);
        			//context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        			prIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        	        PendingIntent ppIntent = PendingIntent.getActivity(context, 0, prIntent, Intent.FLAG_ACTIVITY_NEW_TASK); 
        	        views.setOnClickPendingIntent(R.id.LinearLayout01, ppIntent);
                	
            		/*#####################################################*/
                	
                	
                	
                	
                	
                	
                	
                	
                	
                	
                	
                	
                	
                	
                	
                	
                	
                	if( appWidgetId != -1)
                		appWidgetManager.updateAppWidget( appWidgetId, views);
                	
                	
                	
                	Intent resultValue = new Intent();
                	resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                	
                	setResult(RESULT_OK, resultValue);
                	
                	
                	
                	
                	finish();
                	
        }
    });
    
    RelativeLayout layout = new RelativeLayout(this);
    layout.setLayoutParams( new  ViewGroup.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ) );
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
    
    layout.addView(doneButton, params);
    
    addContentView(layout, params);
	
	
	
	
	}

	
	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
						// return intf.getDisplayName() + " : " + inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(LOG_TAG, ex.getMessage());
		}
		return null;
	}
	
	
}
