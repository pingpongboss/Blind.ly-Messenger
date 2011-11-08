package edu.berkeley.cs169;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import edu.berkeley.cs169.utils.NavigationKeyIntepreter;
import edu.berkeley.cs169.utils.NavigationKeyIntepreter.NavigationKeyInterpreterResultListener;
import edu.berkeley.cs169.utils.Utils;

public class MainActivity extends Activity implements
		NavigationKeyInterpreterResultListener {
	public static final String TAG = "MainActivity";
	NavigationKeyIntepreter keyIntepreter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		keyIntepreter = new NavigationKeyIntepreter(this);

		LinearLayout layoutUp = (LinearLayout) findViewById(R.id.layout_up);
		layoutUp.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startCompose();
			}
		});

		LinearLayout layoutDown = (LinearLayout) findViewById(R.id.layout_down);
		layoutDown.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startRead();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		String alert = getResources().getString(R.string.main_shortcode);
		Utils.textToVibration(alert, this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyIntepreter.onKeyDown(keyCode, event))
			return true;
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyIntepreter.onKeyUp(keyCode, event))
			return true;
		return super.onKeyUp(keyCode, event);
	}

	public void onKeyInterpreterResult(int resultCode) {
		switch (resultCode) {
		case 2: // up && down
			startHelp();
			break;
		case 0: // up
			startCompose();
			break;
		case 1: // down
			startRead();
			break;
		}
	}

	protected void startHelp() {
		String alert = getResources().getString(R.string.main_help);
		Utils.textToVibration(alert, this);
	}

	protected void startCompose() {
		startActivity(new Intent(this, ComposeActivity.class));
	}

	protected void startRead() {
		Utils.textToVibration("t", this);
	}
}
