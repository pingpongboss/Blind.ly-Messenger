package edu.berkeley.cs169;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;
import edu.berkeley.cs169.utils.Utils;

public class ComposeActivity extends Activity implements
		NavigationKeyInterpreterResultListener {
	NavigationKeyInterpreter keyInterpreter;
	public static int PHONE_NUM_SELECT = 555555;
	private EditText recipient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compose);

		keyInterpreter = new NavigationKeyInterpreter(this);
		recipient = (EditText) findViewById(R.id.text_recipient);
	}

	@Override
	protected void onResume() {
		super.onResume();

		String alert = getResources().getString(R.string.compose_shortcode);
		Utils.textToVibration(alert, this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHONE_NUM_SELECT) {
			if (resultCode == RecipientInputActivity.PHONE_NUM_OK) {
				recipient.setText(data.getExtras().getString("recipient"));
			}
		}
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

	public void onKeyInterpreterResult(int resultCode) {
		switch (resultCode) {
		case UP: // up
			editRecipient();
			break;
		case DOWN: // down
			editMessage();
			break;
		case UP_AND_DOWN: // up && down
			// TODO send the SMS
			break;
		case UP_AND_DOWN_HOLD: // hold up && down
			startHelp();
			break;
		}
	}

	protected void startHelp() {
		String alert = getResources().getString(R.string.compose_help);
		Utils.textToVibration(alert, this);
	}

	protected void editRecipient() {
		startActivityForResult(new Intent(this, RecipientInputActivity.class),
				PHONE_NUM_SELECT);
	}

	protected void editMessage() {
		startActivity(new Intent(this, MessageInputActivity.class));
	}
}
