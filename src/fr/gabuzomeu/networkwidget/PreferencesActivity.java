package fr.gabuzomeu.networkwidget;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity {
	final String LOG_TAG="Network Widget";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	
	
	
	addPreferencesFromResource(R.xml.preferences);
	//Get the custom preference
	Preference customPref = (Preference) findPreference("vibrateNotification");
	
	
	
	}

	
	
}
