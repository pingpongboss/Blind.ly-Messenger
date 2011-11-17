package edu.berkeley.cs169;

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
import edu.berkeley.cs169.datamodels.ContactModel;
import edu.berkeley.cs169.datamodels.MorseCodeModel;
import edu.berkeley.cs169.utils.KeyboardKeyInterpreter;
import edu.berkeley.cs169.utils.KeyboardKeyInterpreter.KeyboardKeyInterpreterResultListener;
import edu.berkeley.cs169.utils.Utils;

public class MessageInputActivity extends Activity implements
		KeyboardKeyInterpreterResultListener {
	BlindlyMessenger app;

	KeyboardKeyInterpreter keyInterpreter;
	EditText edit;
	ScrollView scroll;
	TextView visualizer;
	ImageView overlay;
	TextView status;

	ContactModel recipient;
	
	String greeting;
	String help;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.message_input);

		app = (BlindlyMessenger) getApplication();

		keyInterpreter = new KeyboardKeyInterpreter(this);

		recipient = getIntent().getParcelableExtra("recipient");

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

		TextView recipientTextView = (TextView) findViewById(R.id.recipient);
		recipientTextView.setText(String.format("%s\n%s", recipient.getName(),
				recipient.getNumber()));

		status = (TextView) findViewById(R.id.status);
		
		greeting = getResources().getString(R.string.TTS_compose);
		help = getResources().getString(R.string.TTS_compose_help);
		app.speak(recipient.getName() + greeting + help);
	}

	@Override
	protected void onResume() {
		super.onResume();

		String alert = getResources().getString(R.string.compose_shortcode);
		Utils.textToVibration(alert, this);

		greeting = getResources().getString(R.string.TTS_compose);
		app.speak(recipient.getName() + greeting);
	}

	protected void vibrateHelp() {
		String alert = getResources().getString(R.string.compose_help);

		Utils.textToVibration(alert, this);
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

	public void onKeyInterpreterResult(ResultCode code, Object result) {
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
					edit.setText(edit.getText().toString() + copyResult);
					edit.setSelection(edit.length());
					break;
				case DONE:
					String message = Utils
							.morseToText((MorseCodeModel) copyResult);
					if (message != null && !message.equals("")) {
						Utils.sendSMSHelper(recipient.getNumber(), message);
						visualizer.setText(sentText);
						edit.setText("");
						status.setVisibility(View.VISIBLE);
						status.setText(message);
						app.speak("S M S message sent to "
								+ recipient.getName() + ". your message says:"
								+ message);
					}
					break;
				}

				scroll.fullScroll(View.FOCUS_DOWN);
			}
		});
	}
}
