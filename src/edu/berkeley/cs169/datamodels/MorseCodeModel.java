package edu.berkeley.cs169.datamodels;

import java.util.ArrayList;

import edu.berkeley.cs169.utils.VibrateConstants;

public class MorseCodeModel {
	// use SPACE to separate letters, two SPACEs to separate words
	// this representation is ONLY used for the MorseCodeModel
	// DO NOT USE THIS ANYWHERE ELSE
	public static final long DOT = 1;
	public static final long DASH = 2;
	public static final long SPACE = 0;

	ArrayList<Long> rawData;

	public MorseCodeModel(ArrayList<Long> data) {
		rawData = data;
	}

	// "ab" => 'b'
	// "ab c " => ' '
	public char getLastChar() {
		return 0;
	}

	public ArrayList<Long> getRawData() {
		return rawData;
	}

	public void setRawData(ArrayList<Long> r) {
		rawData = r;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof MorseCodeModel)) {
			return false;
		}
		ArrayList<Long> otherRawData = ((MorseCodeModel) obj).rawData;
		if (otherRawData.size() != this.rawData.size())
			return false;

		for (int i = 0; i < this.rawData.size(); i++) {
			if (this.rawData.get(i) != otherRawData.get(i))
				return false;
		}

		return true;
	}

	// convert BMVibrateConstant morse code representation to the MorseCodeModel
	// morse
	// code representation
	public static long[] convert(long[] pattern) {
		int len = pattern.length;

		int count = 0;
		for (int i = 0; i < len; i++) {
			if (pattern[i] != VibrateConstants.GAP) {
				count++;
			}
		}

		long[] toReturn = new long[count];
		for (int i = 0; i < count; i++) {
			if (pattern[i] == VibrateConstants.DASH) {
				toReturn[i] = MorseCodeModel.DASH;
			} else if (pattern[i] == VibrateConstants.DOT) {
				toReturn[i] = MorseCodeModel.DOT;
			}
		}
		return toReturn;
	}

	/** The characters from 'A' to 'Z' */
	public static final long[][] LETTERS = new long[][] {
	/* A */new long[] { DOT, DASH },
	/* B */new long[] { DASH, DOT, DOT, DOT },
	/* C */new long[] { DASH, DOT, DASH, DOT },
	/* D */new long[] { DASH, DOT, DOT },
	/* E */new long[] { DOT },
	/* F */new long[] { DOT, DOT, DASH, DOT },
	/* G */new long[] { DASH, DASH, DOT },
	/* H */new long[] { DOT, DOT, DOT, DOT },
	/* I */new long[] { DOT, DOT },
	/* J */new long[] { DOT, DASH, DASH, DASH },
	/* K */new long[] { DASH, DOT, DASH },
	/* L */new long[] { DOT, DASH, DOT, DOT },
	/* M */new long[] { DASH, DASH },
	/* N */new long[] { DASH, DOT },
	/* O */new long[] { DASH, DASH, DASH },
	/* P */new long[] { DOT, DASH, DASH, DOT },
	/* Q */new long[] { DASH, DASH, DOT, DASH },
	/* R */new long[] { DOT, DASH, DOT },
	/* S */new long[] { DOT, DOT, DOT },
	/* T */new long[] { DASH },
	/* U */new long[] { DOT, DOT, DASH },
	/* V */new long[] { DOT, DOT, DASH },
	/* W */new long[] { DOT, DASH, DASH },
	/* X */new long[] { DASH, DOT, DOT, DASH },
	/* Y */new long[] { DASH, DOT, DASH, DASH },
	/* Z */new long[] { DASH, DASH, DOT, DOT }, };

	/** The characters from '0' to '9' */
	public static final long[][] NUMBERS = new long[][] {
	/* 0 */new long[] { DASH, DASH, DASH, DASH, DASH },
	/* 1 */new long[] { DOT, DASH, DASH, DASH, DASH },
	/* 2 */new long[] { DOT, DOT, DASH, DASH, DASH },
	/* 3 */new long[] { DOT, DOT, DOT, DASH, DASH },
	/* 4 */new long[] { DOT, DOT, DOT, DOT, DASH },
	/* 5 */new long[] { DOT, DOT, DOT, DOT, DOT },
	/* 6 */new long[] { DASH, DOT, DOT, DOT, DOT },
	/* 7 */new long[] { DASH, DASH, DOT, DOT, DOT },
	/* 8 */new long[] { DASH, DASH, DASH, DOT, DOT },
	/* 9 */new long[] { DASH, DASH, DASH, DASH, DOT }, };

	public static long[] pattern(char c) {
		/** Return the pattern data for a given character */
		if (c >= 'A' && c <= 'Z') {
			return LETTERS[c - 'A'];
		}
		if (c >= 'a' && c <= 'z') {
			return LETTERS[c - 'a'];
		} else if (c >= '0' && c <= '9') {
			return NUMBERS[c - '0'];
		} else {
			return VibrateConstants.ERROR_GAP;
		}
	}
}
