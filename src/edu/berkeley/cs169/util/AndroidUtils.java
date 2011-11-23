package edu.berkeley.cs169.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.view.View;
import edu.berkeley.cs169.R;

//utility class that uses Android specific tools
public class AndroidUtils {
	// Takes phone number and an message and sends SMS message.
	public static void sendSMSHelper(String phoneNumber, String message) {
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, null, null);
	}

	// Blanks the screen for battery saving purpose.
	public static void blankScreen(Activity activity) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(activity);
		activity.findViewById(R.id.black).setVisibility(
				prefs.getBoolean("blank", false) ? View.VISIBLE : View.GONE);
	}
}
