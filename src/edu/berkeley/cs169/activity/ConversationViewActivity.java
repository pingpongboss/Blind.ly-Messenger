package edu.berkeley.cs169.activity;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import edu.berkeley.cs169.BlindlyMessenger;
import edu.berkeley.cs169.R;
import edu.berkeley.cs169.model.ConversationModel;
import edu.berkeley.cs169.model.MessageModel;
import edu.berkeley.cs169.util.NavigationKeyInterpreter;
import edu.berkeley.cs169.util.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;

public class ConversationViewActivity extends ListActivity implements
		NavigationKeyInterpreterResultListener {

	BlindlyMessenger app;
	ConversationModel conversation;
	private NavigationKeyInterpreter keyInterpreter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversation_list);

		app = (BlindlyMessenger) getApplication();

		// Register handler for UI elements
		keyInterpreter = new NavigationKeyInterpreter(this, 200, 5);

		// talk to Edmond for name
		conversation = getIntent().getParcelableExtra("conversation");

		populateConversation();
	}

	protected void onResume() {
		super.onResume();
	}

	private void populateConversation() {
		// populate ListActivity with items from ConversationModel

		// put messages in a String (hella ghetto)
		ArrayList<String> messages = new ArrayList<String>();
		for (MessageModel m : conversation.getMessages()) {
			messages.add(m.toString());
		}

		// put ConversationModel into a
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, messages));
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
		// TODO Auto-generated method stub
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
			// open up MessageInputActivity
			break;
		}
	}

}
