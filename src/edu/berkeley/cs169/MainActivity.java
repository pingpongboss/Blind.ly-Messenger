package edu.berkeley.cs169;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;
import edu.berkeley.cs169.utils.Utils;

public class MainActivity extends Activity implements
		NavigationKeyInterpreterResultListener {
	BlindlyMessenger app;
	NavigationKeyInterpreter keyInterpreter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		app = (BlindlyMessenger) getApplication();

		keyInterpreter = new NavigationKeyInterpreter(this);

		LinearLayout layoutUp = (LinearLayout) findViewById(R.id.layout_up);
		layoutUp.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startCompose();
			}
		});

		LinearLayout layoutDown = (LinearLayout) findViewById(R.id.layout_down);
		layoutDown.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startRead();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		String alert = getResources().getString(R.string.main_shortcode);
		Utils.textToVibration(alert, this);

		String greeting = getResources().getString(R.string.main_tts);
		app.speak(greeting);
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
			startCompose();
			break;
		case DOWN:
			startRead();
			break;
		case UP_AND_DOWN:
			// nothing to do
			break;
		case UP_AND_DOWN_LONG:
			startHelp();
			break;
		}
	}

	protected void startHelp() {
		String alert = getResources().getString(R.string.main_help);
		Utils.textToVibration(alert, this);
		app.speak(alert);
	}

	protected void startCompose() {
		startActivity(new Intent(this, RecipientInputActivity.class));
	}

	protected void startRead() {
		startActivity(new Intent(this, ReadMessageActivity.class));
	}
}
