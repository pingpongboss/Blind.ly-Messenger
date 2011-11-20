package edu.berkeley.cs169.activity;

import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.SimpleCursorAdapter;
import edu.berkeley.cs169.BlindlyMessenger;
import edu.berkeley.cs169.R;
import edu.berkeley.cs169.model.ContactModel;
import edu.berkeley.cs169.model.MessageModel;
import edu.berkeley.cs169.util.NavigationKeyInterpreter;
import edu.berkeley.cs169.util.Utils;
import edu.berkeley.cs169.util.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;

public class ReadMessageActivity extends ListActivity implements
		NavigationKeyInterpreterResultListener {

	BlindlyMessenger app;
	private NavigationKeyInterpreter keyInterpreter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_list);

		app = (BlindlyMessenger) getApplication();

		// Register handler for UI elements
		keyInterpreter = new NavigationKeyInterpreter(this, 200, 5);

		// populate the message list
		populateMessageList();

	}

	@Override
	protected void onResume() {
		super.onResume();

		// TODO vibrate shortcode and speak greeting

		Utils.blankScreen(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyInterpreter.onKeyDown(keyCode, event)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyInterpreter.onKeyUp(keyCode, event)) {
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	public void onNavKeyInterpreterResult(ResultCode code) {
		switch (code) {
		case UP:
		case UP_REPEAT:
			dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
					KeyEvent.KEYCODE_DPAD_UP));
			dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
					KeyEvent.KEYCODE_DPAD_UP));
			break;
		case UP_REPEAT_LONG:
			for (int i = 0; i < keyInterpreter.getKeyRepeatLongThreshold(); i++) {
				dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_DPAD_UP));
				dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
						KeyEvent.KEYCODE_DPAD_UP));
			}
			break;
		case DOWN:
		case DOWN_REPEAT:
			dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
					KeyEvent.KEYCODE_DPAD_DOWN));
			dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
					KeyEvent.KEYCODE_DPAD_DOWN));
			break;
		case DOWN_REPEAT_LONG:
			for (int i = 0; i < keyInterpreter.getKeyRepeatLongThreshold(); i++) {
				dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_DPAD_DOWN));
				dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
						KeyEvent.KEYCODE_DPAD_DOWN));
			}
			break;
		case UP_AND_DOWN:
			readMessage();
			break;
		case UP_AND_DOWN_LONG:
			starthelp();
			break;
		}
	}

	private void starthelp() {
		String alert = getResources().getString(R.string.message_input_help);

		app.output(alert);
	}

	private void populateMessageList() {
		Cursor c = getContentResolver().query(
				Uri.parse("content://sms/inbox"),
				new String[] { "_id", "thread_id", "address", "person", "date",
						"body" }, null, null, null);
		startManagingCursor(c);
		String[] columns = new String[] { "address", "body" };
		int[] to = new int[] { R.id.message_entry_name, R.id.message_entry_body };

		// TODO make our own subclass so we can use app.getNameFromNumber()
		SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this,
				R.layout.message_list_item, c, columns, to);
		setListAdapter(mAdapter);
	}

	private void readMessage() {
		Cursor c = (Cursor) getListView().getSelectedItem();
		if (c != null) {
			String number = c.getString(2);
			String name = app.getNameForNumber(number);
			String body = c.getString(5);

			ContactModel from = new ContactModel(name, number);
			ContactModel me = app.getMyContact();
			MessageModel messageModel = new MessageModel(body, from, me);

			app.output(messageModel.toString());
		}
	}
}
