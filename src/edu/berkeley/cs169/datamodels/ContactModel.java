package edu.berkeley.cs169.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactModel implements Parcelable {
	private String name;
	private String number;

	public ContactModel(String name, String number) {
		super();
		this.name = name;
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String newname) {
		name = newname;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String newnumber) {
		number = newnumber;
	}

	public static final Parcelable.Creator<ContactModel> CREATOR = new Parcelable.Creator<ContactModel>() {
		public ContactModel createFromParcel(Parcel in) {
			return new ContactModel(in);
		}

		public ContactModel[] newArray(int size) {
			return new ContactModel[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(number);
	}

	private ContactModel(Parcel in) {
		name = in.readString();
		number = in.readString();
	}
}
