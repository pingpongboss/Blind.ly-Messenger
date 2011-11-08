package edu.berkeley.cs169.utils;

import edu.berkeley.cs169.utils.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;
import android.view.KeyEvent;

public class KeyboardKeyInterpreter {
	public static long DOT_DASH_THRESHOLD = 200; // 200 ms

	NavigationKeyInterpreterResultListener listener;

	long upKeyDownTimestamp = -1;
	long upKeyUpTimestamp = -1;

	public KeyboardKeyInterpreter(
			NavigationKeyInterpreterResultListener listener) {
		this.listener = listener;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			//alert if space
			upKeyDownTimestamp = event.getEventTime();
			upKeyUpTimestamp = -1;
			return true;
		}
		return false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			// alert dot/dash
			upKeyDownTimestamp = -1;
			upKeyUpTimestamp = event.getEventTime();
			return true;
		}
		return false;
	}

	public interface KeyboardKeyInterpreterResultListener {
		public static final int DOT = 0;
		public static final int DASH = 1;
		public static final int UP_AND_DOWN = 2;

		public void onKeyInterpreterResult(int resultCode);

		public boolean onKeyDown(int keyCode, KeyEvent event);

		public boolean onKeyUp(int keyCode, KeyEvent event);
	}
}
