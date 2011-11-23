package edu.berkeley.cs169.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import edu.berkeley.cs169.R;

//preference screen
public class MainPreferenceActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// preference menu is defined in XML
		addPreferencesFromResource(R.xml.preference);
	}
}
