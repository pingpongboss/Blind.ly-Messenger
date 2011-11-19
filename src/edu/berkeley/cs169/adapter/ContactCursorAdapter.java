package edu.berkeley.cs169.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import edu.berkeley.cs169.R;

public class ContactCursorAdapter extends CursorAdapter {
	LayoutInflater mInflater;
	Cursor mCursor;

	public ContactCursorAdapter(Context context, Cursor c) {
		super(context, c);
		mInflater = LayoutInflater.from(context);
		mCursor = c;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.recipient_input_item,
					parent, false);
			holder = new ViewHolder();

			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.number = (TextView) convertView.findViewById(R.id.number);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String display_name = "";
		String number = "0000000000";
		if (mCursor.moveToPosition(position)) {
			display_name = mCursor.getString(mCursor
					.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			number = mCursor
					.getString(mCursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		} else {
			// cursor failed to move to position
		}

		holder.name.setText(display_name);
		holder.number.setText(number);

		return convertView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return null;
	}

	public void setCursor(Cursor cursor) {
		mCursor = cursor;
	}

	static class ViewHolder {
		TextView name;
		TextView number;
	}
}
