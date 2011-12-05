package edu.berkeley.cs169.util;

import java.util.ArrayList;

import android.app.Application;
import android.view.KeyEvent;
import edu.berkeley.cs169.BlindlyMessenger;
import edu.berkeley.cs169.model.MorseCodeModel;
import edu.berkeley.cs169.util.KeyInterpreter.KeyInterpreterResultListener.ResultCode;

//Key Event Intrepreter for navigation and morse code keyboard
//each instance hooks in with an Activity and interprets its key events
public class KeyInterpreter {
	public static long HOLD_THRESHOLD = 3;

	int speedBase;

	KeyInterpreterResultListener listener;

	MorseCodeModel model;

	// manipulate timestamps to determine whether a tap or hold has occured
	long upKeyDownTimestamp = -1;
	long upKeyDownOriginalTimestamp = -1;
	long downKeyDownTimestamp = -1;
	long downKeyDownOriginalTimestamp = -1;

	// how long to hold before the REPEAT event is fired
	long keyRepeatThreshold = -1;
	// how long to hold until REPEAT_LONG event is fired
	long keyRepeatLongThreshold = -1;
	int keyRepeated = 0;

	boolean lastLetterOutputted = true;

	public KeyInterpreter(KeyInterpreterResultListener listener) {
		this.listener = listener;

		speedBase = ((BlindlyMessenger) listener.getApplication())
				.getInputSpeedBase();
		model = new MorseCodeModel(new ArrayList<Long>());
	}

	public KeyInterpreter(KeyInterpreterResultListener listener,
			long keyRepeatThreshold) {
		this(listener);
		this.keyRepeatThreshold = keyRepeatThreshold;
	}

	public KeyInterpreter(KeyInterpreterResultListener listener,
			long keyRepeatThreshold, long keyRepeatLongThreshold) {
		this(listener, keyRepeatThreshold);
		this.keyRepeatLongThreshold = keyRepeatLongThreshold;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP: {
			long dt = event.getEventTime() - upKeyDownTimestamp;
			if (upKeyDownTimestamp == -1) {
				// set keyDown timestamp
				upKeyDownTimestamp = event.getEventTime();
				upKeyDownOriginalTimestamp = event.getEventTime();
				keyRepeated = 0;
			} else if (keyRepeatThreshold > 0 && dt > keyRepeatThreshold
					&& downKeyDownTimestamp == -1) {
				// key has been held down past the keyRepeatThreshold
				upKeyDownTimestamp = event.getEventTime();
				// determine whether to alert REPEAT or REPEAT_LONG
				if (keyRepeated < keyRepeatLongThreshold)
					listener.onKeyInterpreterResult(
							ResultCode.NAVIGATION_UP_REPEAT, null);
				else
					listener.onKeyInterpreterResult(
							ResultCode.NAVIGATION_UP_REPEAT_LONG,
							keyRepeatLongThreshold);
				keyRepeated++;
			}
			return true;
		}
		case KeyEvent.KEYCODE_VOLUME_DOWN: {
			long dt = event.getEventTime() - downKeyDownTimestamp;
			if (downKeyDownTimestamp == -1) {
				// set keyDown timestamp
				downKeyDownTimestamp = event.getEventTime();
				downKeyDownOriginalTimestamp = event.getEventTime();
				keyRepeated = 0;
			} else if (keyRepeatThreshold > 0 && dt > keyRepeatThreshold
					&& upKeyDownTimestamp == -1) {
				// key has been held down past the keyRepeatThreshold
				downKeyDownTimestamp = event.getEventTime();
				// determine whether to alert REPEAT or REPEAT_LONG
				if (keyRepeated < keyRepeatLongThreshold)
					listener.onKeyInterpreterResult(
							ResultCode.NAVIGATION_DOWN_REPEAT, null);
				else
					listener.onKeyInterpreterResult(
							ResultCode.NAVIGATION_DOWN_REPEAT_LONG,
							keyRepeatLongThreshold);
				keyRepeated++;
			}
			return true;
		}
		}
		return false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		long upDt = event.getEventTime() - upKeyDownOriginalTimestamp;
		long downDt = event.getEventTime() - downKeyDownOriginalTimestamp;
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (upKeyDownTimestamp != -1 && downKeyDownTimestamp != -1) {
				// both UP and DOWN were pressed before either was released
				if (upDt > HOLD_THRESHOLD * speedBase
						&& downDt > HOLD_THRESHOLD * speedBase) {
					// held for a while
					listener.onKeyInterpreterResult(
							ResultCode.UP_AND_DOWN_LONG, null);
				} else {
					// tapped real fast
					listener.onKeyInterpreterResult(ResultCode.UP_AND_DOWN,
							null);
				}
			} else if (upKeyDownTimestamp != -1) {
				// UP was pressed
				if (keyRepeated == 0) {
					listener.onKeyInterpreterResult(ResultCode.NAVIGATION_UP,
							null);
				}

				if (upDt > HOLD_THRESHOLD * speedBase) {
					// DASH
					model.getRawData().add(MorseCodeModel.DASH);
					listener.onKeyInterpreterResult(ResultCode.KEYBOARD_DASH,
							null);
				} else {
					// DOT
					model.getRawData().add(MorseCodeModel.DOT);
					listener.onKeyInterpreterResult(ResultCode.KEYBOARD_DOT,
							null);
				}
			} else if (downKeyDownTimestamp != -1) {
				// DOWN was pressed
				if (keyRepeated == 0) {
					listener.onKeyInterpreterResult(ResultCode.NAVIGATION_DOWN,
							null);
				}

				model.getRawData().add(MorseCodeModel.SPACE);
				listener.onKeyInterpreterResult(
						ResultCode.KEYBOARD_LAST_LETTER, model.getLastChar());
			}

			// reset timestamps
			upKeyDownTimestamp = -1;
			downKeyDownTimestamp = -1;
			return true;
		}
		return false;
	}

	public interface KeyInterpreterResultListener {
		public enum ResultCode {
			NAVIGATION_UP, NAVIGATION_UP_REPEAT, NAVIGATION_UP_REPEAT_LONG, NAVIGATION_DOWN, NAVIGATION_DOWN_REPEAT, NAVIGATION_DOWN_REPEAT_LONG, KEYBOARD_DOT, KEYBOARD_DASH, KEYBOARD_LAST_LETTER, UP_AND_DOWN, UP_AND_DOWN_LONG,
		}

		// May be called from a non-UI thread
		public void onKeyInterpreterResult(ResultCode code, Object data);

		public boolean onKeyDown(int keyCode, KeyEvent event);

		public boolean onKeyUp(int keyCode, KeyEvent event);

		public Application getApplication();
	}
}
