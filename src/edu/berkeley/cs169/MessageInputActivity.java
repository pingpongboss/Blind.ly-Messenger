package edu.berkeley.cs169;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import edu.berkeley.cs169.utils.KeyboardKeyInterpreter;
import edu.berkeley.cs169.utils.KeyboardKeyInterpreter.KeyboardKeyInterpreterResultListener;
import edu.berkeley.cs169.utils.Utils;

public class MessageInputActivity extends Activity implements
		KeyboardKeyInterpreterResultListener {
	KeyboardKeyInterpreter keyInterpreter;
	EditText edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.message_input);

		keyInterpreter = new KeyboardKeyInterpreter(this);

		edit = (EditText) findViewById(R.id.edit);
	}

	@Override
	protected void onResume() {
		super.onResume();

		String alert = getResources().getString(R.string.compose_shortcode);
		Utils.textToVibration(alert, this);
	}

	// Edit for each Activity
	protected void vibrateHelp() {
		String alert = getResources().getString(R.string.compose_help);

		Utils.textToVibration(alert, this);
	}

	// Same for all Activities
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyInterpreter.onKeyDown(keyCode, event))
			return true;
		return super.onKeyDown(keyCode, event);
	}

	// Same for all Activities
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyInterpreter.onKeyUp(keyCode, event))
			return true;
		return super.onKeyUp(keyCode, event);
	}

	public void onKeyInterpreterResult(int resultCode, Object result) {
		switch (resultCode) {
		case DOT:
			break;
		case DASH:
			break;
		case LETTER_GAP:
			break;
		case WORD_GAP:
			break;
		case LAST_LETTER:
			Log.d("MessageInputActivity", result.toString());
			edit.setText(edit.getText().toString() + result);
			break;
		}
	}

	// }}

	// private MessageModel mMessage;

}
