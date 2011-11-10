package edu.berkeley.cs169.utils;

import android.app.Activity;
import android.os.Handler;
import android.view.KeyEvent;

public class VolumeKeysRepeater {
	private boolean volDownThreadRunning, cancelVolDownThread,
			volUpThreadRunning, cancelVolUpThread;
	private Activity activity;
	private Handler handler;
	private static int WAIT_TIME = 120;

	public VolumeKeysRepeater(Activity a, Handler h) {
		volDownThreadRunning = false;
		cancelVolDownThread = false;
		volUpThreadRunning = false;
		cancelVolUpThread = false;
		this.activity = a;
		this.handler = h;
	}
	
	public void handleVolDown() {
		if (!volDownThreadRunning) {
			startVolDownThread();
		}
	}

	public void handleVolUp() {
		if (!volUpThreadRunning) {
			startVolUpThread();
		}
	}

	public void handleVolDownUp() {
		cancelVolDownThread = true;
	}

	public void handleVolUpUp() {
		cancelVolUpThread = true;
	}

	public void startVolDownThread() {
		Thread t = new Thread() {

			@Override
			public void run() {

				try {
					volDownThreadRunning = true;
					int counter = 0;
					while (!cancelVolDownThread) {
						counter++;
						handler.post(new Runnable() {

							@Override
							public void run() {
								dispatchDpadDown(activity);
							}
						});

						try {
							int increment = WAIT_TIME - (3 * counter);
							if (increment < 30) {
								increment = 30;
							}
							Thread.sleep(increment);
						} catch (InterruptedException e) {
							throw new RuntimeException(
									"could not wait between vol down repeat", e);
						}
					}
				} finally {
					volDownThreadRunning = false;
					cancelVolDownThread = false;
				}
			}
		};

		t.start();
	}

	public void startVolUpThread() {
		Thread t = new Thread() {

			@Override
			public void run() {

				try {
					volUpThreadRunning = true;
					int counter = 0;
					while (!cancelVolUpThread) {
						counter++;
						handler.post(new Runnable() {

							@Override
							public void run() {
								dispatchDpadUp(activity);
							}
						});

						try {
							int increment = WAIT_TIME - (3 * counter);
							if (increment < 30) {
								increment = 30;
							}
							Thread.sleep(increment);
						} catch (InterruptedException e) {
							throw new RuntimeException(
									"could not wait between vol up repeat", e);
						}
					}
				} finally {
					volUpThreadRunning = false;
					cancelVolUpThread = false;
				}
			}
		};

		t.start();
	}
	
	public static void dispatchDpadDown(Activity a) {
		a.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_DPAD_DOWN));
		a.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
				KeyEvent.KEYCODE_DPAD_DOWN));
	}

	public static void dispatchDpadUp(Activity a) {
		a.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_DPAD_UP));
		a.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
				KeyEvent.KEYCODE_DPAD_UP));
	}
}
