package edu.berkeley.cs169.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.view.View;
import edu.berkeley.cs169.R;


//utility class that uses Android specific tools
public class AndroidUtils {
	// Takes phone number and an message and sends SMS message.
	public static void sendSMSHelper(String phoneNumber, String message, Context c) {
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, null, null);
		
		ContentValues values = new ContentValues();
		values.put("address", phoneNumber);
		values.put("body", message);
		c.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
	}

	// Blanks the screen for battery saving purpose.
	public static void blankScreen(Activity activity) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(activity);
		activity.findViewById(R.id.black).setVisibility(
				prefs.getBoolean("blank", false) ? View.VISIBLE : View.GONE);
	}
}
