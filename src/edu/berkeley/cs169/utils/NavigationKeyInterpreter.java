package edu.berkeley.cs169.utils;

import android.view.KeyEvent;

public class NavigationKeyInterpreter {
	public static long HOLD_THRESHOLD = 3 * Utils.INPUT_SPEED_BASE;

	NavigationKeyInterpreterResultListener listener;

	long keyRepeatThreshold = -1;
	long upKeyDownTimestamp = -1;
	long downKeyDownTimestamp = -1;

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

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP: {
			long dt = event.getEventTime() - upKeyDownTimestamp;
			if (upKeyDownTimestamp == -1)
				upKeyDownTimestamp = event.getEventTime();
			else if (keyRepeatThreshold > 0 && dt > keyRepeatThreshold
					&& downKeyDownTimestamp == -1) {
				upKeyDownTimestamp = event.getEventTime();
				listener.onKeyInterpreterResult(NavigationKeyInterpreterResultListener.UP);
			}
			return true;
		}
		case KeyEvent.KEYCODE_VOLUME_DOWN: {
			long dt = event.getEventTime() - downKeyDownTimestamp;
			if (downKeyDownTimestamp == -1)
				downKeyDownTimestamp = event.getEventTime();
			else if (keyRepeatThreshold > 0 && dt > keyRepeatThreshold
					&& upKeyDownTimestamp == -1) {
				downKeyDownTimestamp = event.getEventTime();
				listener.onKeyInterpreterResult(NavigationKeyInterpreterResultListener.UP);
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
			if (upKeyDownTimestamp != -1 && downKeyDownTimestamp != -1) {
				if (upDt > HOLD_THRESHOLD && downDt > HOLD_THRESHOLD) {
					listener.onKeyInterpreterResult(NavigationKeyInterpreterResultListener.UP_AND_DOWN_HOLD);
				} else {
					listener.onKeyInterpreterResult(NavigationKeyInterpreterResultListener.UP_AND_DOWN);
				}
			} else if (upKeyDownTimestamp != -1) {
				listener.onKeyInterpreterResult(NavigationKeyInterpreterResultListener.UP);
			} else if (downKeyDownTimestamp != -1) {
				listener.onKeyInterpreterResult(NavigationKeyInterpreterResultListener.DOWN);
			}
			upKeyDownTimestamp = -1;
			downKeyDownTimestamp = -1;
			return true;
		}
		return false;
	}

	public interface NavigationKeyInterpreterResultListener {
		public static final int UP = 0;
		public static final int DOWN = 1;
		public static final int UP_AND_DOWN = 2;
		public static final int UP_AND_DOWN_HOLD = 3;

		public void onKeyInterpreterResult(int resultCode);

		public boolean onKeyDown(int keyCode, KeyEvent event);

		public boolean onKeyUp(int keyCode, KeyEvent event);
	}
}
