package edu.berkeley.cs169.utils;

import edu.berkeley.cs169.datamodels.MorseCodeModel;
import edu.berkeley.cs169.ttm.TextToMorse;

public class BMUtils {
	public static MorseCodeModel textToMorse(String text) {
		long[] data = TextToMorse.pattern(text);
		return new MorseCodeModel(data);
	}

	public static String morseToText(MorseCodeModel morse) {
		return null;
	}
}
