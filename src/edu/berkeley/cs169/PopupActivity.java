package edu.berkeley.cs169;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import edu.berkeley.cs169.datamodels.MessageModel;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;

public class PopupActivity extends Activity implements
		NavigationKeyInterpreterResultListener {
	BlindlyMessenger app;
	NavigationKeyInterpreter keyInterpreter;

	MessageModel mMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup);

		app = (BlindlyMessenger) getApplication();

		keyInterpreter = new NavigationKeyInterpreter(this);

		mMessage = getIntent().getParcelableExtra("message");

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
		TextView message = (TextView) findViewById(R.id.message);

		sender.setText(mMessage.getFrom().toString());
		message.setText(mMessage.getContent());
	}

	@Override
	protected void onResume() {
		super.onResume();

		String alert = getResources().getString(R.string.popup_shortcode);
		app.vibrate(alert);

		String greeting = getResources().getString(R.string.popup_tts);
		app.speak(String.format("%s %s", greeting, mMessage.getFrom()));
	}

	protected void startListen() {
		String alert = mMessage.toString();
		app.vibrate(alert);
		app.speak(alert);
	}

	protected void startReply() {
		Intent i = new Intent(this, MessageInputActivity.class);
		i.putExtra("recipient", mMessage.getFrom());
		startActivity(i);
	}

	protected void startHelp() {
		String alert = getResources().getString(R.string.popup_help);
		app.vibrate(alert);
		app.speak(alert);
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

	public void onKeyInterpreterResult(ResultCode code) {
		switch (code) {
		case UP:
			startListen();
			break;
		case DOWN:
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
