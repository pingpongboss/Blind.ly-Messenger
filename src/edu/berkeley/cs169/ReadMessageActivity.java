package edu.berkeley.cs169;

import edu.berkeley.cs169.utils.Utils;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class ReadMessageActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView view = new TextView(this);
		Cursor c = getContentResolver().query(Uri.parse("content://sms/inbox"),null,null,null, null);
		startManagingCursor(c);
		
		int smsEntriesCount = c.getCount();

		
		    String[] body = new String[smsEntriesCount];
		    String[] number = new String[smsEntriesCount];
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
	            view.setText(number[0] + " " + body[0]);
	            setContentView(view);

	}
	
	protected void startHelp() {
		String alert = getResources().getString(R.string.compose_help);
		Utils.textToVibration(alert, this);
	}

}

