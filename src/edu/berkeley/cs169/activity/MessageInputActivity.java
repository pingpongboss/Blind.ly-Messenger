package edu.berkeley.cs169.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import edu.berkeley.cs169.BlindlyMessenger;
import edu.berkeley.cs169.R;
import edu.berkeley.cs169.model.ContactModel;
import edu.berkeley.cs169.model.MessageModel;
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
	Button send;

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
				if (!app.isTouch())
					return false;

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

		send = (Button) findViewById(R.id.send);
		send.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				sendMessage();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		String alert = getResources().getString(
				R.string.message_input_shortcode);
		app.vibrate(alert);

		String greeting = getResources().getString(R.string.message_input_tts);
		app.speak(String.format("%s %s", greeting, mRecipient));

		Utils.blankScreen(this);

		boolean touch = app.isTouch();
		edit.setFocusable(touch);
		send.setVisibility(touch ? View.VISIBLE : View.GONE);
	}

	protected void vibrateHelp() {
		String alert = getResources().getString(R.string.message_input_help);

		app.output(alert);
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
					sendMessage();
					break;
				}

				scroll.fullScroll(View.FOCUS_DOWN);
			}
		});
	}

	protected void sendMessage() {
		String message = edit.getText().toString().toLowerCase();
		if (message != null && !message.equals("")) {
			Utils.sendSMSHelper(mRecipient.getNumber(), message);

			String sentText = getResources().getString(
					R.string.message_input_sent);
			visualizer.setText(sentText);
			edit.setText("");
			status.setVisibility(View.VISIBLE);
			status.setText(message);
			MessageModel messageModel = new MessageModel(message,
					app.getMyContact(), mRecipient);

			Intent intent = new Intent(this, MainActivity.class);
			// finish RecipientInput and MessageInput activities
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("message", messageModel);
			startActivity(intent);
		}
	}
}
