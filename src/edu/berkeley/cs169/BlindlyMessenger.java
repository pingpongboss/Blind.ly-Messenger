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

public class BlindlyMessenger extends Application {
	private TextToSpeech mTextToSpeech;

	private String savedText = null;

	@Override
	public void onCreate() {
		super.onCreate();

		mTextToSpeech = new TextToSpeech(this, new OnInitListener() {

			public void onInit(int status) {
				if (savedText != null) {
					speak(savedText);
				}
				// Tell speak() that we are initialized
				savedText = "";
			}
		});
	}

	public void output(String text) {
		speak(text);
		vibrate(text);
	}

	public void speak(String text) {
		// Check if not initialized
		if (savedText == null) {
			savedText = text;
			return;
		}

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("tts", false))
			mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}

	public void vibrate(String text) {

		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("vibrate", true)) {
			long[] data = Utils.vibratePattern(text);
			int baseSpeed = Integer.parseInt(prefs.getString("vibrate_speed",
					"100"));

			for (int i = 0; i < data.length; i++) {
				data[i] *= baseSpeed;
			}
			vibrator.vibrate(data, -1);
		}
	}

	public String getNameForNumber(String number) {
		try {
			Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
					Uri.encode(number));
			Cursor c = getContentResolver()
					.query(uri, new String[] { PhoneLookup.DISPLAY_NAME },
							null, null, null);

			if (c.moveToFirst()) {
				return c.getString(c.getColumnIndex(PhoneLookup.DISPLAY_NAME));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public ContactModel getMyContact() {
		TelephonyManager phoneManager = (TelephonyManager) getApplicationContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		String number = phoneManager.getLine1Number();
		String name = getResources().getString(R.string.name_self);
		return new ContactModel(name, number);
	}
}
