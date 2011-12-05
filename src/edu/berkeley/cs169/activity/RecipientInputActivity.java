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
import android.widget.TextView;
import android.widget.Toast;
import edu.berkeley.cs169.BlindlyMessenger;
import edu.berkeley.cs169.R;
import edu.berkeley.cs169.adapter.RecipientInputAdapter;
import edu.berkeley.cs169.model.ContactModel;
import edu.berkeley.cs169.util.AndroidUtils;
import edu.berkeley.cs169.util.KeyboardKeyInterpreter;
import edu.berkeley.cs169.util.KeyboardKeyInterpreter.KeyboardKeyInterpreterResultListener;
import edu.berkeley.cs169.util.NavigationKeyInterpreter;
import edu.berkeley.cs169.util.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;

//screen to select which phone number to compose a new message for
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recipient_input);

		app = (BlindlyMessenger) getApplication();

		// hook up the text box so that it filters our list
		filterText = (EditText) findViewById(R.id.search_box);
		filterText.addTextChangedListener(new TextWatcher() {

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

		});

		filterText.requestFocus();
		contactsList = (ListView) findViewById(android.R.id.list);
		contactsList.setClickable(false);

		// Initialize class properties
		mShowInvisible = false;
		firstVolDown = false;

		// default selector for retrieving contacts
		String defaultSelection = String.format("%s = '%s'",
				ContactsContract.CommonDataKinds.Phone.IN_VISIBLE_GROUP,
				mShowInvisible ? "0" : "1");

		// Register handler for key events
		navKeyInterpreter = new NavigationKeyInterpreter(this, 200, 5);
		keyKeyInterpreter = new KeyboardKeyInterpreter(this);

		// get Cursor representing contacts data
		cursor = getContacts(defaultSelection);

		if (cursor.getCount() == 1) {
			setOnEmpty();
			app.output("No Contacts");
		} else {
			// initialize list adapter
			adapter = new RecipientInputAdapter(this, cursor);

			// make list filterable
			adapter.setFilterQueryProvider(new FilterQueryProvider() {
				public Cursor runQuery(CharSequence constraint) {
					if (constraint != null) {
						// ignore spaces
						constraint = constraint.toString().trim();
						String selection = String
								.format("%s = '%s' AND %s LIKE '%s%%'",
										ContactsContract.CommonDataKinds.Phone.IN_VISIBLE_GROUP,
										mShowInvisible ? "0" : "1",
										ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
										constraint.toString());
						// maintain reference to new updated cursor
						cursor = getContacts(selection);
						// update reference of cursor in our
						// ContactCursorAdapter
						adapter.setCursor(cursor);
					}
					return cursor;
				}
			});
		}

		// alert user of contact name when they select it
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

		// accommodate touch preference
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

		// display list items
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

		AndroidUtils.blankScreen(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		app.stopOutput();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// close Cursor when Activity is closed
		cursor.close();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// if the list is focused, key events are navigation events
		// if the filter text box is focused, then key events are morse code
		// keyboard events and navigation events (only UP_AND_DOWN is being
		// listened for when filterText is focused)
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

		app.speak(alert, true);
	}

	// retrieves the contact at the user's selection position
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

	// starts new Activity to compose the message content
	private void passPhoneNumberAtCursorPosition(int position) {
		Cursor c = ((RecipientInputAdapter) getListAdapter()).getCursor();
		// work around the fact that we have an invisible 0th element
		// (used to swap focus between the filter EditText and the ListView)
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

	// retrieves the Cursor representing phone contacts
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

	public void onKeyboardKeyInterpreterResult(
			KeyboardKeyInterpreterResultListener.ResultCode code, Object result) {
		// final copies for anonymous nested methods
		final KeyboardKeyInterpreterResultListener.ResultCode copyCode = code;
		final Object copyResult = result;

		// run on main UI thread whenever we change UI elements
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
			NavigationKeyInterpreterResultListener.ResultCode code) {
		// send the appropriate DPAD_UP or DPAD_DOWN action to the system
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
			String phoneNum = filterText.getText().toString();
			// we allow the user to type a phone number into the filter text box
			// check if there is a valid number
			if (isPhoneNumber(phoneNum)) {
				String name = app.getNameForNumber(phoneNum);
				ContactModel recipient = new ContactModel(name, phoneNum);
				Intent i = new Intent(this, MessageInputActivity.class);
				i.putExtra("recipient", recipient);
				startActivity(i);
			} else {
				// If no phone number in the text box, then check to see if the
				// user has a valid selection in the ListView
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

	// utility method to check for valid phone numbers
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

	// called if no contacts exist on the phone
	private void setOnEmpty() {
		TextView empty = (TextView) findViewById(android.R.id.empty);
		empty.setText("No\nContacts");
	}

}
