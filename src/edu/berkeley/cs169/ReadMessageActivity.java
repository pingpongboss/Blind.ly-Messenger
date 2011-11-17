package edu.berkeley.cs169;

import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import edu.berkeley.cs169.utils.Utils;

public class ReadMessageActivity extends ListActivity {
	String msg = "";
	String content = "";
	String name = "";
	BlindlyMessenger app;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_list);
		
		Cursor c = getContentResolver().query(Uri.parse("content://sms/inbox"),null,null,null, null);
		startManagingCursor(c);
		app = (BlindlyMessenger) getApplication();
		
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
		    
		    String[] columns = new String[] {null, null}; // TODO: find column names
		    int[] to = new int[] {R.id.message_entry_name, R.id.message_entry_body};
		    SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, R.layout.message_entry, c, columns, to);
		    
		    this.setListAdapter(mAdapter);
		    
		    /*
		    c.close();
		    content = body[0];
		    name = getContactNameFromNumber(number[0]);
		    msg = name + ": " + body[0];
		    //display the newest message
		    view.setText(msg);
		    setContentView(view);
		    */
		    //readMessage();
		    }
	}
	
	//playback the message
	private void readMessage() {
		Utils.textToVibration(content, this);
		app.speak(content);
	}
	
	// find the name in the contact list by number
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
 
		//return the original number if no match was found
		return number;
	}
}

