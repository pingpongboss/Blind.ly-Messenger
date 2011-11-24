package edu.berkeley.cs169;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.PhoneLookup;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.TelephonyManager;
import edu.berkeley.cs169.model.ContactModel;
import edu.berkeley.cs169.util.Utils;

// Shared Application instance, shared across all Activities in this Android app
public class BlindlyMessenger extends Application {
	private TextToSpeech mTextToSpeech;

	private String savedText = null;

	@Override
	public void onCreate() {
		super.onCreate();

		// initialize text-to-speech used by all Activities
		mTextToSpeech = new TextToSpeech(this, new OnInitListener() {

			public void onInit(int status) {
				if (savedText != null) {
					speak(savedText);
				}
				// Tell speak() that we are initialized
				savedText = "";
			}
		});

		// check to see if this is the first time this app is being run
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean firstRun = prefs.getBoolean("firstRun", true);

		// if on first run, speak out a long help message
		if (firstRun) {
			prefs.edit().putBoolean("firstRun", false).commit();
			String firstRunMessage = getString(R.string.first_run_message);
			speak(firstRunMessage, true);
		}
	}

	// easy utility to intelligently vibrate and/or speak a message
	public void output(String text) {
		speak(text);
		vibrate(text);
	}

	// speak a message if user enabled text-to-speech in the settings or force
	// parameter is true
	public void speak(String text, boolean force) {
		// Check if not initialized
		if (savedText == null) {
			savedText = text;
			return;
		}

		// check settings
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (force || prefs.getBoolean("tts", false))
			mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}

	// speak a message if user enabled text-to-speech in the settings
	public void speak(String text) {
		speak(text, false);
	}

	// vibrate a message if user enabled vibrations in the settings
	public void vibrate(String text) {

		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		// check settings
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("vibrate", true)) {
			// get morse code from text
			long[] data = Utils.vibratePattern(text);
			int baseSpeed = Integer.parseInt(prefs.getString("vibrate_speed",
					"100"));

			// for each DOT or DASH, determine how long to vibrate for
			for (int i = 0; i < data.length; i++) {
				data[i] *= baseSpeed;
			}
			vibrator.vibrate(data, -1);
		}
	}

	// utility method to match a contact name with a given number
	public String getNameForNumber(String number) {
		try {
			// Get the Cursor relating to the PhoneLookup service
			Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
					Uri.encode(number));
			Cursor c = getContentResolver()
					.query(uri, new String[] { PhoneLookup.DISPLAY_NAME },
							null, null, null);

			// Move to the correct position
			if (c.moveToFirst()) {
				return c.getString(c.getColumnIndex(PhoneLookup.DISPLAY_NAME));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	// utility method to return a ContactModel representing the app user
	public ContactModel getMyContact() {
		TelephonyManager phoneManager = (TelephonyManager) getApplicationContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		String number = phoneManager.getLine1Number();
		String name = getResources().getString(R.string.name_self);
		return new ContactModel(name, number);
	}

	// utility method to get input base speed from settings
	public int getInputSpeedBase() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		return Integer.parseInt(prefs.getString("input_speed", "100"));
	}

	// utility method to intelligently return whether the app should respond to
	// touches
	public boolean isTouch() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		return prefs.getBoolean("touch", true)
				&& !prefs.getBoolean("blank", false);
	}
}
