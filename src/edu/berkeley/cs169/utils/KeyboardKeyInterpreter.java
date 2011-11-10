package edu.berkeley.cs169.utils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.view.KeyEvent;
import edu.berkeley.cs169.datamodels.MorseCodeModel;
import edu.berkeley.cs169.utils.KeyboardKeyInterpreter.KeyboardKeyInterpreterResultListener.ResultCode;

public class KeyboardKeyInterpreter {
	public static long DOT_DASH_THRESHOLD = 3 * Utils.INPUT_SPEED_BASE;
	public static long LETTER_GAP_THRESHOLD = 4 * Utils.INPUT_SPEED_BASE;
	public static long WORD_GAP_THRESHOLD = 10 * Utils.INPUT_SPEED_BASE;

	KeyboardKeyInterpreterResultListener listener;
	MorseCodeModel model;

	long upKeyDownTimestamp = -1;
	long upKeyUpTimestamp = -1;
	long downKeyDownTimestamp = -1;

	Timer letterTimer, wordTimer;
	boolean lastLetterOutputted = true;

	public KeyboardKeyInterpreter(KeyboardKeyInterpreterResultListener listener) {
		this.listener = listener;
		model = new MorseCodeModel(new ArrayList<Long>());

		letterTimer = new Timer();
		wordTimer = new Timer();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (upKeyUpTimestamp != -1) {
				letterTimer.cancel();
				wordTimer.cancel();
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
					listener.onKeyInterpreterResult(ResultCode.DOT, null);
				} else {
					model.getRawData().add(MorseCodeModel.DASH);
					listener.onKeyInterpreterResult(ResultCode.DASH, null);
				}
				lastLetterOutputted = false;

				letterTimer.cancel();
				letterTimer = new Timer();
				letterTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						model.getRawData().add(MorseCodeModel.SPACE);
						char lastChar = model.getLastChar();
						if (lastChar == 0) {
							listener.onKeyInterpreterResult(ResultCode.ERROR,
									null);
						} else {
							listener.onKeyInterpreterResult(
									ResultCode.LAST_LETTER, lastChar);
							lastLetterOutputted = true;
						}
						listener.onKeyInterpreterResult(ResultCode.LETTER_GAP,
								null);
					}
				}, LETTER_GAP_THRESHOLD);

				wordTimer.cancel();
				wordTimer = new Timer();
				wordTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						model.getRawData().add(MorseCodeModel.SPACE);
						char lastChar = model.getLastChar();
						if (lastChar == 0) {
							listener.onKeyInterpreterResult(ResultCode.ERROR,
									null);
						} else {
							listener.onKeyInterpreterResult(
									ResultCode.LAST_LETTER, lastChar);
							lastLetterOutputted = true;
						}
						listener.onKeyInterpreterResult(ResultCode.WORD_GAP,
								null);
					}
				}, WORD_GAP_THRESHOLD);
			}

			if (upKeyUpTimestamp == -1) {
				upKeyDownTimestamp = -1;
				upKeyUpTimestamp = event.getEventTime();
			}

			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			letterTimer.cancel();
			wordTimer.cancel();
			if (downKeyDownTimestamp != -1) {
				if (!lastLetterOutputted) {
					char lastChar = model.getLastChar();
					listener.onKeyInterpreterResult(ResultCode.LAST_LETTER,
							lastChar);
				}

				listener.onKeyInterpreterResult(ResultCode.DONE, null);
			}
			return true;
		}
		return false;
	}

	public interface KeyboardKeyInterpreterResultListener {
		public enum ResultCode {
			ERROR, DOT, DASH, LETTER_GAP, WORD_GAP, LAST_LETTER, DONE
		}

		// May be called from a non-UI thread
		public void onKeyInterpreterResult(ResultCode code, Object result);

		public boolean onKeyDown(int keyCode, KeyEvent event);

		public boolean onKeyUp(int keyCode, KeyEvent event);
	}
}
