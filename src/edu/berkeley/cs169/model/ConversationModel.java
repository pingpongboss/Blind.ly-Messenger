package edu.berkeley.cs169.model;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class ConversationModel implements Parcelable {
	
	private List<MessageModel> messages;
	private ContactModel other;
	
	public static final Parcelable.Creator<ConversationModel> CREATOR = new Parcelable.Creator<ConversationModel>() {
		public ConversationModel createFromParcel(Parcel in) {
			return new ConversationModel(in);
		}

		public ConversationModel[] newArray(int size) {
			return new ConversationModel[size];
		}
	};


	public ConversationModel(List<MessageModel> messages, ContactModel other) {
		super();
		this.messages = messages;
		this.other = other;
	}

	public ConversationModel(Parcel in) {
		// TODO Auto-generated constructor stub
		in.readList(messages, null);
		other = in.readParcelable(ContactModel.class.getClassLoader());
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeList(messages);
		dest.writeParcelable(other, 0);
	}

	public String[] messagesAsString() {
		String[] m = new String[messages.size()];
		for (int i = 0; i < messages.size(); i++) {
			m[i] = messages.get(i).getFrom().getName() + ":\n" +  messages.get(i).getContent();
		}
		
		return m;
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

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


}
