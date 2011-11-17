package edu.berkeley.cs169.datamodels;

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
