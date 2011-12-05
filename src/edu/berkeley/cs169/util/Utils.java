package edu.berkeley.cs169.util;

import java.util.ArrayList;

import edu.berkeley.cs169.model.MorseCodeModel;

public class Utils {

	// Utility method that converts string text to MorseCodeModel.
	public static MorseCodeModel textToMorse(String text) {
		boolean lastWasWhitespace;
		int strlen = text.length();

		ArrayList<Long> result = new ArrayList<Long>();
		lastWasWhitespace = true;
		for (int i = 0; i < strlen; i++) {
			char c = text.charAt(i);
			if (Character.isWhitespace(c)) {
				if (!lastWasWhitespace) {
					result.add(MorseCodeModel.SPACE);
					result.add(MorseCodeModel.SPACE);
					lastWasWhitespace = true;
				}
			} else {
				if (!lastWasWhitespace) {
					result.add(MorseCodeModel.SPACE);
				}
				lastWasWhitespace = false;
				long[] letter = MorseCodeModel.pattern(c);
				for (int j = 0; j < letter.length; j++) {
					result.add(letter[j]);
				}
			}
		}
		return new MorseCodeModel(result);
	}

	// Utility method that converts MorsecodeModel to string text.
	public static String morseToText(MorseCodeModel morse) {
		String output = "";
		String word = "";
		String tempWord = "";
		boolean prev = false;

		ArrayList<Long> morseInput = morse.getRawData();

		for (int i = 0; i < morseInput.size(); i++) {
			if (morseInput.get(i).equals(MorseCodeModel.DOT)) {
				prev = false;
				tempWord += MorseCodeModel.DOT;

			} else if (morseInput.get(i).equals(MorseCodeModel.DASH)) {
				prev = false;
				tempWord += MorseCodeModel.DASH;

			} else if (morseInput.get(i) == MorseCodeModel.SPACE) {
				if (prev == true) { // word is ready to be added
					prev = false;
					output += word + " ";
					word = "";
				} else { // constructing a word
					prev = true;
					word += morseWordToText(tempWord);
					tempWord = "";
				}
			}

		}
		if (!word.equals(""))
			output += word;
		if (!tempWord.equals("")) { // if ended without last letter/word being
									// added.
			output += morseWordToText(tempWord);
		}
		return output;
	}

	// Takes string representation of Morse code and returns string letter.
	public static String morseWordToText(String input) {
		String output = "";
		if (input.equals("12")) {
			output = "A";
		} else if (input.equals("2111")) {
			output = "B";
		} else if (input.equals("2121")) {
			output = "C";
		} else if (input.equals("211")) {
			output = "D";
		} else if (input.equals("1")) {
			output = "E";
		} else if (input.equals("1121")) {
			output = "F";
		} else if (input.equals("221")) {
			output = "G";
		} else if (input.equals("1111")) {
			output = "H";
		} else if (input.equals("11")) {
			output = "I";
		} else if (input.equals("1222")) {
			output = "J";
		} else if (input.equals("212")) {
			output = "K";
		} else if (input.equals("1211")) {
			output = "L";
		} else if (input.equals("22")) {
			output = "M";
		} else if (input.equals("21")) {
			output = "N";
		} else if (input.equals("222")) {
			output = "O";
		} else if (input.equals("1221")) {
			output = "P";
		} else if (input.equals("2212")) {
			output = "Q";
		} else if (input.equals("121")) {
			output = "R";
		} else if (input.equals("111")) {
			output = "S";
		} else if (input.equals("2")) {
			output = "T";
		} else if (input.equals("112")) {
			output = "U";
		} else if (input.equals("1112")) {
			output = "V";
		} else if (input.equals("122")) {
			output = "W";
		} else if (input.equals("2112")) {
			output = "X";
		} else if (input.equals("2122")) {
			output = "Y";
		} else if (input.equals("2211")) {
			output = "Z";
		} else if (input.equals("22222")) {
			output = "0";
		} else if (input.equals("12222")) {
			output = "1";
		} else if (input.equals("11222")) {
			output = "2";
		} else if (input.equals("11122")) {
			output = "3";
		} else if (input.equals("11112")) {
			output = "4";
		} else if (input.equals("11111")) {
			output = "5";
		} else if (input.equals("21111")) {
			output = "6";
		} else if (input.equals("22111")) {
			output = "7";
		} else if (input.equals("22211")) {
			output = "8";
		} else if (input.equals("22221")) {
			output = "9";
		}

		return output;
	}

	/** Return the pattern data for a given character */
	private static long[] vibratePattern(char c) {
		if (c >= 'A' && c <= 'Z') {
			return VibrateConstants.LETTERS[c - 'A'];
		}
		if (c >= 'a' && c <= 'z') {
			return VibrateConstants.LETTERS[c - 'a'];
		} else if (c >= '0' && c <= '9') {
			return VibrateConstants.NUMBERS[c - '0'];
		} else {
			return VibrateConstants.ERROR_GAP;
		}
	}

	public static long[] vibratePattern(String str) {
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
				len += vibratePattern(c).length;
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
					result[pos] = VibrateConstants.WORD_GAP;
					pos++;
					lastWasWhitespace = true;
				}
			} else {
				if (!lastWasWhitespace) {
					result[pos] = VibrateConstants.LETTER_GAP;
					pos++;
				}
				lastWasWhitespace = false;
				long[] letter = vibratePattern(c);
				System.arraycopy(letter, 0, result, pos, letter.length);
				pos += letter.length;
			}
		}
		return result;
	}
}
