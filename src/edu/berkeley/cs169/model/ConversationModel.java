package edu.berkeley.cs169.model;

import java.util.List;

public class ConversationModel {
	private List<MessageModel> messages;
	private ContactModel other;
	
	

	public ConversationModel(List<MessageModel> messages, ContactModel other) {
		super();
		this.messages = messages;
		this.other = other;
	}

	public ContactModel getOther() {
		return other;
	}

	public void setOther(ContactModel o) {
		other = o;
	}

	public List<MessageModel> getMessages() {
		return messages;
	}

	public void setMessages(List<MessageModel> m) {
		messages = m;
	}
}
