package edu.berkeley.cs169;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter;
import edu.berkeley.cs169.utils.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;
import edu.berkeley.cs169.utils.Utils;

public class MainActivity extends Activity implements
		NavigationKeyInterpreterResultListener {
	
	NavigationKeyInterpreter keyInterpreter;
	private TextToSpeech mTts;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		keyInterpreter = new NavigationKeyInterpreter(this);
		
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

		mTts = new TextToSpeech(this, new OnInitListener() {
			
			public void onInit(int status) {
				mTts.speak("Welcome to Blindly Messenger", TextToSpeech.QUEUE_FLUSH, null);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		String alert = getResources().getString(R.string.main_shortcode);
		Utils.textToVibration(alert, this);
		mTts.speak("Welcome to Blindly Messenger", TextToSpeech.QUEUE_FLUSH, null);
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

	public void onKeyInterpreterResult(ResultCode code) {
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

	protected void startHelp() {
		String alert = getResources().getString(R.string.main_help);
		Utils.textToVibration(alert, this);
		mTts.speak(alert, TextToSpeech.QUEUE_FLUSH, null);
	}

	protected void startCompose() {
		startActivity(new Intent(this, RecipientInputActivity.class));
	}

	protected void startRead() {
		startActivity(new Intent(this, ReadMessageActivity.class));
	}

	// Implements TextToSpeech.OnInitListener.
    public void onInit(int status) {
    }
}
