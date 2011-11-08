package edu.berkeley.cs169;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import edu.berkeley.cs169.utils.Utils;

public class MessageInputActivity extends Activity{

	// Edit for each Activity
	public static final String TAG = "MessageInputActivity";
	
	// Same for all Activities
			public boolean upPressed = false;
			public boolean downPressed = false;
	// Same for all Activities
	@Override
	protected void onResume() {
		super.onResume();

		vibrateShortCode();
	}
	// Edit for each Activity
	protected void vibrateShortCode() {
		String alert = getResources().getString(R.string.compose_shortcode);

		Utils.textToVibration(alert, this);
		}

	// Edit for each Activity
	protected void vibrateHelp() {
		String alert = getResources().getString(R.string.compose_help);

		Utils.textToVibration(alert, this);
		}

	// Edit for each Activity
	protected void startUpAction() {
		Log.d(TAG, "Clicked UP");
		Utils.textToVibration("e", this);
	}

	// Edit for each Activity
	protected void startDownAction() {
		Log.d(TAG, "Clicked DOWN");
		Utils.textToVibration("t", this);
	}
	
	// Same for all Activities
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			upPressed = true;
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			downPressed = true;
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// Same for all Activities
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (upPressed && downPressed) {
				vibrateHelp();
			} else if (upPressed) {
				startUpAction();
			} else if (downPressed) {
				startDownAction();
			}
			upPressed = false;
			downPressed = false;
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	// }}
	
//	private MessageModel mMessage;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.message_input);
	}
}	


