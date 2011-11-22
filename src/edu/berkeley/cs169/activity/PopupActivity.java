package edu.berkeley.cs169.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import edu.berkeley.cs169.BlindlyMessenger;
import edu.berkeley.cs169.R;
import edu.berkeley.cs169.model.MessageModel;
import edu.berkeley.cs169.util.NavigationKeyInterpreter;
import edu.berkeley.cs169.util.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;

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
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		setIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();

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

		String greeting = getResources().getString(R.string.popup_tts);
		app.output(String.format("%s %s", greeting, mMessage.getFrom()));
	}

	protected void startListen() {
		String alert = mMessage.toString();
		app.output(alert);
	}

	protected void startReply() {
		Intent i = new Intent(this, MessageInputActivity.class);
		i.putExtra("recipient", mMessage.getFrom());
		startActivity(i);
		finish();
	}

	protected void startHelp() {
		String alert = getResources().getString(R.string.popup_help);
		app.output(alert);
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

	public void onNavKeyInterpreterResult(ResultCode code) {
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
