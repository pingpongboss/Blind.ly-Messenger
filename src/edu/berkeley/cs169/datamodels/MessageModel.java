package edu.berkeley.cs169.datamodels;

public class MessageModel {
	private String content;
	private ContactModel from;
	private ContactModel to;
	
	public String getContent (){
		return content;
	}
	
	public void setContent (String newcontent){
		content = newcontent;
	}
	
	public ContactModel getSender (){
		return from;
	}
	
	public void setSender (ContactModel s){
		from = s ;
	}
	
	public ContactModel getReceiver (){
		return to;
	}
	
	public void setReceiver (ContactModel r){
		to = r;
	}
}
