package edu.berkeley.cs169.model;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

//model for a conversation
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

	@Override
	public String toString() {
		String exerpt = "";
		if (!messages.isEmpty())
			exerpt = messages.get(0).getContent();
		return String.format("%s: %s", other, exerpt);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ConversationModel) {
			ConversationModel c = (ConversationModel) o;
			return messages.equals(c.getMessages())
					&& other.equals(c.getOther());
		}
		return false;
	}

	public ConversationModel(Parcel in) {
		messages = in.createTypedArrayList(MessageModel.CREATOR);
		other = in.readParcelable(ContactModel.class.getClassLoader());
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedList(messages);
		dest.writeParcelable(other, 0);
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
