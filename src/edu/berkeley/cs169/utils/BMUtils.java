package edu.berkeley.cs169.utils;

import edu.berkeley.cs169.datamodels.MorseCodeModel;
import edu.berkeley.cs169.ttm.TextToMorse;

public class BMUtils {
	public static long[] textToMorse(String text) {
		return TextToMorse.pattern(text);
	}

	public static String morseToText(MorseCodeModel morse) {
		return null;
	}
}
