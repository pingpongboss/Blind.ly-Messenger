package edu.berkeley.cs169.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.berkeley.cs169.R;
import edu.berkeley.cs169.model.ConversationModel;
import edu.berkeley.cs169.model.MessageModel;

public class ConversationModelAdapter extends ArrayAdapter<ConversationModel> {
	List<ConversationModel> mConversations;
	LayoutInflater mInflater;

	public ConversationModelAdapter(Context context, int textViewResourceId,
			List<ConversationModel> objects) {
		super(context, textViewResourceId, objects);
		mConversations = objects;
		mInflater = LayoutInflater.from(context);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.message_list_item, parent,
					false);
			holder = new ViewHolder();

			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.content = (TextView) convertView.findViewById(R.id.content);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ConversationModel conversation = mConversations.get(position);
		if (conversation != null) {
			List<MessageModel> messageModel = conversation.getMessages();
			int recentMsg = messageModel.size() - 1;

			// It's gonna be either the very first message or the last (for the
			// most recent msg).
			// gonna try last message. change it to get(0) if it's incorrect.
			String name = conversation.getOther().getName();
			String content = conversation.getMessages().get(recentMsg)
					.getContent();
			holder.name.setText(name);
			holder.content.setText(content);
		}

		return convertView;
	}

	static class ViewHolder {
		TextView name;
		TextView content;
	}
}
