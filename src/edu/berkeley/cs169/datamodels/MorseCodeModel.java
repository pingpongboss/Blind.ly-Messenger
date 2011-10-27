package edu.berkeley.cs169.datamodels;

import edu.berkeley.cs169.utils.BMVibrateConstants;

public class MorseCodeModel {
	// use SPACE to separate letters, two SPACEs to separate words
	// this representation is ONLY used for the MorseCodeModel
	// DO NOT USE THIS ANYWHERE ELSE
	public static final int DOT = 1;
	public static final int DASH = 2;
	public static final int SPACE = 0;

	// convert BMVibrateConstant morse code representation to the MorseCodeModel morse
	// code representation
	public static long[] convert(long[] pattern) {
		int len = pattern.length;

		int count = 0;
		for (int i = 0; i < len; i++) {
			if (pattern[i] != BMVibrateConstants.GAP) {
				count++;
			}
		}
		
		long[] toReturn = new long[count];
		for (int i = 0; i < count; i++) {
			if (pattern[i] == BMVibrateConstants.DASH) {
				toReturn[i] = MorseCodeModel.DASH;
			} else if (pattern[i] == BMVibrateConstants.DOT) {
				toReturn[i] = MorseCodeModel.DOT;
			} 
		}
		return toReturn;
	}

	private long[] rawData;

	public MorseCodeModel(long[] data) {
		rawData = data;
	}

	public long[] getRawData() {
		return rawData;
	}

	public void setRawData(long[] r) {
		rawData = r;
	}
}
