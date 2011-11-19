package edu.berkeley.cs169.util;

import android.view.KeyEvent;
import edu.berkeley.cs169.util.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener.ResultCode;

public class NavigationKeyInterpreter {
	public static long HOLD_THRESHOLD = 3 * Utils.INPUT_SPEED_BASE;

	NavigationKeyInterpreterResultListener listener;

	long upKeyDownTimestamp = -1;
	long downKeyDownTimestamp = -1;

	long keyRepeatThreshold = -1;
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
				upKeyDownTimestamp = event.getEventTime();
				keyRepeated = 0;
			} else if (keyRepeatThreshold > 0 && dt > keyRepeatThreshold
					&& downKeyDownTimestamp == -1) {
				upKeyDownTimestamp = event.getEventTime();
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
				downKeyDownTimestamp = event.getEventTime();
				keyRepeated = 0;
			} else if (keyRepeatThreshold > 0 && dt > keyRepeatThreshold
					&& upKeyDownTimestamp == -1) {
				downKeyDownTimestamp = event.getEventTime();
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
					if (upDt > HOLD_THRESHOLD && downDt > HOLD_THRESHOLD) {
						listener.onNavKeyInterpreterResult(ResultCode.UP_AND_DOWN_LONG);
					} else {
						listener.onNavKeyInterpreterResult(ResultCode.UP_AND_DOWN);
					}
				} else if (upKeyDownTimestamp != -1) {
					listener.onNavKeyInterpreterResult(ResultCode.UP);
				} else if (downKeyDownTimestamp != -1) {
					listener.onNavKeyInterpreterResult(ResultCode.DOWN);
				}
			}
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
