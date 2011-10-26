package edu.berkeley.cs169.datamodels;

import edu.berkeley.cs169.utils.BMConstants;

public class MorseCodeModel {
	// use SPACE to separate letters, two SPACEs to separate words
	// this representation is ONLY used for the MorseCodeModel
	// DO NOT USE THIS ANYWHERE ELSE
	public static final long DOT = 1;
	public static final long DASH = 2;
	public static final long SPACE = 0;

	// convert BMConstant morse code representation to the MorseCodeModel morse
	// code representation
	public static long[] convert(long[] pattern) {
		int len = pattern.length;

		int count = 0;
		for (int i = 0; i < len; i++) {
			if (pattern[i] != BMConstants.GAP) {
				count++;
			}
		}
		
		long[] toReturn = new long[count];
		for (int i = 0; i < count; i++) {
			if (pattern[i] == BMConstants.DASH) {
				toReturn[i] = MorseCodeModel.DASH;
			} else if (pattern[i] == BMConstants.DOT) {
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
