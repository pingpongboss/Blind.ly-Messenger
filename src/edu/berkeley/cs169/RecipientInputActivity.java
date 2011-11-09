package edu.berkeley.cs169;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;

public class RecipientInputActivity extends Activity implements
		NavigationKeyInterpreterResultListener {
	private ListView mContactList;
	private boolean mShowInvisible, mNumbersOnly;
	NavigationKeyInterpreter keyInterpreter;
	int curPosition = 0;
	int padding = 0;
	public static int PHONE_NUM_OK = 293847;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_manager);

		mContactList = (ListView) findViewById(R.id.contactList);
		
		// Initialize class properties
		mShowInvisible = false;
		mNumbersOnly = true;

		// Register handler for UI elements
		keyInterpreter = new NavigationKeyInterpreter(this);

		// Populate the contact list
		populateContactList();
		
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
		case 2: // power
			passPhoneNumber();
			break;
		case 0: // up
			scrollToPrev();
			break;
		case 1: // down
			scrollToNext();
			break;
		}
	}

	private void passPhoneNumber() {
		Cursor c = (Cursor) mContactList.getItemAtPosition(curPosition);
		long id = Long.parseLong(c.getString(0));
		String num = getContactPhoneNumberByPhoneType(id,
				ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

		Toast.makeText(getApplicationContext(), c.getString(1) + " " + num,
				Toast.LENGTH_LONG).show();

		Intent i = new Intent(this, MessageInputActivity.class);
		i.putExtra("recipient", num);

		startActivity(i);
	}

	private void scrollToNext() {
		if (curPosition == mContactList.getCount() - 1)
			return;
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN));
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN));
//		curPosition += 1;
//		mContactList.setSelection(curPosition);
	}

	private void scrollToPrev() {
		if (curPosition == 0)
			return;
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP));
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP));

//		curPosition -= 1;
//		mContactList.setSelection(curPosition);
	}

	public String getContactPhoneNumberByPhoneType(long contactId, int type) {
		String phoneNumber = null;

		String[] whereArgs = new String[] { String.valueOf(contactId),
				String.valueOf(type) };

		Cursor cursor = getApplicationContext().getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? and "
						+ ContactsContract.CommonDataKinds.Phone.TYPE + " = ?",
				whereArgs, null);

		int phoneNumberIndex = cursor
				.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);

		if (cursor != null) {
			try {
				if (cursor.moveToNext()) {
					phoneNumber = cursor.getString(phoneNumberIndex);
				}
			} finally {
				cursor.close();
			}
		}

		return phoneNumber;
	}

	private void populateContactList() {
		Cursor cursor = getContacts();
		String[] fields = new String[] { ContactsContract.Data.DISPLAY_NAME };
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.contact_entry, cursor, fields,
				new int[] { R.id.contactEntryText });
		mContactList.setAdapter(adapter);
	}

	private Cursor getContacts() {
		// Run query
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = new String[] { ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME };
		String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '"
				+ (mShowInvisible ? "0" : "1") + "'" + " AND "
				+ ContactsContract.Data.HAS_PHONE_NUMBER + " = '"
				+ (mNumbersOnly ? "1" : "0") + "'";
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		return managedQuery(uri, projection, selection, selectionArgs,
				sortOrder);
	}
}
