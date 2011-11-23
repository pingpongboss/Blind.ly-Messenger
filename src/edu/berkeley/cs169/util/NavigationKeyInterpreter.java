package edu.berkeley.cs169.util;

import android.view.KeyEvent;
import edu.berkeley.cs169.util.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener.ResultCode;

//Key Event Intrepreter for simple navigation
//each instance hooks in with an Activity and interprets its key events
public class NavigationKeyInterpreter {
	public static long HOLD_THRESHOLD = 3 * Utils.INPUT_SPEED_BASE;

	NavigationKeyInterpreterResultListener listener;

	// manipulate timestamps to determine whether a tap or hold has occured
	long upKeyDownTimestamp = -1;
	long downKeyDownTimestamp = -1;

	// how long to hold before the REPEAT event is fired
	long keyRepeatThreshold = -1;
	// how long to hold until REPEAT_LONG event is fired
	long keyRepeatLongThreshold = -1;
	int keyRepeated = 0;

	public NavigationKeyInterpreter(
			NavigationKeyInterpreterResultListener listener) {
		this.listener = listener;
	}

	public NavigationKeyInterpreter(
			NavigationKeyInterpreterResultListener listener,
			long keyRepeatThreshold) {
		this.listener = listener;
		this.keyRepeatThreshold = keyRepeatThreshold;
	}

	public NavigationKeyInterpreter(
			NavigationKeyInterpreterResultListener listener,
			long keyRepeatThreshold, long keyRepeatLongThreshold) {
		this.listener = listener;
		this.keyRepeatThreshold = keyRepeatThreshold;
		this.keyRepeatLongThreshold = keyRepeatLongThreshold;
	}

	public long getKeyRepeatLongThreshold() {
		return keyRepeatLongThreshold;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP: {
			long dt = event.getEventTime() - upKeyDownTimestamp;
			if (upKeyDownTimestamp == -1) {
				// set keyDown timestamp
				upKeyDownTimestamp = event.getEventTime();
				keyRepeated = 0;
			} else if (keyRepeatThreshold > 0 && dt > keyRepeatThreshold
					&& downKeyDownTimestamp == -1) {
				// key has been held down past the keyRepeatThreshold
				upKeyDownTimestamp = event.getEventTime();
				// determine whether to alert REPEAT or REPEAT_LONG
				if (keyRepeated < keyRepeatLongThreshold)
					listener.onNavKeyInterpreterResult(ResultCode.UP_REPEAT);
				else
					listener.onNavKeyInterpreterResult(ResultCode.UP_REPEAT_LONG);
				keyRepeated++;
			}
			return true;
		}
		case KeyEvent.KEYCODE_VOLUME_DOWN: {
			long dt = event.getEventTime() - downKeyDownTimestamp;
			if (downKeyDownTimestamp == -1) {
				// set keyDown timestamp
				downKeyDownTimestamp = event.getEventTime();
				keyRepeated = 0;
			} else if (keyRepeatThreshold > 0 && dt > keyRepeatThreshold
					&& upKeyDownTimestamp == -1) {
				// key has been held down past the keyRepeatThreshold
				downKeyDownTimestamp = event.getEventTime();
				// determine whether to alert REPEAT or REPEAT_LONG
				if (keyRepeated < keyRepeatLongThreshold)
					listener.onNavKeyInterpreterResult(ResultCode.DOWN_REPEAT);
				else
					listener.onNavKeyInterpreterResult(ResultCode.DOWN_REPEAT_LONG);
				keyRepeated++;
			}
			return true;
		}
		}
		return false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		long upDt = event.getEventTime() - upKeyDownTimestamp;
		long downDt = event.getEventTime() - downKeyDownTimestamp;
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (keyRepeated == 0) {
				if (upKeyDownTimestamp != -1 && downKeyDownTimestamp != -1) {
					// both UP and DOWN were pressed before either was released
					if (upDt > HOLD_THRESHOLD && downDt > HOLD_THRESHOLD) {
						// held for a while
						listener.onNavKeyInterpreterResult(ResultCode.UP_AND_DOWN_LONG);
					} else {
						// tapped real fast
						listener.onNavKeyInterpreterResult(ResultCode.UP_AND_DOWN);
					}
				} else if (upKeyDownTimestamp != -1) {
					// DOWN was never pressed
					listener.onNavKeyInterpreterResult(ResultCode.UP);
				} else if (downKeyDownTimestamp != -1) {
					// UP was never pressed
					listener.onNavKeyInterpreterResult(ResultCode.DOWN);
				}
			}

			// reset timestamps
			upKeyDownTimestamp = -1;
			downKeyDownTimestamp = -1;
			return true;
		}
		return false;
	}

	public interface NavigationKeyInterpreterResultListener {
		public enum ResultCode {
			UP, UP_REPEAT, UP_REPEAT_LONG, DOWN, DOWN_REPEAT, DOWN_REPEAT_LONG, UP_AND_DOWN, UP_AND_DOWN_LONG,
		}

		// May be called from a non-UI thread
		public void onNavKeyInterpreterResult(ResultCode code);

		public boolean onKeyDown(int keyCode, KeyEvent event);

		public boolean onKeyUp(int keyCode, KeyEvent event);
	}
}
