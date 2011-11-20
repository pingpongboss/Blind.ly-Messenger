package edu.berkeley.cs169.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.berkeley.cs169.R;
import edu.berkeley.cs169.model.ContactModel;
import edu.berkeley.cs169.model.ConversationModel;
import edu.berkeley.cs169.model.MessageModel;

public class MessageModelAdapter extends ArrayAdapter<MessageModel> {
	ConversationModel mConversation;
	ContactModel mMyContact;
	LayoutInflater mInflater;

	public MessageModelAdapter(Context context, int textViewResourceId,
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
			convertView = mInflater.inflate(R.layout.conversation_list_item,
					parent, false);
			holder = new ViewHolder();

			holder.layout = (LinearLayout) convertView
					.findViewById(R.id.layout);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.content = (TextView) convertView.findViewById(R.id.content);

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

			if (message.getFrom().equals(mMyContact))
				holder.layout.setGravity(Gravity.LEFT);
			else
				holder.layout.setGravity(Gravity.RIGHT);
		}

		return convertView;
	}

	static class ViewHolder {
		LinearLayout layout;
		TextView name;
		TextView content;
	}
}
