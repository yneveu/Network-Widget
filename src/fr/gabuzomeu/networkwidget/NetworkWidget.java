package fr.gabuzomeu.networkwidget;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.Layout;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;



public class NetworkWidget extends AppWidgetProvider{

	AppWidgetManager widgetManager;
	ComponentName thisWidget;
	
	
	
	int currentDataType = -1;
	String networkName = "Unknown";
	boolean stateChanged=true;
	
	RemoteViews views;
	int currentLayout = R.layout.network_widget_black;
	
	final String LOG_TAG="Network Widget";
	
	private static final String WIFI_TOGGLE = "fr.gabuzomeu.NetworkWidget.WIFI_TOGGLE";
	private static final String NETWIDG_PREFS = "fr.gabuzomeu.NetworkWidget.NETWIDG_PREFS";

	@Override
	public void onReceive(Context context, Intent intent) {

		widgetManager = AppWidgetManager.getInstance(context);
		Log.d( LOG_TAG, "Current Layout: " + currentLayout );
		
		/**Called when the widget is destroyed*/
		
		final String action = intent.getAction();
		if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
			final int appWidgetId = intent.getExtras().getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
			if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				this.onDeleted(context, new int[] { appWidgetId });
			}
		
		
		/**Called on connectivity changes**/
		}else if( android.net.ConnectivityManager.CONNECTIVITY_ACTION.equals( action)  ){
			Log.d( LOG_TAG, "Connectivity change");
				
			
			ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
			if( connManager.getActiveNetworkInfo() != null)
				Log.d( LOG_TAG, connManager.getActiveNetworkInfo().getTypeName());

			/**Refresh the widget*/
			if( intent.getAction() != null){
				onUpdate(context, AppWidgetManager.getInstance(context), new int[]{ 0 });
			}
			
		/**Called when click on wifi icon**/	
		} else if( WIFI_TOGGLE.equals(action)){
			Log.d( LOG_TAG, "Toggle wifi");
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			if( !wifiManager.isWifiEnabled() ){
				Bitmap theImage;
				Resources res = context.getResources();
				theImage = BitmapFactory.decodeResource(res, R.drawable.wifi_connecting_32);
				theImage = Bitmap.createBitmap(theImage);
				views = new RemoteViews(context.getPackageName(), currentLayout);
				views.setImageViewBitmap( R.id.ImageViewType, theImage );	
				
				
				if( widgetManager != null ){
					Toast.makeText(context, "Enabling wifi", Toast.LENGTH_SHORT).show(); 
					wifiManager.setWifiEnabled( true);
					Log.d( LOG_TAG, "WIFI ENABLING ICON" + action.toString() );
					//widgetManager.updateAppWidget( thisWidget, views);
				}
			}
			else {
				Toast.makeText(context, "Disabling wifi", Toast.LENGTH_SHORT).show();
				wifiManager.setWifiEnabled(false);
			}
		}
		
	/** Called when click on right part*/	
	 else if( NETWIDG_PREFS.equals(action)){
			Log.d( LOG_TAG, "Open prefs");
			Intent prefsActivity = new Intent( context , PreferencesActivity.class);
			context.startActivity(prefsActivity);
			
			
	 }
		
		else {	
			if( action != null)
				Log.d( LOG_TAG, "Other Action" + action.toString() );
			else
				Log.d( LOG_TAG, "Action NULL" );

			super.onReceive(context, intent);
		}
	
		//onUpdate(context, AppWidgetManager.getInstance(context), new int[]{ 0 });
	
	}



	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		widgetManager = appWidgetManager;
		ComponentName thisWidget = new ComponentName(context, NetworkWidget.class);
		
		 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			if( prefs != null){
				String appearance = prefs.getString( "selectedAppearance", "transparent");
				Log.d(  LOG_TAG, "Appareance:" + appearance);
				
				if( appearance.compareTo( "transparent") == 0)
						currentLayout = R.layout.network_widget_transparent;
				else
						currentLayout = R.layout.network_widget_black;
				
			}else
				Log.d(  LOG_TAG, "Pref NULL");
		
		views = new RemoteViews(context.getPackageName(), currentLayout);
		ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);     
		Resources res = context.getResources();
		Bitmap theImage = BitmapFactory.decodeResource(res, R.drawable.unknown_32);
		int connType = -1;
		
		
		
		//Click on image to toggle wifi
		Intent intent = new Intent( context,  NetworkWidget.class);
		intent.setAction( WIFI_TOGGLE);
        PendingIntent pIntent =  PendingIntent.getBroadcast(context, 0, intent, 0);
		views.setOnClickPendingIntent(R.id.ImageViewType, pIntent);
		
		//Click on text to open prefs activity
		Intent prIntent = new Intent( context,  PreferencesActivity.class);
		prIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        		
        PendingIntent ppIntent = PendingIntent.getActivity(context, 0, prIntent, Intent.FLAG_ACTIVITY_NEW_TASK); 
        views.setOnClickPendingIntent(R.id.LinearLayout01, ppIntent);
		
		
       
        
        
		String networkInfo="";
		
		if( connManager.getActiveNetworkInfo() != null ){
			connType = connManager.getActiveNetworkInfo().getType();
			if( connType != currentDataType){
				stateChanged = true;
				Log.d(  LOG_TAG, "TRUE: Type changed:" + currentDataType + " -> " + connType);
				Log.d(  LOG_TAG, "Changement de type de connexion -> stateChanged a true");
			}

		
			switch( connType){
				case ConnectivityManager.TYPE_MOBILE:
					theImage = BitmapFactory.decodeResource(res, R.drawable.mobile_32);
					TelephonyManager telManager = (TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE);
					//	CellLocation loc = (CellLocation) telManager.getCellLocation();
					networkInfo = telManager.getNetworkOperatorName() + " " + telManager.getNetworkCountryIso().toUpperCase();
					break;
			
				case ConnectivityManager.TYPE_WIFI:
					theImage = BitmapFactory.decodeResource(res, R.drawable.wifi_32);
					WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
					networkInfo = wifiManager.getConnectionInfo().getSSID();
					break;
					
				default:
					theImage = BitmapFactory.decodeResource(res, R.drawable.unknown_32);
					networkInfo = "Unknown";
			};
			
			
			
			
			
			
			if( networkInfo.compareTo( networkName) != 0 ){
				Log.d(  LOG_TAG, "TRUE: Network Name changed:" + networkName + " -> " + networkInfo);
				Log.d(  LOG_TAG, "Changement de nom -> stateChanged a true");
				stateChanged = true;
			}
		
		//**Transitional state*/
		} else {
			networkInfo = "Unknown";
		}
			
			
		/**State have changed we do something*/
		if( stateChanged){
			/*Graphics changes*/
			theImage = Bitmap.createBitmap(theImage);
			views.setImageViewBitmap( R.id.ImageViewType, theImage );	
			views.setTextViewText( R.id.TextViewAdress, getLocalIpAddress() );
			views.setTextViewText( R.id.TextViewNetwork , networkInfo );

			Log.d(  LOG_TAG, "Update the view -> " + networkName + " TYPE: " + currentDataType);
		//	Log.d(  LOG_TAG, "Network Name changed:" + networkName + " -> " + networkInfo);
			//Log.d(  LOG_TAG, "Type changed:" + currentDataType + " -> " + connType);
			
			
		
			//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
			if( prefs != null ){
				Log.d(  LOG_TAG, "??????????VIBRRRR-> " + networkName + " TYPE: " + currentDataType);
				if( (networkInfo.compareTo("Unknown") != 0) && ( connType != -1) ){
					Log.d(  LOG_TAG, "VIBRRRR-> " + networkName + " TYPE: " + currentDataType);
					boolean bVibrate = prefs.getBoolean( "vibrateNotification", false);
					if( bVibrate){
						Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
						v.vibrate(500); 
					}
				}
			}
		
			/**reset state*/
			Log.d(  LOG_TAG, "MAJ des variables -> " + networkInfo);
			Log.d(  LOG_TAG, "MAJ des variables -> " + connType);
			networkName = networkInfo;
			currentDataType =  connType;
			stateChanged = false;
			Log.d(  LOG_TAG, "stateChanged a false");
			
		}
		
		
		
		for (int i = 0; i < appWidgetIds.length; i++) {
	          appWidgetManager.updateAppWidget(appWidgetIds[i], views);
	     }
		
		
		if( widgetManager != null )
			widgetManager.updateAppWidget( thisWidget, views);
		
		
		
		stateChanged = false;
		Log.d(  LOG_TAG, "stateChanged a false");
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

	
	
	
	
	public void onHandleAction(Context context, int appWidgetId, Uri data) {
        
       // RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.network_widget );
        String controlType = data.getFragment();

        Log.e(LOG_TAG, "onHandleAction: " + controlType); 

    }
	
	
	
	
	
	
	
	
}




