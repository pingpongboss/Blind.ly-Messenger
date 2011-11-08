package edu.berkeley.cs169.utils;

import android.view.KeyEvent;

public class NavigationKeyInterpreter {
	NavigationKeyInterpreterResultListener listener;

	boolean upPressed = false;
	boolean downPressed = false;

	public NavigationKeyInterpreter(
			NavigationKeyInterpreterResultListener listener) {
		this.listener = listener;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			upPressed = true;
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			downPressed = true;
			return true;
		}
		return false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (upPressed && downPressed) {
				listener.onKeyInterpreterResult(2);
			} else if (upPressed) {
				listener.onKeyInterpreterResult(0);
			} else if (downPressed) {
				listener.onKeyInterpreterResult(1);
			}
			upPressed = false;
			downPressed = false;
			return true;
		}
		return false;
	}

	public interface NavigationKeyInterpreterResultListener {
		public static final int UP = 0;
		public static final int DOWN = 1;
		public static final int UP_AND_DOWN = 2;

		public void onKeyInterpreterResult(int resultCode);

		public boolean onKeyDown(int keyCode, KeyEvent event);

		public boolean onKeyUp(int keyCode, KeyEvent event);
	}
}
