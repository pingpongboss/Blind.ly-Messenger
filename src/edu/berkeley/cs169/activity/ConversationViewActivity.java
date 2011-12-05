package edu.berkeley.cs169.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import edu.berkeley.cs169.BlindlyMessenger;
import edu.berkeley.cs169.R;
import edu.berkeley.cs169.adapter.ConversationViewAdapter;
import edu.berkeley.cs169.model.ContactModel;
import edu.berkeley.cs169.model.ConversationModel;
import edu.berkeley.cs169.model.MessageModel;
import edu.berkeley.cs169.util.AndroidUtils;
import edu.berkeley.cs169.util.KeyInterpreter;
import edu.berkeley.cs169.util.KeyInterpreter.KeyInterpreterResultListener;

//screen to view messages in a particular conversation
public class ConversationViewActivity extends ListActivity implements
		KeyInterpreterResultListener {

	BlindlyMessenger app;
	ConversationModel conversation;
	private KeyInterpreter keyInterpreter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.conversation_view);

		app = (BlindlyMessenger) getApplication();

		// Register handler for Key events
		keyInterpreter = new KeyInterpreter(this, 200, 5);

		// get the ConversationModel from MessageListActivity
		conversation = getIntent().getParcelableExtra("conversation");

		// alert the user of the message that gets selected
		getListView().setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long i) {
				app.output(((MessageModel) getListView().getItemAtPosition(
						position)).toShortString(app.getMyContact()));
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

		setListAdapter(new ConversationViewAdapter(this,
				R.layout.conversation_view_item, conversation,
				app.getMyContact()));
	}

	protected void onResume() {
		super.onResume();

		String alert = getResources().getString(
				R.string.conversation_view_shortcode);
		app.vibrate(alert);

		app.speak(getResources().getString(R.string.conversation_view_tts)
				+ conversation.getOther().toString());

		AndroidUtils.blankScreen(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();

		app.stopOutput();
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (app.isTouch()) {
			app.speak(l.getItemAtPosition(position).toString());
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
			ContactModel recipient = conversation.getOther();
			Intent i = new Intent(this, MessageInputActivity.class);
			i.putExtra("recipient", recipient);
			startActivity(i);
			break;
		case UP_AND_DOWN_LONG:
			String alert = getResources().getString(
					R.string.conversation_view_help);
			app.output(alert);
			break;
		}
	}

}
