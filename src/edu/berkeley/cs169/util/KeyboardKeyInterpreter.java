package edu.berkeley.cs169.util;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Application;
import android.view.KeyEvent;
import edu.berkeley.cs169.BlindlyMessenger;
import edu.berkeley.cs169.model.MorseCodeModel;
import edu.berkeley.cs169.util.KeyboardKeyInterpreter.KeyboardKeyInterpreterResultListener.ResultCode;

public class KeyboardKeyInterpreter {
	public static long DOT_DASH_THRESHOLD = 3;
	public static long LETTER_GAP_THRESHOLD = 4;
	public static long WORD_GAP_THRESHOLD = 10;

	int speedBase;

	KeyboardKeyInterpreterResultListener listener;
	MorseCodeModel model;

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
				if (dt < DOT_DASH_THRESHOLD * speedBase) {
					model.getRawData().add(MorseCodeModel.DOT);
					listener.onKeyboardKeyInterpreterResult(ResultCode.DOT,
							null);
				} else {
					model.getRawData().add(MorseCodeModel.DASH);
					listener.onKeyboardKeyInterpreterResult(ResultCode.DASH,
							null);
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
							listener.onKeyboardKeyInterpreterResult(
									ResultCode.ERROR, null);
						} else {
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
						model.getRawData().add(MorseCodeModel.SPACE);
						char lastChar = model.getLastChar();
						if (lastChar == 0) {
							listener.onKeyboardKeyInterpreterResult(
									ResultCode.ERROR, null);
						} else {
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
					listener.onKeyboardKeyInterpreterResult(
							ResultCode.LAST_LETTER, lastChar);
				}

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

		// May be called from a non-UI thread
		public void onKeyboardKeyInterpreterResult(ResultCode code,
				Object result);

		public boolean onKeyDown(int keyCode, KeyEvent event);

		public boolean onKeyUp(int keyCode, KeyEvent event);

		public Application getApplication();
	}
}
