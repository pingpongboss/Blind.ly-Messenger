package edu.berkeley.cs169;

import edu.berkeley.cs169.utils.Utils;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.widget.TextView;

public class ReadMessageActivity extends Activity {
	String msg = "";
	String content = "";
	String name = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView view = new TextView(this);
		Cursor c = getContentResolver().query(Uri.parse("content://sms/inbox"),null,null,null, null);
		startManagingCursor(c);
		
		int smsEntriesCount = c.getCount();

		
		    String[] body = new String[smsEntriesCount];
		    String[] number = new String[smsEntriesCount];
		    if (c.moveToFirst() == true){
		    if (c.moveToFirst()) 
		    {
		    	for (int i = 0; i < smsEntriesCount; i++) 
		    	{
		    		body[i] = c.getString(c.getColumnIndexOrThrow("body")).toString();
		    		number[i] = c.getString(c.getColumnIndexOrThrow("address")).toString();
		    		c.moveToNext();
		    	}
		    }
		    c.close();
		    content = body[0];
		    name = getContactNameFromNumber(number[0]);
		    msg = name + ": " + body[0];
		    view.setText(msg);
		    setContentView(view);
		    readMessage();
		    }
	}
	private void readMessage() {
		Utils.textToVibration(content, this);
	}
	
	private String getContactNameFromNumber(String number) {
		String[] projection = new String[] {
				Contacts.Phones.DISPLAY_NAME,
				Contacts.Phones.NUMBER };
		
		Uri contactUri = Uri.withAppendedPath(Contacts.Phones.CONTENT_FILTER_URL, Uri.encode(number));
 
		Cursor c = getContentResolver().query(contactUri, projection, null,
				null, null);
 
		if (c.moveToFirst()) {
			String name = c.getString(c
					.getColumnIndex(Contacts.Phones.DISPLAY_NAME));
			return name;
		}
 
		// return the original number if no match was found
		return number;
	}
}

