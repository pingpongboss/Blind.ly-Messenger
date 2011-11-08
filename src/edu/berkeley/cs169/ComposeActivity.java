package edu.berkeley.cs169;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;
import edu.berkeley.cs169.utils.Utils;

public class ComposeActivity extends Activity implements
		NavigationKeyInterpreterResultListener {
	NavigationKeyInterpreter keyIntepreter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compose);

		keyIntepreter = new NavigationKeyInterpreter(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		String alert = getResources().getString(R.string.compose_shortcode);
		Utils.textToVibration(alert, this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyIntepreter.onKeyDown(keyCode, event))
			return true;
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyIntepreter.onKeyUp(keyCode, event))
			return true;
		return super.onKeyUp(keyCode, event);
	}

	public void onKeyInterpreterResult(int resultCode) {
		switch (resultCode) {
		case UP_AND_DOWN: // up && down
			startHelp();
			break;
		case UP: // up
			editRecipient();
			break;
		case DOWN: // down
			editMessage();
			break;
		}
	}

	protected void startHelp() {
		String alert = getResources().getString(R.string.compose_help);
		Utils.textToVibration(alert, this);
	}

	protected void editRecipient() {
		Utils.textToVibration("t", this);
	}

	protected void editMessage() {
		startActivity(new Intent(this, MessageInputActivity.class));
	}
}
