package edu.berkeley.cs169.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import edu.berkeley.cs169.BlindlyMessenger;
import edu.berkeley.cs169.R;
import edu.berkeley.cs169.model.MessageModel;
import edu.berkeley.cs169.util.AndroidUtils;
import edu.berkeley.cs169.util.NavigationKeyInterpreter;
import edu.berkeley.cs169.util.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;

// entry point of the app
public class MainActivity extends Activity implements
		NavigationKeyInterpreterResultListener {
	BlindlyMessenger app;
	NavigationKeyInterpreter keyInterpreter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		app = (BlindlyMessenger) getApplication();

		// delegates the key presses to the Interpreter
		keyInterpreter = new NavigationKeyInterpreter(this);

		// allow clicking on Up and Down screen elements
		LinearLayout layoutUp = (LinearLayout) findViewById(R.id.layout_up);
		layoutUp.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (!app.isTouch())
					return;

				startCompose();
			}
		});

		LinearLayout layoutDown = (LinearLayout) findViewById(R.id.layout_down);
		layoutDown.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (!app.isTouch())
					return;

				startRead();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Used when transitioning from MessageInputActivity
		// When the user sends a message, MessageInputActivity tells
		// MainActivity which message was sent
		MessageModel message = getIntent().getParcelableExtra("message");
		if (message != null) {
			// alert the user they just sent a message
			app.output(message.toString());
			Toast.makeText(this, "Sent " + message.toString(),
					Toast.LENGTH_SHORT).show();
			// don't alert them more than once
			getIntent().removeExtra("message");
		} else {

			// short code is a quick reminder of which screen the user is on
			String alert = getResources().getString(R.string.main_shortcode);
			app.vibrate(alert);

			// greeting is a longer reminder
			String greeting = getResources().getString(R.string.main_tts);
			app.speak(greeting);
		}

		// blanks the screen depending on settings
		AndroidUtils.blankScreen(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// delegates the key events to the Interpreter
		if (keyInterpreter.onKeyDown(keyCode, event))
			return true;
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// delegates the key events to the Interpreter
		if (keyInterpreter.onKeyUp(keyCode, event))
			return true;
		return super.onKeyUp(keyCode, event);
	}

	// callback from the Interpreter, when it detects that a series of key
	// events has some meanings ag
	public void onNavKeyInterpreterResult(ResultCode code) {
		switch (code) {
		case UP:
			startCompose();
			break;
		case DOWN:
			startRead();
			break;
		case UP_AND_DOWN:
			// nothing to do
			break;
		case UP_AND_DOWN_LONG:
			startHelp();
			break;
		}
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

	// output help message
	protected void startHelp() {
		String alert = getResources().getString(R.string.main_help);
		app.speak(alert, true);
	}

	// start new Activity for composing messages
	protected void startCompose() {
		startActivity(new Intent(this, RecipientInputActivity.class));
	}

	// start new Activity for reading messages
	protected void startRead() {
		startActivity(new Intent(this, MessageListActivity.class));
	}
}
