package edu.berkeley.cs169.adapter;

import edu.berkeley.cs169.R;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

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

		mCursor.moveToPosition(position);

		holder.name
				.setText(mCursor.getString(mCursor
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
		holder.number
				.setText(mCursor.getString(mCursor
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
		return convertView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return null;
	}

	static class ViewHolder {
		TextView name;
		TextView number;
	}
}
