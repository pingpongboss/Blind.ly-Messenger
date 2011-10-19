package edu.berkeley.cs169.datamodels;


public class MorseCodeModel {
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
