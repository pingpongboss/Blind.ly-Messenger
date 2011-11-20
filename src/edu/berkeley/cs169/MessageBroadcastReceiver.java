package edu.berkeley.cs169;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import edu.berkeley.cs169.activity.PopupActivity;
import edu.berkeley.cs169.model.ContactModel;
import edu.berkeley.cs169.model.MessageModel;

public class MessageBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (!prefs.getBoolean("popup", true))
			return;

		// get the SMS message passed in
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			// retrieve the SMS message received
			Object[] pdus = (Object[]) bundle.get("pdus");
			if (pdus.length > 0) {
				SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdus[0]);

				BlindlyMessenger app = (BlindlyMessenger) context
						.getApplicationContext();
				String number = msg.getOriginatingAddress();
				MessageModel message = new MessageModel(msg.getMessageBody(),
						new ContactModel(app.getNameForNumber(number), number),
						app.getMyContact());

				Intent i = new Intent(context, PopupActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_SINGLE_TOP
						| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				i.putExtra("message", message);

				context.startActivity(i);
			}
		}
	}
}
