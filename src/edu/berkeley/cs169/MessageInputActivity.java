package edu.berkeley.cs169;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import edu.berkeley.cs169.datamodels.ContactModel;
import edu.berkeley.cs169.utils.KeyboardKeyInterpreter;
import edu.berkeley.cs169.utils.KeyboardKeyInterpreter.KeyboardKeyInterpreterResultListener;
import edu.berkeley.cs169.utils.Utils;

public class MessageInputActivity extends Activity implements
		KeyboardKeyInterpreterResultListener {
	KeyboardKeyInterpreter keyInterpreter;
	EditText edit;
	ScrollView scroll;
	TextView visualizer;

	ContactModel recipient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.message_input);

		keyInterpreter = new KeyboardKeyInterpreter(this);

		recipient = getIntent().getParcelableExtra("recipient");

		edit = (EditText) findViewById(R.id.edit);
		scroll = (ScrollView) findViewById(R.id.scroll);
		visualizer = (TextView) findViewById(R.id.visualizer);

		TextView recipientTextView = (TextView) findViewById(R.id.recipient);
		recipientTextView.setText(String.format("%s\n%s", recipient.getName(),
				recipient.getNumber()));
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

	public void onKeyInterpreterResult(ResultCode code, Object result) {
		final ResultCode copyCode = code;
		final Object copyResult = result;
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				switch (copyCode) {
				case DOT:
					visualizer.setText(visualizer.getText().toString() + ".");
					break;
				case DASH:
					visualizer.setText(visualizer.getText().toString() + "-");
					break;
				case LETTER_GAP:
					visualizer.setText(visualizer.getText().toString() + " ");
					break;
				case WORD_GAP:
					visualizer.setText(visualizer.getText().toString() + "\n");
					break;
				case LAST_LETTER:
					edit.setText(edit.getText().toString() + copyResult);
					edit.setSelection(edit.length());
					break;
				case DONE:
					String message = edit.getText().toString();
					Utils.sendSMSHelper(recipient.getNumber(), message);
					Toast.makeText(
							MessageInputActivity.this,
							String.format("SMS sent to %s: \"%s\"", recipient,
									message), Toast.LENGTH_SHORT).show();
					break;
				}

				scroll.fullScroll(View.FOCUS_DOWN);
			}
		});
	}
}
