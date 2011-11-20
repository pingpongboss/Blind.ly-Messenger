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
import edu.berkeley.cs169.BlindlyMessenger;
import edu.berkeley.cs169.R;
import edu.berkeley.cs169.util.NavigationKeyInterpreter;
import edu.berkeley.cs169.util.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;
import edu.berkeley.cs169.util.Utils;

public class MainActivity extends Activity implements
		NavigationKeyInterpreterResultListener {
	BlindlyMessenger app;
	NavigationKeyInterpreter keyInterpreter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		app = (BlindlyMessenger) getApplication();

		keyInterpreter = new NavigationKeyInterpreter(this);

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

		String alert = getResources().getString(R.string.main_shortcode);
		app.vibrate(alert);

		String greeting = getResources().getString(R.string.main_tts);
		app.speak(greeting);

		Utils.blankScreen(this);
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
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.preference:
			Intent i = new Intent(this, MainPreferenceActivity.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void startHelp() {
		String alert = getResources().getString(R.string.main_help);
		app.output(alert);
	}

	protected void startCompose() {
		startActivity(new Intent(this, RecipientInputActivity.class));
	}

	protected void startRead() {
		startActivity(new Intent(this, ReadMessageActivity.class));
	}
}
