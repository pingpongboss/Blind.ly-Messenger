package edu.berkeley.cs169.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.widget.Toast;
import edu.berkeley.cs169.BlindlyMessenger;
import edu.berkeley.cs169.R;
import edu.berkeley.cs169.adapter.ContactCursorAdapter;
import edu.berkeley.cs169.model.ContactModel;
import edu.berkeley.cs169.util.NavigationKeyInterpreter;
import edu.berkeley.cs169.util.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;

public class RecipientInputActivity extends ListActivity implements
		NavigationKeyInterpreterResultListener {
	BlindlyMessenger app;

	private boolean mShowInvisible;
	private NavigationKeyInterpreter keyInterpreter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recipient_input);

		app = (BlindlyMessenger) getApplication();

		// Initialize class properties
		mShowInvisible = false;

		// Register handler for UI elements
		keyInterpreter = new NavigationKeyInterpreter(this, 200, 5);

		Cursor cursor = getContacts();
		setListAdapter(new ContactCursorAdapter(this, cursor));
	}

	@Override
	protected void onResume() {
		super.onResume();

		String alert = getResources().getString(
				R.string.recipient_input_shortcode);
		app.vibrate(alert);

		String greeting = getResources()
				.getString(R.string.recipient_input_tts);
		app.speak(greeting);
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

	public void onKeyInterpreterResult(ResultCode code) {
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
			passPhoneNumber();
			break;
		case UP_AND_DOWN_LONG:
			starthelp();
			break;
		}
	}

	private void starthelp() {
		String alert = getResources().getString(R.string.recipient_input_help);

		app.vibrate(alert);
		app.speak(alert);
	}

	private void passPhoneNumber() {
		Cursor c = (Cursor) getListView().getSelectedItem();
		if (c != null) {
			String name = c
					.getString(c
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String number = c
					.getString(c
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			ContactModel recipient = new ContactModel(name, number);
			Intent i = new Intent(this, MessageInputActivity.class);
			i.putExtra("recipient", recipient);

			startActivity(i);
		} else {
			Toast.makeText(getApplicationContext(), "nothing selected",
					Toast.LENGTH_SHORT).show();
		}
	}

	private Cursor getContacts() {
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER };
		String selection = String.format("%s = '%s'",
				ContactsContract.CommonDataKinds.Phone.IN_VISIBLE_GROUP,
				mShowInvisible ? "0" : "1");

		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		return managedQuery(uri, projection, selection, null, sortOrder);
	}
}