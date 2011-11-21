package edu.berkeley.cs169.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.berkeley.cs169.R;
import edu.berkeley.cs169.model.ContactModel;
import edu.berkeley.cs169.model.ConversationModel;
import edu.berkeley.cs169.model.MessageModel;

public class ConversationViewAdapter extends ArrayAdapter<MessageModel> {
	ConversationModel mConversation;
	ContactModel mMyContact;
	LayoutInflater mInflater;

	public ConversationViewAdapter(Context context, int textViewResourceId,
			ConversationModel conversation, ContactModel myContact) {
		super(context, textViewResourceId, conversation.getMessages());

		mConversation = conversation;
		mMyContact = myContact;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.conversation_view_item,
					parent, false);
			holder = new ViewHolder();

			holder.layout = (LinearLayout) convertView
					.findViewById(R.id.layout);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.content = (TextView) convertView.findViewById(R.id.content);

			holder.pictureLeft = (ImageView) convertView
					.findViewById(R.id.picture_left);
			holder.pictureRight = (ImageView) convertView
					.findViewById(R.id.picture_right);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MessageModel message = mConversation.getMessages().get(position);
		if (message != null) {
			String name = message.getFrom().toString();
			String content = message.getContent();
			holder.name.setText(name);
			holder.content.setText(content);

			if (!message.getFrom().equals(mMyContact)) {
				holder.layout.setGravity(Gravity.LEFT);
				holder.name.setGravity(Gravity.LEFT);
				holder.content.setGravity(Gravity.LEFT);
				holder.pictureLeft.setVisibility(View.VISIBLE);
				holder.pictureRight.setVisibility(View.GONE);
			} else {
				holder.layout.setGravity(Gravity.RIGHT);
				holder.name.setGravity(Gravity.RIGHT);
				holder.content.setGravity(Gravity.RIGHT);
				holder.pictureLeft.setVisibility(View.GONE);
				holder.pictureRight.setVisibility(View.VISIBLE);
			}

			// hide name and picture if same person last text
			if (position > 0
					&& message.getFrom().equals(
							mConversation.getMessages().get(position - 1)
									.getFrom())) {
				holder.name.setVisibility(View.GONE);
				holder.pictureLeft.setVisibility(View.GONE);
				holder.pictureRight.setVisibility(View.GONE);
			} else {
				holder.name.setVisibility(View.VISIBLE);
			}
		}

		return convertView;
	}

	static class ViewHolder {
		LinearLayout layout;

		TextView name;
		TextView content;

		ImageView pictureLeft;
		ImageView pictureRight;
	}
}
