package edu.berkeley.cs169.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.Toast;
import edu.berkeley.cs169.BlindlyMessenger;
import edu.berkeley.cs169.R;
import edu.berkeley.cs169.adapter.RecipientInputAdapter;
import edu.berkeley.cs169.model.ContactModel;
import edu.berkeley.cs169.util.KeyboardKeyInterpreter;
import edu.berkeley.cs169.util.KeyboardKeyInterpreter.KeyboardKeyInterpreterResultListener;
import edu.berkeley.cs169.util.NavigationKeyInterpreter;
import edu.berkeley.cs169.util.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;
import edu.berkeley.cs169.util.Utils;

public class RecipientInputActivity extends ListActivity implements
		NavigationKeyInterpreterResultListener,
		KeyboardKeyInterpreterResultListener {
	BlindlyMessenger app;

	private EditText filterText;
	private ListView contactsList;
	RecipientInputAdapter adapter;
	ContactCursor cursor;

	private boolean mShowInvisible, firstVolDown;
	private NavigationKeyInterpreter navKeyInterpreter;
	private KeyboardKeyInterpreter keyKeyInterpreter;

	String defaultSelection = String.format("%s = '%s'",
			ContactsContract.CommonDataKinds.Phone.IN_VISIBLE_GROUP,
			mShowInvisible ? "0" : "1");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recipient_input);
		filterText = (EditText) findViewById(R.id.search_box);
		filterText.addTextChangedListener(filterTextWatcher);
		filterText.requestFocus();
		contactsList = (ListView) findViewById(android.R.id.list);
		contactsList.setClickable(false);

		app = (BlindlyMessenger) getApplication();

		// Initialize class properties
		mShowInvisible = false;
		firstVolDown = false;

		// Register handler for UI elements
		navKeyInterpreter = new NavigationKeyInterpreter(this, 200, 5);
		keyKeyInterpreter = new KeyboardKeyInterpreter(this);

		cursor = getContacts(defaultSelection);
		adapter = new RecipientInputAdapter(this, cursor);
		adapter.setFilterQueryProvider(new FilterQueryProvider() {
			public Cursor runQuery(CharSequence constraint) {
				if (constraint != null) {
					constraint = constraint.toString().trim();
					String selection = String
							.format("%s = '%s' AND %s LIKE '%s%%'",
									ContactsContract.CommonDataKinds.Phone.IN_VISIBLE_GROUP,
									mShowInvisible ? "0" : "1",
									ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
									constraint.toString());
					// maintain reference to new updated cursor
					cursor = getContacts(selection);
					// update reference of cursor in our ContactCursorAdapter
					adapter.setCursor(cursor);
				}
				return cursor;
			}
		});

		contactsList.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long i) {
				if (contactsList.getSelectedItemPosition() != 0) {
					ContactModel recipient = getContactModelAtCursorPosition((Cursor) contactsList
							.getItemAtPosition(getSelectedItemPosition() - 1));
					app.output(recipient.toString());
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		if (!app.isTouch()) {
			filterText.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});
			contactsList.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});
		}

		setListAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		firstVolDown = true;
		String alert = getResources().getString(
				R.string.recipient_input_shortcode);
		app.vibrate(alert);

		String greeting = getResources()
				.getString(R.string.recipient_input_tts);
		app.speak(greeting);

		Utils.blankScreen(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cursor.close();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (contactsList.isFocused()) {
			if (navKeyInterpreter.onKeyDown(keyCode, event)) {
				return true;
			}
		} else if (filterText.isFocused()) {
			if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
				contactsList.requestFocus();
				if (navKeyInterpreter.onKeyDown(keyCode, event)) {
					return true;
				}
			} else if (keyCode != KeyEvent.KEYCODE_VOLUME_DOWN) {
				if (keyKeyInterpreter.onKeyDown(keyCode, event)
						&& navKeyInterpreter.onKeyDown(keyCode, event)) {
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (contactsList.isFocused()) {
			if (navKeyInterpreter.onKeyUp(keyCode, event)) {
				return true;
			}
		} else if (filterText.isFocused()) {
			if (keyKeyInterpreter.onKeyUp(keyCode, event)
					&& navKeyInterpreter.onKeyUp(keyCode, event)) {
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (app.isTouch()) {
			if (position != 0) {
				passPhoneNumberAtCursorPosition(position);
			}
		}
	}

	private void startHelp() {
		String alert = getResources().getString(R.string.recipient_input_help);

		app.output(alert);
	}

	private ContactModel getContactModelAtCursorPosition(Cursor c) {
		if (c != null) {
			String name = c
					.getString(c
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String number = c
					.getString(c
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			ContactModel recipient = new ContactModel(name, number);
			return recipient;
		}
		return null;
	}

	private void passPhoneNumberAtCursorPosition(int position) {
		Cursor c = ((RecipientInputAdapter) getListAdapter()).getCursor();
		c.moveToPosition(position - 1);
		ContactModel recipient = getContactModelAtCursorPosition(c);
		if (recipient != null) {
			Intent i = new Intent(this, MessageInputActivity.class);
			i.putExtra("recipient", recipient);

			startActivity(i);
		} else {
			Toast.makeText(getApplicationContext(), "nothing selected",
					Toast.LENGTH_SHORT).show();
		}
	}

	private ContactCursor getContacts(String selection) {
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER };

		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		return new ContactCursor(getContentResolver().query(uri, projection,
				selection, null, sortOrder));
	}

	private TextWatcher filterTextWatcher = new TextWatcher() {

		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (adapter != null) {
				adapter.getFilter().filter(s);
			}
		}

	};

	public void onKeyboardKeyInterpreterResult(
			edu.berkeley.cs169.util.KeyboardKeyInterpreter.KeyboardKeyInterpreterResultListener.ResultCode code,
			Object result) {
		final edu.berkeley.cs169.util.KeyboardKeyInterpreter.KeyboardKeyInterpreterResultListener.ResultCode copyCode = code;
		final Object copyResult = result;
		runOnUiThread(new Runnable() {

			public void run() {
				switch (copyCode) {
				case DOT:
					break;
				case DASH:
					break;
				case LETTER_GAP:
					break;
				case WORD_GAP:
					break;
				case LAST_LETTER:
					String character = copyResult.toString();
					// do not allow spaces when typing a recipient or number
					if (!character.equals(" ")) {
						filterText.setText(filterText.getText().toString()
								+ character);
						filterText.setSelection(filterText.length());
					}
					break;
				case DONE:
					break;
				}
			}
		});
	}

	public void onNavKeyInterpreterResult(
			edu.berkeley.cs169.util.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener.ResultCode code) {
		switch (code) {
		case UP:
		case UP_REPEAT:
			if (!filterText.isFocused()) {
				dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_DPAD_UP));
				dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
						KeyEvent.KEYCODE_DPAD_UP));
				if (contactsList.getSelectedItemPosition() == 0) {
					// move focus back up to filterText
					filterText.setText(""); // clear text so user can re-filter
					filterText.requestFocus();
				}
			}
			break;
		case UP_REPEAT_LONG:
			if (!filterText.isFocused()) {
				for (int i = 0; i < navKeyInterpreter
						.getKeyRepeatLongThreshold(); i++) {
					dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
							KeyEvent.KEYCODE_DPAD_UP));
					dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
							KeyEvent.KEYCODE_DPAD_UP));
				}
			}
			break;
		case DOWN:
		case DOWN_REPEAT:
			if (!filterText.isFocused()) {
				dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_DPAD_DOWN));
				dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
						KeyEvent.KEYCODE_DPAD_DOWN));
				if (firstVolDown) {
					firstVolDown = false;
					dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
							KeyEvent.KEYCODE_DPAD_DOWN));
					dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
							KeyEvent.KEYCODE_DPAD_DOWN));
				}
			}
			break;
		case DOWN_REPEAT_LONG:
			if (!filterText.isFocused()) {
				for (int i = 0; i < navKeyInterpreter
						.getKeyRepeatLongThreshold(); i++) {
					dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
							KeyEvent.KEYCODE_DPAD_DOWN));
					dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
							KeyEvent.KEYCODE_DPAD_DOWN));
				}
			}
			break;
		case UP_AND_DOWN:
			Log.d("RIA", "BLAM!!");
			String phoneNum = filterText.getText().toString();
			if (isPhoneNumber(phoneNum)) {
				String name = app.getNameForNumber(phoneNum);
				ContactModel recipient = new ContactModel(name, phoneNum);
				Intent i = new Intent(this, MessageInputActivity.class);
				i.putExtra("recipient", recipient);
				startActivity(i);
			} else {
				// check selection is valid
				if (contactsList.getSelectedItemPosition() > 0) {
					passPhoneNumberAtCursorPosition(contactsList
							.getSelectedItemPosition());
				}
			}
			break;
		case UP_AND_DOWN_LONG:
			startHelp();
			break;
		}
	}

	private boolean isPhoneNumber(String phoneNum) {
		char[] c = phoneNum.toCharArray();
		// correct length of phone number
		if ((c.length != 10) && (c.length != 7)) {
			return false;
		}
		for (int i = 0; i < c.length; i++) { // verify all chars is a digit
			if (!Character.isDigit(c[i])) {
				return false;
			}
		}
		return true;
	}

	public static class ContactCursor extends CursorWrapper {
		private Cursor mCursor;

		public ContactCursor(Cursor cursor) {
			super(cursor);
			mCursor = cursor;
		}

		@Override
		public int getCount() {
			return mCursor.getCount() + 1;
		}

	}
}
