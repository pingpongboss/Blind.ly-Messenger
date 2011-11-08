package edu.berkeley.cs169.utils;

import android.view.KeyEvent;

public class NavigationKeyIntepreter {
	NavigationKeyInterpreterResultListener listener;

	public boolean upPressed = false;
	public boolean downPressed = false;

	public NavigationKeyIntepreter(
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
		public void onKeyInterpreterResult(int resultCode);

		public boolean onKeyDown(int keyCode, KeyEvent event);

		public boolean onKeyUp(int keyCode, KeyEvent event);
	}
}
