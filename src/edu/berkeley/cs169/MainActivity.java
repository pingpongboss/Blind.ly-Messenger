package edu.berkeley.cs169;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import edu.berkeley.cs169.utils.BMUtils;

public class MainActivity extends Activity {

	// {{ Copy and edit these fields and methods for each Activity

	// Edit for each Activity
	public static final String TAG = "MainActivity";
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
		String alert = getResources().getString(R.string.main_shortcode);

		BMUtils.textToVibration(alert, this);
	}

	// Edit for each Activity
	protected void vibrateHelp() {
		String alert = getResources().getString(R.string.main_help);

		BMUtils.textToVibration(alert, this);
	}

	// Edit for each Activity
	protected void startUpAction() {
		Log.d(TAG, "Clicked UP");

		startActivity(new Intent(this, ComposeActivity.class));
	}

	// Edit for each Activity
	protected void startDownAction() {
		Log.d(TAG, "Clicked DOWN");
		BMUtils.textToVibration("t", this);
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		LinearLayout layoutUp = (LinearLayout) findViewById(R.id.layout_up);
		layoutUp.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startUpAction();
			}
		});

		LinearLayout layoutDown = (LinearLayout) findViewById(R.id.layout_down);
		layoutDown.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startDownAction();
			}
		});
	}
}