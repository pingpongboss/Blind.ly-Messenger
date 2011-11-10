package edu.berkeley.cs169.deprecated;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import edu.berkeley.cs169.MessageInputActivity;
import edu.berkeley.cs169.R;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;
import edu.berkeley.cs169.utils.Utils;

@Deprecated
public class ComposeActivity extends Activity implements
		NavigationKeyInterpreterResultListener {
	NavigationKeyInterpreter keyInterpreter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compose);

		keyInterpreter = new NavigationKeyInterpreter(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		String alert = getResources().getString(R.string.compose_shortcode);
		Utils.textToVibration(alert, this);
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

	public void onKeyInterpreterResult(ResultCode result) {
		switch (result) {
		case UP: // up
			editRecipient();
			break;
		case DOWN: // down
			editMessage();
			break;
		case UP_AND_DOWN: // up && down
			// TODO send the SMS
			break;
		case UP_AND_DOWN_LONG: // hold up && down
			startHelp();
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
