package edu.berkeley.cs169.datamodels;

public class MessageModel {
	private String content;
	private ContactModel from;
	private ContactModel to;

	public MessageModel(String content, ContactModel from, ContactModel to) {
		this.content = content;
		this.from = from;
		this.to = to;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String newcontent) {
		content = newcontent;
	}

	public ContactModel getFrom() {
		return from;
	}

	public void setFrom(ContactModel f) {
		from = f;
	}

	public ContactModel getTo() {
		return to;
	}

	public void setTo(ContactModel t) {
		to = t;
	}
}
