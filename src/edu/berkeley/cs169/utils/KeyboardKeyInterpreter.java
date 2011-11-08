package edu.berkeley.cs169.utils;

import java.util.ArrayList;

import android.view.KeyEvent;
import edu.berkeley.cs169.datamodels.MorseCodeModel;

public class KeyboardKeyInterpreter {
	public static long DOT_DASH_THRESHOLD = 2 * Utils.INPUT_SPEED_BASE;
	public static long LETTER_GAP_THRESHOLD = 3 * Utils.INPUT_SPEED_BASE;
	public static long WORD_GAP_THRESHOLD = 7 * Utils.INPUT_SPEED_BASE;

	KeyboardKeyInterpreterResultListener listener;
	MorseCodeModel model;

	long upKeyDownTimestamp = -1;
	long upKeyUpTimestamp = -1;
	long downKeyDownTimestamp = -1;

	public KeyboardKeyInterpreter(KeyboardKeyInterpreterResultListener listener) {
		this.listener = listener;
		model = new MorseCodeModel(new ArrayList<Long>());
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		long dt = event.getEventTime() - upKeyUpTimestamp;
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:

			if (upKeyUpTimestamp != -1) {
				if (dt > WORD_GAP_THRESHOLD) {
					listener.onKeyInterpreterResult(
							KeyboardKeyInterpreterResultListener.LAST_LETTER,
							model.getLastChar());
					model.getRawData().add(MorseCodeModel.SPACE);
					model.getRawData().add(MorseCodeModel.SPACE);
					listener.onKeyInterpreterResult(
							KeyboardKeyInterpreterResultListener.WORD_GAP, null);
				} else if (dt > LETTER_GAP_THRESHOLD) {
					listener.onKeyInterpreterResult(
							KeyboardKeyInterpreterResultListener.LAST_LETTER,
							model.getLastChar());
					model.getRawData().add(MorseCodeModel.SPACE);
					listener.onKeyInterpreterResult(
							KeyboardKeyInterpreterResultListener.LETTER_GAP,
							null);
				}
			}

			if (upKeyDownTimestamp == -1) {
				upKeyDownTimestamp = event.getEventTime();
				upKeyUpTimestamp = -1;
			}
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (downKeyDownTimestamp == -1) {
				downKeyDownTimestamp = event.getEventTime();
			}
			return true;
		}
		return false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		long dt = event.getEventTime() - upKeyDownTimestamp;
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (upKeyDownTimestamp != -1) {
				if (dt < DOT_DASH_THRESHOLD) {
					model.getRawData().add(MorseCodeModel.DOT);
					listener.onKeyInterpreterResult(
							KeyboardKeyInterpreterResultListener.DOT, null);
				} else {
					model.getRawData().add(MorseCodeModel.DASH);
					listener.onKeyInterpreterResult(
							KeyboardKeyInterpreterResultListener.DASH, null);
				}
			}
			if (upKeyUpTimestamp == -1) {
				upKeyDownTimestamp = -1;
				upKeyUpTimestamp = event.getEventTime();
			}
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (downKeyDownTimestamp != -1) {
				listener.onKeyInterpreterResult(6, null);
			}
			return true;
		}
		return false;
	}

	public interface KeyboardKeyInterpreterResultListener {
		public static final int DOT = 1;
		public static final int DASH = 2;
		public static final int LETTER_GAP = 3;
		public static final int WORD_GAP = 4;
		public static final int LAST_LETTER = 5;
		public static final int DONE = 6;

		public void onKeyInterpreterResult(int resultCode, Object result);

		public boolean onKeyDown(int keyCode, KeyEvent event);

		public boolean onKeyUp(int keyCode, KeyEvent event);
	}
}
