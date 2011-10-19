package edu.berkeley.cs169.utils;

import edu.berkeley.cs169.datamodels.MorseCodeModel;

public class BMUtils {
	public static MorseCodeModel textToMorse(String text) {
		long[] data = pattern(text);
		return new MorseCodeModel(data);
	}

	public static String morseToText(MorseCodeModel morse) {
		return null;
	}
	


	/** Return the pattern data for a given character */
	private static long[] pattern(char c) {
		if (c >= 'A' && c <= 'Z') {
			return BMConstants.LETTERS[c - 'A'];
		}
		if (c >= 'a' && c <= 'z') {
			return BMConstants.LETTERS[c - 'a'];
		} else if (c >= '0' && c <= '9') {
			return BMConstants.NUMBERS[c - '0'];
		} else {
			return BMConstants.ERROR_GAP;
		}
	}

	private static long[] pattern(String str) {
		boolean lastWasWhitespace;
		int strlen = str.length();

		// Calculate how long our array needs to be.
		int len = 1;
		lastWasWhitespace = true;
		for (int i = 0; i < strlen; i++) {
			char c = str.charAt(i);
			if (Character.isWhitespace(c)) {
				if (!lastWasWhitespace) {
					len++;
					lastWasWhitespace = true;
				}
			} else {
				if (!lastWasWhitespace) {
					len++;
				}
				lastWasWhitespace = false;
				len += pattern(c).length;
			}
		}

		// Generate the pattern array. Note that we put an extra element of 0
		// in at the beginning, because the pattern always starts with the
		// pause,
		// not with the vibration.
		long[] result = new long[len + 1];
		result[0] = 0;
		int pos = 1;
		lastWasWhitespace = true;
		for (int i = 0; i < strlen; i++) {
			char c = str.charAt(i);
			if (Character.isWhitespace(c)) {
				if (!lastWasWhitespace) {
					result[pos] = BMConstants.WORD_GAP;
					pos++;
					lastWasWhitespace = true;
				}
			} else {
				if (!lastWasWhitespace) {
					result[pos] = BMConstants.LETTER_GAP;
					pos++;
				}
				lastWasWhitespace = false;
				long[] letter = pattern(c);
				System.arraycopy(letter, 0, result, pos, letter.length);
				pos += letter.length;
			}
		}
		return result;
	}
}
