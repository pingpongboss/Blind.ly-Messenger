package edu.berkeley.cs169.datamodels;

import java.util.List;

public class MorseCodeModel {
	private List<Integer> rawData; 
	
	public List<Integer> getRawDate (){
		return rawData;
	}
	
	public void setRawDate(Integer n){
		rawData.add(n);
	}
}
