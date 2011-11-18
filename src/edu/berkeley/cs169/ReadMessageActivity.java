package edu.berkeley.cs169;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.KeyEvent;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import edu.berkeley.cs169.datamodels.ContactModel;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;


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
		
		//populate the message list
		populateMessageList();

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
			readMessage();
			break;
		case UP_AND_DOWN_LONG:
			starthelp();
			break;
		}
	}
	
	private void starthelp() {
		String alert = getResources().getString(R.string.message_input_help);

		app.vibrate(alert);
		app.speak(alert);
	}
	
	private void populateMessageList(){
		Cursor c = getContentResolver().query(Uri.parse("content://sms/inbox"),new String[] { "_id", "thread_id", "address", "person", 
			"date", "body" },null,null, null);
		startManagingCursor(c);
		String[] columns = new String[] {"address", "body"};
		int[] to = new int[] {R.id.message_entry_name, R.id.message_entry_body};
		SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, R.layout.message_entry, c, columns, to);
		setListAdapter(mAdapter);
		/*
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
		    //display the newest message
		    //view.setText(msg);
		    //setContentView(view);
		   
			String[] columns = new String[] {name, content};
			int[] to = new int[] {R.id.message_entry_name, R.id.message_entry_body};
			SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, R.layout.message_entry, c, columns, to);
			setListAdapter(mAdapter);
			//readMessage();
		}
		*/
	}
	
	// playback the message
	private void readMessage() {
		Cursor c = (Cursor) getListView().getSelectedItem();
		if (c != null) {
			//long id = Long.parseLong(c.getString(0));
			String name = app.getNameForNumber(c.getString(2));
			String body = c.getString(5);
			String message = name + " says " + body;
			//app.vibrate(name);
			//app.speak(name);
			app.speak(message);
		}
	}
}
