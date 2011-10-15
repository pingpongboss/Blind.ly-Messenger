package edu.berkeley.cs169.datamodels;

import java.util.List;

public class ConversationModel {
	private List<MessageModel> messages;
	private ContactModel other;

	public ContactModel getOther (){
		return other;
	}
	
	public void setOther(ContactModel o){
		other = o;
	}
	
	public List<MessageModel> getMessage () {
		return messages;
	}
	
	public void setMessage (MessageModel m){
		messages.add(m);
	}
	
}
