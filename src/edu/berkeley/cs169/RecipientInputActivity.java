package edu.berkeley.cs169;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;

public class RecipientInputActivity extends Activity implements
		NavigationKeyInterpreterResultListener {
	private ListView mContactList;
	private boolean mShowInvisible;
	NavigationKeyInterpreter keyInterpreter;
	int curPosition = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_manager);

		mContactList = (ListView) findViewById(R.id.contactList);

		// Initialize class properties
		mShowInvisible = false;

		// Register handler for UI elements
		keyInterpreter = new NavigationKeyInterpreter(this);

		// Populate the contact list
		populateContactList();

		mContactList.setSelection(curPosition);
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
			break;
		case 0: // up
			scrollToPrev();
			break;
		case 1: // down
			scrollToNext();
			break;
		}
	}

	private void scrollToNext() {
		if (curPosition == mContactList.getCount() - 1)
			return;
		mContactList.setSelection(curPosition++);
		mContactList.clearFocus();
	}

	private void scrollToPrev() {
		if (curPosition == 0)
			return;
		mContactList.setSelection(curPosition--);
		mContactList.clearFocus();
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
				+ (mShowInvisible ? "0" : "1") + "'";
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		return managedQuery(uri, projection, selection, selectionArgs,
				sortOrder);
	}
}
