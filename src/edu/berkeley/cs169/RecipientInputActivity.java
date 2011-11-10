package edu.berkeley.cs169;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter;
import edu.berkeley.cs169.utils.VolumeKeysRepeater;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;

public class RecipientInputActivity extends Activity implements
		NavigationKeyInterpreterResultListener {
	private ListView mContactList;
	private boolean mShowInvisible, mNumbersOnly;
	private Handler handler;
	private VolumeKeysRepeater volKeyRepeater;
	private NavigationKeyInterpreter keyInterpreter;
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
		handler = new Handler();
		volKeyRepeater = new VolumeKeysRepeater(this, handler);
		keyInterpreter = new NavigationKeyInterpreter(this);

		// Populate the contact list
		populateContactList();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			volKeyRepeater.handleVolDown();
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			volKeyRepeater.handleVolUp();
		}
		if (keyInterpreter.onKeyDown(keyCode, event))
			return true;
		return super.onKeyDown(keyCode, event);
	}


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			volKeyRepeater.handleVolDownUp();
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			volKeyRepeater.handleVolUpUp();
		}
		if (keyInterpreter.onKeyUp(keyCode, event))
			return true;
		return super.onKeyUp(keyCode, event);
	}

	public void onKeyInterpreterResult(int resultCode) {
		switch (resultCode) {
		case UP_AND_DOWN:
			passPhoneNumber();
			break;
		}
	}

	private void passPhoneNumber() {
		Cursor c = (Cursor) mContactList.getSelectedItem();
		if (c != null) {
			long id = Long.parseLong(c.getString(0));
			String num = getContactPhoneNumberByPhoneType(id,
					ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

			Toast.makeText(getApplicationContext(), c.getString(1) + " " + num,
					Toast.LENGTH_LONG).show();

			Intent i = new Intent(this, MessageInputActivity.class);
			i.putExtra("recipient", num);

			startActivity(i);
		} else {
			Toast.makeText(getApplicationContext(), "nothing selected",
					Toast.LENGTH_SHORT).show();
		}
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