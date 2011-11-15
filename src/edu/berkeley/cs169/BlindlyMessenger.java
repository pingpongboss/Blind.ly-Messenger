package edu.berkeley.cs169;

import android.app.Application;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class BlindlyMessenger extends Application {
	private TextToSpeech mTextToSpeech;

	private String savedText = null;

	@Override
	public void onCreate() {
		super.onCreate();

		mTextToSpeech = new TextToSpeech(this, new OnInitListener() {

			@Override
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

		mTextToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
	}
}
