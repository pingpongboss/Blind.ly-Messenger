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
import edu.berkeley.cs169.util.AndroidUtils;
import edu.berkeley.cs169.util.KeyInterpreter;
import edu.berkeley.cs169.util.KeyInterpreter.KeyInterpreterResultListener;

//screen to compose the message content
public class MessageInputActivity extends Activity implements
		KeyInterpreterResultListener {
	BlindlyMessenger app;

	KeyInterpreter keyInterpreter;

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

		keyInterpreter = new KeyInterpreter(this);

		// get contact data from RecipientInputActivity
		mRecipient = getIntent().getParcelableExtra("recipient");

		edit = (EditText) findViewById(R.id.edit);

		// allow user to tap morse code on the lower half of the screen
		scroll = (ScrollView) findViewById(R.id.scroll);
		scroll.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (!app.isTouch())
					return false;

				// fake events
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

		// the overlay lights up when a key is being held down
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

		AndroidUtils.blankScreen(this);

		boolean touch = app.isTouch();
		edit.setFocusable(touch);
		send.setVisibility(touch ? View.VISIBLE : View.GONE);
	}

	@Override
	protected void onPause() {
		super.onPause();

		app.stopOutput();
	}

	protected void startHelp() {
		String alert = getResources().getString(R.string.message_input_help);

		app.speak(alert, true);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		overlay.setAlpha(255);
		if (keyInterpreter.onKeyDown(keyCode, event))
			return true;
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		overlay.setAlpha(0);
		if (keyInterpreter.onKeyUp(keyCode, event))
			return true;
		return super.onKeyUp(keyCode, event);
	}

	public void onKeyInterpreterResult(ResultCode code, Object data) {
		final ResultCode copyCode = code;
		final Object copyData = data;
		runOnUiThread(new Runnable() {

			public void run() {
				// update the visualizer or the edit text box to show the last
				// morse code actions or characters
				status.setVisibility(View.GONE);
				String sentText = getResources().getString(
						R.string.message_input_sent);
				String existingText = visualizer.getText().toString();
				if (existingText.equals(sentText))
					existingText = "";

				switch (copyCode) {
				case KEYBOARD_DOT:
					visualizer.setText(existingText + ".");
					app.speak("dot");
					break;
				case KEYBOARD_DASH:
					visualizer.setText(existingText + "-");
					app.speak("dash");
					break;
				case KEYBOARD_LAST_LETTER:
					char character = (Character) copyData;

					if (character == ' ') {
						visualizer.setText(existingText + "\n");
					} else {
						visualizer.setText(existingText + " ");
					}

					edit.setText(edit.getText().toString() + character);
					edit.setSelection(edit.length());

					app.speak(String.valueOf(character));
					break;
				case UP_AND_DOWN:
					sendMessage();
					break;
				case UP_AND_DOWN_LONG:
					startHelp();
					break;
				}

				scroll.fullScroll(View.FOCUS_DOWN);
			}
		});
	}

	// sends a text message to the recipient
	protected void sendMessage() {
		// get content from what the user has tapped in mrose code so far
		String message = edit.getText().toString().toLowerCase();
		if (message != null && !message.equals("")) {
			// actually send the SMS
			AndroidUtils.sendSMSHelper(mRecipient.getNumber(), message,
					getApplicationContext());

			String sentText = getResources().getString(
					R.string.message_input_sent);
			visualizer.setText(sentText);
			edit.setText("");
			status.setVisibility(View.VISIBLE);
			status.setText(message);
			MessageModel messageModel = new MessageModel(message,
					app.getMyContact(), mRecipient);

			// once sent, go back to MainActivity
			// give MainActivity the message we just sent so MainActivity can
			// alert the user
			Intent intent = new Intent(this, MainActivity.class);
			// flag finishes RecipientInput and MessageInput activities, which
			// are on top of the back stack
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("message", messageModel);
			startActivity(intent);
		}
	}
}
