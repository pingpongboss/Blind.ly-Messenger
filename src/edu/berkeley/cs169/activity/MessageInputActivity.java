package edu.berkeley.cs169.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import edu.berkeley.cs169.BlindlyMessenger;
import edu.berkeley.cs169.R;
import edu.berkeley.cs169.model.ContactModel;
import edu.berkeley.cs169.model.MessageModel;
import edu.berkeley.cs169.model.MorseCodeModel;
import edu.berkeley.cs169.util.KeyboardKeyInterpreter;
import edu.berkeley.cs169.util.KeyboardKeyInterpreter.KeyboardKeyInterpreterResultListener;
import edu.berkeley.cs169.util.Utils;

public class MessageInputActivity extends Activity implements
		KeyboardKeyInterpreterResultListener {
	BlindlyMessenger app;

	KeyboardKeyInterpreter keyInterpreter;
	EditText edit;
	ScrollView scroll;
	TextView visualizer;
	ImageView overlay;
	TextView status;

	ContactModel mRecipient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.message_input);

		app = (BlindlyMessenger) getApplication();

		keyInterpreter = new KeyboardKeyInterpreter(this);

		mRecipient = getIntent().getParcelableExtra("recipient");

		edit = (EditText) findViewById(R.id.edit);
		scroll = (ScrollView) findViewById(R.id.scroll);
		scroll.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					dispatchKeyEvent(new KeyEvent(SystemClock.uptimeMillis(),
							SystemClock.uptimeMillis(), KeyEvent.ACTION_DOWN,
							KeyEvent.KEYCODE_VOLUME_UP, 0));
				else if (event.getAction() == MotionEvent.ACTION_UP)
					dispatchKeyEvent(new KeyEvent(SystemClock.uptimeMillis(),
							SystemClock.uptimeMillis(), KeyEvent.ACTION_UP,
							KeyEvent.KEYCODE_VOLUME_UP, 0));
				return true;
			}
		});
		visualizer = (TextView) findViewById(R.id.visualizer);

		overlay = (ImageView) findViewById(R.id.overlay);
		overlay.setAlpha(0);

		TextView name = (TextView) findViewById(R.id.name);
		TextView number = (TextView) findViewById(R.id.number);
		name.setText(mRecipient.getName());
		number.setText(mRecipient.getNumber());

		status = (TextView) findViewById(R.id.status);
	}

	@Override
	protected void onResume() {
		super.onResume();

		String alert = getResources().getString(
				R.string.message_input_shortcode);
		app.vibrate(alert);

		String greeting = getResources().getString(R.string.message_input_tts);
		app.speak(String.format("%s %s", greeting, mRecipient));
	}

	protected void vibrateHelp() {
		String alert = getResources().getString(R.string.message_input_help);

		app.vibrate(alert);
		app.speak(alert);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
			overlay.setAlpha(255);
		if (keyInterpreter.onKeyDown(keyCode, event))
			return true;
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
			overlay.setAlpha(0);
		if (keyInterpreter.onKeyUp(keyCode, event))
			return true;
		return super.onKeyUp(keyCode, event);
	}

	public void onKeyboardKeyInterpreterResult(ResultCode code, Object result) {
		final ResultCode copyCode = code;
		final Object copyResult = result;
		runOnUiThread(new Runnable() {

			public void run() {
				status.setVisibility(View.GONE);
				String sentText = getResources().getString(
						R.string.message_input_sent);
				String existingText = visualizer.getText().toString();
				if (existingText.equals(sentText))
					existingText = "";

				switch (copyCode) {
				case DOT:
					visualizer.setText(existingText + ".");
					break;
				case DASH:
					visualizer.setText(existingText + "-");
					break;
				case LETTER_GAP:
					visualizer.setText(existingText + " ");
					break;
				case WORD_GAP:
					visualizer.setText(existingText.trim() + "\n");
					break;
				case LAST_LETTER:
					String character = copyResult.toString();
					edit.setText(edit.getText().toString() + character);
					edit.setSelection(edit.length());

					app.speak(character);
					break;
				case DONE:
					String message = Utils
							.morseToText((MorseCodeModel) copyResult);
					if (message != null && !message.equals("")) {
						Utils.sendSMSHelper(mRecipient.getNumber(), message);
						visualizer.setText(sentText);
						edit.setText("");
						status.setVisibility(View.VISIBLE);
						status.setText(message);
						MessageModel messageModel = new MessageModel(message,
								app.getMyContact(), mRecipient);
						app.speak(messageModel.toString());
					}
					break;
				}

				scroll.fullScroll(View.FOCUS_DOWN);
			}
		});
	}
}
