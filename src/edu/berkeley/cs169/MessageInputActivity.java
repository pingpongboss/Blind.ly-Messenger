package edu.berkeley.cs169;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import edu.berkeley.cs169.utils.KeyboardKeyInterpreter;
import edu.berkeley.cs169.utils.KeyboardKeyInterpreter.KeyboardKeyInterpreterResultListener;
import edu.berkeley.cs169.utils.Utils;

public class MessageInputActivity extends Activity implements
		KeyboardKeyInterpreterResultListener {
	KeyboardKeyInterpreter keyInterpreter;
	EditText edit;

	String recipient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.message_input);

		keyInterpreter = new KeyboardKeyInterpreter(this);

		recipient = getIntent().getStringExtra("recipient");

		edit = (EditText) findViewById(R.id.edit);

		TextView recipientTextView = (TextView) findViewById(R.id.recipient);
		recipientTextView.setText("To: " + recipient);
	}

	@Override
	protected void onResume() {
		super.onResume();

		String alert = getResources().getString(R.string.compose_shortcode);
		Utils.textToVibration(alert, this);
	}

	protected void vibrateHelp() {
		String alert = getResources().getString(R.string.compose_help);

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
		case DONE:
			String message = edit.getText().toString();
			Utils.sendSMSHelper(recipient, message);
			Toast.makeText(
					this,
					String.format("SMS sent to %s: \"%s\"", recipient, message),
					Toast.LENGTH_SHORT).show();
			break;
		}
	}

}
