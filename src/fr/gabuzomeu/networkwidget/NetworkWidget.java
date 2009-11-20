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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;



public class NetworkWidget extends AppWidgetProvider{

	AppWidgetManager widgetManager;
	ComponentName thisWidget;
	final String LOG_TAG="Network Widget";
	
	private static final String WIFI_TOGGLE = "fr.gabuzomeu.NetworkWidget.WIFI_TOGGLE";


	@Override
	public void onReceive(Context context, Intent intent) {

		final String action = intent.getAction();
		if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
			final int appWidgetId = intent.getExtras().getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
			if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				this.onDeleted(context, new int[] { appWidgetId });
			}
		}else if( android.net.ConnectivityManager.CONNECTIVITY_ACTION.equals( action)  ){
			Log.d( LOG_TAG, "Connectivity change");

			ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
			if( connManager.getActiveNetworkInfo() != null)
				Log.d( LOG_TAG, connManager.getActiveNetworkInfo().getTypeName());


			if( intent.getAction() != null){

				onUpdate(context, AppWidgetManager.getInstance(context), new int[]{ 0 });

			}
			
			
		} else {	
			Log.d( LOG_TAG, "Other Action");

			super.onReceive(context, intent);
		}
	}



	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		widgetManager = appWidgetManager;
		ComponentName thisWidget = new ComponentName(context, NetworkWidget.class);
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.network_widget);
		ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);     
		Resources res = context.getResources();
		Bitmap theImage;
		int connType = -1;
		String networkInfo = "Unknown";
		if( connManager.getActiveNetworkInfo() != null ){
			connType = connManager.getActiveNetworkInfo().getType();

		}

		if( connType == ConnectivityManager.TYPE_MOBILE ){
			theImage = BitmapFactory.decodeResource(res, R.drawable.mobile_32);
			//ServiceState state = new ServiceState();
			//networkInfo = state.getOperatorAlphaLong();
			TelephonyManager telManager = (TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE);
			networkInfo = telManager.getNetworkOperatorName() + " " + telManager.getNetworkCountryIso().toUpperCase();
		}

		else if( connType == ConnectivityManager.TYPE_WIFI ){
			theImage = BitmapFactory.decodeResource(res, R.drawable.wifi_32);

			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			networkInfo = wifiManager.getConnectionInfo().getSSID();


		}else{
			theImage = BitmapFactory.decodeResource(res, R.drawable.wifi_32);

		}
		theImage = Bitmap.createBitmap(theImage);
		views.setImageViewBitmap( R.id.ImageViewType, theImage );	


		views.setTextViewText( R.id.TextViewAdress, getLocalIpAddress() );


		views.setTextViewText( R.id.TextViewNetwork , networkInfo  );

		Log.d(  LOG_TAG, "Update the view -> " + networkInfo);

		
		Intent intent = new Intent( WIFI_TOGGLE);
		
        PendingIntent pIntent =  PendingIntent.getBroadcast(context, 0, intent, 0);
		
		views.setOnClickPendingIntent(R.id.ImageViewType, pIntent);
		
		
		// Tell the AppWidgetManager to perform an update on the current App Widget
		if( widgetManager != null )
			widgetManager.updateAppWidget( thisWidget, views);


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
        
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.network_widget );
        String controlType = data.getFragment();

        Log.e(LOG_TAG, "onHandleAtion: " + controlType); 

    }
	
	
	
	
	
	
	
	
}




