package edu.berkeley.cs169;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.ContactsContract.PhoneLookup;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.TelephonyManager;
import edu.berkeley.cs169.datamodels.ContactModel;
import edu.berkeley.cs169.utils.Utils;

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

	public void speak(String text) {
		// Check if not initialized
		if (savedText == null) {
			savedText = text;
			return;
		}

		mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}

	public void vibrate(String text) {
		long[] data = Utils.vibratePattern(text);

		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(data, -1);
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
