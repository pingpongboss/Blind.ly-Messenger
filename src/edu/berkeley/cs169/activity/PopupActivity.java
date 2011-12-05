package edu.berkeley.cs169.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import edu.berkeley.cs169.BlindlyMessenger;
import edu.berkeley.cs169.R;
import edu.berkeley.cs169.model.MessageModel;
import edu.berkeley.cs169.util.KeyInterpreter;
import edu.berkeley.cs169.util.KeyInterpreter.KeyInterpreterResultListener;

//view that is shown when an SMS_RECEIVED broadcast is received
public class PopupActivity extends Activity implements
		KeyInterpreterResultListener {
	BlindlyMessenger app;
	KeyInterpreter keyInterpreter;

	MessageModel mMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.popup);

		app = (BlindlyMessenger) getApplication();

		keyInterpreter = new KeyInterpreter(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		// since we disallow multiple popups, an existing popup will be given a
		// new Intent
		setIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// get the MessageModel from MessageBroadcastReceiver
		MessageModel message = getIntent().getParcelableExtra("message");

		if (message == null)
			return;

		mMessage = message;

		View listen = findViewById(R.id.popup_bottom_left);
		View reply = findViewById(R.id.popup_bottom_right);

		listen.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startListen();
			}
		});

		reply.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startReply();
			}
		});

		TextView sender = (TextView) findViewById(R.id.sender);
		TextView body = (TextView) findViewById(R.id.message);

		sender.setText(mMessage.getFrom().toString());
		body.setText(mMessage.getContent());

		// no shortcode for popup
		String greeting = getResources().getString(R.string.popup_tts);
		app.output(String.format("%s %s", greeting, mMessage.getFrom()));
	}

	@Override
	protected void onPause() {
		super.onPause();

		app.stopOutput();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// creates the context menu to get to settings
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// detects when user clicks on the settings context menu
		switch (item.getItemId()) {
		case R.id.preference:
			Intent i = new Intent(this, MainPreferenceActivity.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// listen to the message
	protected void startListen() {
		String alert = mMessage.toString();
		app.output(alert);
	}

	// start new Activity to compose a reply message
	protected void startReply() {
		Intent i = new Intent(this, MessageInputActivity.class);
		i.putExtra("recipient", mMessage.getFrom());
		startActivity(i);
		finish();
	}

	protected void startHelp() {
		String alert = getResources().getString(R.string.popup_help);
		app.speak(alert, true);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyInterpreter.onKeyDown(keyCode, event))
			return true;
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyInterpreter.onKeyUp(keyCode, event))
			return true;
		return super.onKeyUp(keyCode, event);
	}

	public void onKeyInterpreterResult(ResultCode code, Object data) {
		switch (code) {
		case NAVIGATION_UP:
			startListen();
			break;
		case NAVIGATION_DOWN:
			startReply();
			break;
		case UP_AND_DOWN:
			// nothing to do
			break;
		case UP_AND_DOWN_LONG:
			startHelp();
			break;
		}
	}
}
