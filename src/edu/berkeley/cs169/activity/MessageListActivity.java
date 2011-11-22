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
import edu.berkeley.cs169.util.NavigationKeyInterpreter;
import edu.berkeley.cs169.util.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;
import edu.berkeley.cs169.util.Utils;

public class MessageListActivity extends ListActivity implements
		NavigationKeyInterpreterResultListener {

	BlindlyMessenger app;
	private NavigationKeyInterpreter keyInterpreter;
	ArrayList<ConversationModel> conversationList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (BlindlyMessenger) getApplication();

		setContentView(R.layout.message_list);

		// Register handler for UI elements
		keyInterpreter = new NavigationKeyInterpreter(this, 200, 5);

		// populate the lists
		conversationList = new ArrayList<ConversationModel>();

		new Thread(new Runnable() {

			public void run() {
				populateConversationList();

				if (conversationList.size() == 0) {
					setOnEmpty();
					return;
				}
				MessageListActivity.this.runOnUiThread(new Runnable() {

					public void run() {
						((MessageListAdapter) getListAdapter())
								.notifyDataSetChanged();
					}
				});
			}
		}).start();

		getListView().setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long i) {
				ContactModel person = conversationList.get(position).getOther();
				app.output(person.toString());
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		if (!app.isTouch()) {
			getListView().setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});
		}

		setListAdapter(new MessageListAdapter(this, R.layout.message_list_item,
				conversationList));
	}

	@Override
	protected void onResume() {
		super.onResume();
		String alert = getResources()
				.getString(R.string.message_list_shortcode);
		app.vibrate(alert);

		String greeting = getResources().getString(R.string.message_list_tts);
		app.speak(greeting);

		Utils.blankScreen(this);
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

	public void onNavKeyInterpreterResult(ResultCode code) {
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
			openConversation();
			break;
		case UP_AND_DOWN_LONG:
			starthelp();
			break;
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (app.isTouch()) {
			openConversationPosition(position);
		}
	}

	private void starthelp() {
		String alert = getResources().getString(R.string.message_list_help);

		app.output(alert);
	}

	private void populateConversationList() {
		ArrayList<ConversationModel> conversations = new ArrayList<ConversationModel>();
		conversationList.clear();

		int counter = 0;
		Cursor cursor = getContentResolver().query(Uri.parse("content://sms"),
				null, null, null, null);
		startManagingCursor(cursor);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		int messageLimit = Integer.parseInt(prefs.getString("messages", "200"));

		while (cursor.moveToNext() && (counter < messageLimit)) {
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
				// received
				from = new ContactModel(name, number);
				to = app.getMyContact();
				message = new MessageModel(body, from, to);
				other = from;
			} else if (type.equals("2")) {
				// sent
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
			for (int i = 0; i < conversations.size(); i++) {
				ConversationModel conversation = conversations.get(i);
				if (conversation.getOther().equals(other)) {
					// it belongs here
					conversation.getMessages().add(0, message);
					inserted = true;
					break;
				}
			}

			if (!inserted) {
				// add new conversation to list
				List<MessageModel> messages = new ArrayList<MessageModel>();
				messages.add(message);
				ConversationModel conversation = new ConversationModel(
						messages, other);
				conversations.add(conversation);
			}

			counter++;
		}

		conversationList.addAll(conversations);
	}

	private void openConversation() {
		ConversationModel conversation = (ConversationModel) getListView()
				.getSelectedItem();

		if (conversation != null) {
			Intent i = new Intent(this, ConversationViewActivity.class);
			i.putExtra("conversation", conversation);
			startActivity(i);
		}
	}

	private void openConversationPosition(int position) {
		ConversationModel conversation = (ConversationModel) conversationList
				.get(position);

		if (conversation != null) {
			Intent i = new Intent(this, ConversationViewActivity.class);
			i.putExtra("conversation", conversation);
			startActivity(i);
		}
	}

	private void setOnEmpty() { // Display "No messages"
		TextView empty = (TextView) findViewById(android.R.id.empty);
		empty.setText("No\nConversations");
	}
}
