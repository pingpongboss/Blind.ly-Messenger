package edu.berkeley.cs169.datamodels;

public class MessageModel {
	private String content;
	ContactModel from;
	ContactModel to;
	
	public String getContent (){
		return content;
	}
	
	public void setContent (String newcontent){
		content = newcontent;
	}
	
}
