package edu.berkeley.cs169.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.TextView;
import edu.berkeley.cs169.BlindlyMessenger;
import edu.berkeley.cs169.R;
import edu.berkeley.cs169.adapter.MessageListAdapter;
import edu.berkeley.cs169.model.ContactModel;
import edu.berkeley.cs169.model.ConversationModel;
import edu.berkeley.cs169.model.MessageModel;
import edu.berkeley.cs169.util.AndroidUtils;
import edu.berkeley.cs169.util.KeyInterpreter;
import edu.berkeley.cs169.util.KeyInterpreter.KeyInterpreterResultListener;

//screen to select a conversation to view
public class MessageListActivity extends ListActivity implements
		KeyInterpreterResultListener {

	BlindlyMessenger app;
	Thread backgroundTask;
	private KeyInterpreter keyInterpreter;
	ArrayList<ConversationModel> conversationList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (BlindlyMessenger) getApplication();

		setContentView(R.layout.message_list);

		// Register handler for key events
		keyInterpreter = new KeyInterpreter(this, 200, 5);

		// populate the ListView's backing ArrayList in the background
		conversationList = new ArrayList<ConversationModel>();

		// read the name of the conversation partner when user selects an item
		getListView().setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long i) {
				ContactModel person = conversationList.get(position).getOther();
				app.output(person.toString());
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		// accommodate touch preference
		if (!app.isTouch()) {
			getListView().setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});
		}

		// display list items
		setListAdapter(new MessageListAdapter(this, R.layout.message_list_item,
				conversationList));

	}

	@Override
	protected void onResume() {
		super.onResume();
		String alert = getResources().getString(
				R.string.message_list_shortcode_loading);
		app.vibrate(alert);

		String greeting = getResources().getString(
				R.string.message_list_tts_loading);
		app.speak(greeting);
		
		conversationList.clear();
		((MessageListAdapter)getListAdapter()).notifyDataSetChanged();

		backgroundTask = new Thread(new Runnable() {

			public void run() {
				startLoadConversations();
			}
		});
		backgroundTask.setPriority(Thread.MIN_PRIORITY);
		backgroundTask.start();

		AndroidUtils.blankScreen(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// interrupt backgroundTask thread if Activity is paused
		if (backgroundTask.isAlive()) {
			backgroundTask.interrupt();
		}

		app.stopOutput();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// creates the context menu to get to settings
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// detects when user clicks on the settings context menu
		switch (item.getItemId()) {
		case R.id.preference:
			Intent i = new Intent(this, MainPreferenceActivity.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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

	public void onKeyInterpreterResult(ResultCode code, Object data) {
		switch (code) {
		case NAVIGATION_UP:
		case NAVIGATION_UP_REPEAT:
			dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
					KeyEvent.KEYCODE_DPAD_UP));
			dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
					KeyEvent.KEYCODE_DPAD_UP));
			break;
		case NAVIGATION_UP_REPEAT_LONG:
			for (int i = 0; i < (Long) data; i++) {
				dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_DPAD_UP));
				dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
						KeyEvent.KEYCODE_DPAD_UP));
			}
			break;
		case NAVIGATION_DOWN:
		case NAVIGATION_DOWN_REPEAT:
			dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
					KeyEvent.KEYCODE_DPAD_DOWN));
			dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
					KeyEvent.KEYCODE_DPAD_DOWN));
			break;
		case NAVIGATION_DOWN_REPEAT_LONG:
			for (int i = 0; i < (Long) data; i++) {
				dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_DPAD_DOWN));
				dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
						KeyEvent.KEYCODE_DPAD_DOWN));
			}
			break;
		case UP_AND_DOWN:
			openConversation();
			break;
		case UP_AND_DOWN_LONG:
			starthelp();
			break;
		}
	}

	// enable tap to go to conversation view
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (app.isTouch()) {
			openConversationPosition(position);
		}
	}

	private void starthelp() {
		String alert = getResources().getString(R.string.message_list_help);

		app.speak(alert, true);
	}

	private void startLoadConversations() {
		populateConversationList();

		if (backgroundTask.isInterrupted())
			return;

		// once finished, notify the user on the main UI thread
		MessageListActivity.this.runOnUiThread(new Runnable() {

			public void run() {
				if (conversationList.size() == 0) {
					alertEmpty();
					return;
				} else {
					String alert = getResources().getString(
							R.string.message_list_shortcode_done);
					app.vibrate(alert);

					String greeting = String.format(
							"%d %s",
							conversationList.size(),
							getResources().getString(
									R.string.message_list_tts_done));
					app.speak(greeting);
				}
				((MessageListAdapter) getListAdapter()).notifyDataSetChanged();
				getListView().requestFocus();
			}
		});
	}

	// makes the ConversationModels from the system's SMS messages
	private void populateConversationList() {
		// temporary ArrayList to avoid populating the real ArrayList partially
		ArrayList<ConversationModel> conversations = new ArrayList<ConversationModel>();
		// clear real ArrayList
		conversationList.clear();

		int counter = 0;

		// get the Cursor representing all SMS messages
		Cursor cursor = getContentResolver().query(Uri.parse("content://sms"),
				null, null, null, null);
		startManagingCursor(cursor);

		// determine how many SMS messages to retrieve
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		int messageLimit = Integer.parseInt(prefs.getString("messages",
				getResources().getString(R.string.default_messages)));

		// go through each SMS message and put it into the correct
		// ConversationModel

		while (!backgroundTask.isInterrupted() && cursor.moveToNext()
				&& (counter < messageLimit)) {

			ContactModel from;
			ContactModel to;
			ContactModel other;
			MessageModel message;

			String number = cursor.getString(cursor.getColumnIndex("address"));
			String body = cursor
					.getString(cursor.getColumnIndexOrThrow("body"));
			String type = cursor
					.getString(cursor.getColumnIndexOrThrow("type"));
			String name = app.getNameForNumber(number);

			if (type.equals("1")) {
				// SMS message is received
				from = new ContactModel(name, number);
				to = app.getMyContact();
				message = new MessageModel(body, from, to);
				other = from;
			} else if (type.equals("2")) {
				// SMS message is sent
				to = new ContactModel(name, number);
				from = app.getMyContact();
				message = new MessageModel(body, from, to);
				other = to;
			} else {
				Log.d("MessageListActivity",
						"Message type not sent or received...");
				continue;
			}

			boolean inserted = false;

			// go through all previous ConversationModels
			// add this MessageModel if the person you are conversing with is
			// the same
			for (int i = 0; i < conversations.size(); i++) {
				ConversationModel conversation = conversations.get(i);
				if (conversation.getOther().equals(other)) {
					// this message belongs in this conversation
					conversation.getMessages().add(0, message);
					inserted = true;
					break;
				}
			}

			// we found no conversations where the message belongs
			if (!inserted) {
				// add this message to a new conversation
				List<MessageModel> messages = new ArrayList<MessageModel>();
				messages.add(message);
				ConversationModel conversation = new ConversationModel(
						messages, other);
				conversations.add(conversation);
			}

			counter++;
		}

		// update real ArrayList with our temporary one
		conversationList.addAll(conversations);
	}

	// starts a new Activity to view the messages in the selected conversation
	private void openConversation() {
		ConversationModel conversation = (ConversationModel) getListView()
				.getSelectedItem();

		if (conversation != null) {
			Intent i = new Intent(this, ConversationViewActivity.class);
			i.putExtra("conversation", conversation);
			startActivity(i);
		}
	}

	// starts a new Activity to view the messages in the tapped conversation
	private void openConversationPosition(int position) {
		ConversationModel conversation = (ConversationModel) conversationList
				.get(position);

		if (conversation != null) {
			Intent i = new Intent(this, ConversationViewActivity.class);
			i.putExtra("conversation", conversation);
			startActivity(i);
		}
	}

	// called if there are no SMS messages in the system
	private void alertEmpty() {
		String error = "No\nConversations";

		TextView empty = (TextView) findViewById(android.R.id.empty);
		empty.setText(error);
		app.output(error);
	}
}
