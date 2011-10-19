package edu.berkeley.cs169.datamodels;

public class ContactModel {
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
}
