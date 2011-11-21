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
import edu.berkeley.cs169.activity.RecipientInputActivity.ContactCursor;

public class RecipientInputAdapter extends CursorAdapter {
	LayoutInflater mInflater;
	ContactCursor mCursor;

	public RecipientInputAdapter(Context context, ContactCursor c) {
		super(context, c);
		mInflater = LayoutInflater.from(context);
		mCursor = c;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int fauxPosition = position - 1;
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

		if (fauxPosition != -1) {
			if (mCursor.moveToPosition(fauxPosition)) {
				convertView.setVisibility(View.VISIBLE);
				convertView.findViewById(R.id.picture).setVisibility(View.VISIBLE);
				holder.name.setVisibility(View.VISIBLE);
				holder.number.setVisibility(View.VISIBLE);
				holder.name
						.setText(mCursor.getString(mCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
				holder.number
						.setText(mCursor.getString(mCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
			} else {
				// cursor failed to move to position
			}
		} else {
			convertView.findViewById(R.id.picture).setVisibility(View.GONE);
			holder.name.setVisibility(View.GONE);
			holder.number.setVisibility(View.GONE);
		}
		return convertView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return null;
	}

	public void setCursor(ContactCursor cursor) {
		mCursor = cursor;
	}

	static class ViewHolder {
		TextView name;
		TextView number;
	}
}
