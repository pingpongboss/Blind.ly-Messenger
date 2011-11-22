package edu.berkeley.cs169.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageModel implements Parcelable {
	private String content;
	private ContactModel from;
	private ContactModel to;

	public MessageModel(String content, ContactModel from, ContactModel to) {
		this.content = content;
		this.from = from;
		this.to = to;
	}

	@Override
	public String toString() {
		return String.format("Message from %s to %s: %s", from, to, content);
	}
	
	public String toShortString(ContactModel me) {
		if (me.equals(from)) { //if I am the sender
			return String.format("You said: %s", content);
		}
		else { //if i am the recipient
			return String.format("They said: %s", content);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MessageModel) {
			MessageModel m = (MessageModel) o;
			return content.equals(m.getContent()) && from.equals(m.getFrom())
					&& to.equals(m.getTo());
		}
		return false;
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

	public static final Parcelable.Creator<MessageModel> CREATOR = new Parcelable.Creator<MessageModel>() {
		public MessageModel createFromParcel(Parcel in) {
			return new MessageModel(in);
		}

		public MessageModel[] newArray(int size) {
			return new MessageModel[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(content);
		dest.writeParcelable(from, 0);
		dest.writeParcelable(to, 0);
	}

	private MessageModel(Parcel in) {
		content = in.readString();
		from = in.readParcelable(ContactModel.class.getClassLoader());
		to = in.readParcelable(ContactModel.class.getClassLoader());
	}
}
