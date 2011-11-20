package edu.berkeley.cs169.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import edu.berkeley.cs169.R;

public class MainPreferenceActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preference);
	}
}
