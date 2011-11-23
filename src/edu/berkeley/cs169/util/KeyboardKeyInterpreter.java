package edu.berkeley.cs169.util;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Application;
import android.view.KeyEvent;
import edu.berkeley.cs169.BlindlyMessenger;
import edu.berkeley.cs169.model.MorseCodeModel;
import edu.berkeley.cs169.util.KeyboardKeyInterpreter.KeyboardKeyInterpreterResultListener.ResultCode;

//each instance hooks in with an Activity and interprets its key events
public class KeyboardKeyInterpreter {
	public static long DOT_DASH_THRESHOLD = 3;
	public static long LETTER_GAP_THRESHOLD = 4;
	public static long WORD_GAP_THRESHOLD = 10;

	int speedBase;

	KeyboardKeyInterpreterResultListener listener;
	MorseCodeModel model;

	// manipulate timestaps to determine whether a DOT or DASH was inputted
	long upKeyDownTimestamp = -1;
	long upKeyUpTimestamp = -1;
	long downKeyDownTimestamp = -1;

	Timer letterTimer, wordTimer;
	boolean lastLetterOutputted = true;

	public KeyboardKeyInterpreter(KeyboardKeyInterpreterResultListener listener) {
		speedBase = ((BlindlyMessenger) listener.getApplication())
				.getInputSpeedBase();

		this.listener = listener;
		model = new MorseCodeModel(new ArrayList<Long>());

		// timers used to determine when characters end and spaces are inserted
		letterTimer = new Timer();
		wordTimer = new Timer();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (upKeyUpTimestamp != -1) {
				// cancel timers. they will be started again on keyUp
				letterTimer.cancel();
				wordTimer.cancel();
			}

			if (upKeyDownTimestamp == -1) {
				// set keyDown timestamp
				upKeyDownTimestamp = event.getEventTime();
				// reset keyUp timestamp
				upKeyUpTimestamp = -1;
			}
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (downKeyDownTimestamp == -1) {
				// set keyDown timestamp
				downKeyDownTimestamp = event.getEventTime();
			}
			return true;
		}
		return false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// used to determine between DOT and DASH
		long dt = event.getEventTime() - upKeyDownTimestamp;
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (upKeyDownTimestamp != -1) {
				if (dt < DOT_DASH_THRESHOLD * speedBase) {
					// add DOT to morse code model
					model.getRawData().add(MorseCodeModel.DOT);
					// report back to Activity
					listener.onKeyboardKeyInterpreterResult(ResultCode.DOT,
							null);
				} else {
					// add DASH to morse code model
					model.getRawData().add(MorseCodeModel.DASH);
					// report back to Activity
					listener.onKeyboardKeyInterpreterResult(ResultCode.DASH,
							null);
				}
				lastLetterOutputted = false;

				// reset all timers
				letterTimer.cancel();
				letterTimer = new Timer();
				letterTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						// last letter is finished
						model.getRawData().add(MorseCodeModel.SPACE);
						// get the last letter
						char lastChar = model.getLastChar();
						if (lastChar == 0) {
							listener.onKeyboardKeyInterpreterResult(
									ResultCode.ERROR, null);
						} else {
							// report back to Activity
							listener.onKeyboardKeyInterpreterResult(
									ResultCode.LAST_LETTER, lastChar);
							lastLetterOutputted = true;
						}
						listener.onKeyboardKeyInterpreterResult(
								ResultCode.LETTER_GAP, null);
					}
				}, LETTER_GAP_THRESHOLD * speedBase);

				wordTimer.cancel();
				wordTimer = new Timer();
				wordTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						// last word is finished
						model.getRawData().add(MorseCodeModel.SPACE);
						// get the last letter. should be a space
						char lastChar = model.getLastChar();
						if (lastChar == 0) {
							listener.onKeyboardKeyInterpreterResult(
									ResultCode.ERROR, null);
						} else {
							// report back to Activity
							listener.onKeyboardKeyInterpreterResult(
									ResultCode.LAST_LETTER, lastChar);
							lastLetterOutputted = true;
						}
						listener.onKeyboardKeyInterpreterResult(
								ResultCode.WORD_GAP, null);
					}
				}, WORD_GAP_THRESHOLD * speedBase);
			}

			if (upKeyUpTimestamp == -1) {
				// reset keyDown timestamp
				upKeyDownTimestamp = -1;
				// set keyUp timestamp
				upKeyUpTimestamp = event.getEventTime();
			}

			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			// cancel all timers
			letterTimer.cancel();
			wordTimer.cancel();
			if (downKeyDownTimestamp != -1) {
				if (!lastLetterOutputted) {
					// the letterTimer had not yet fired
					char lastChar = model.getLastChar();
					// report back to Activity
					listener.onKeyboardKeyInterpreterResult(
							ResultCode.LAST_LETTER, lastChar);
				}

				// report back to Activity that we are done typing
				listener.onKeyboardKeyInterpreterResult(ResultCode.DONE, model);
			}
			return true;
		}
		return false;
	}

	public interface KeyboardKeyInterpreterResultListener {
		public enum ResultCode {
			ERROR, DOT, DASH, LETTER_GAP, WORD_GAP, LAST_LETTER, DONE
		}

		// possibly called from a non-UI thread
		public void onKeyboardKeyInterpreterResult(ResultCode code,
				Object result);

		public boolean onKeyDown(int keyCode, KeyEvent event);

		public boolean onKeyUp(int keyCode, KeyEvent event);

		public Application getApplication();
	}
}
