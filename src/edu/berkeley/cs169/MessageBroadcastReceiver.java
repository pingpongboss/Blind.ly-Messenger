package edu.berkeley.cs169;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
	private static final int SMS_NOTIFICATION_ID = 1;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras(); // get the SMS message passed in
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

				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(context);
				if (prefs.getBoolean("popup", true)) {
					// show popup alert

					Intent i = getPopupIntent(context, message);

					context.startActivity(i);
				} else {
					// show status bar notification

					int icon = R.drawable.statusbaricon;
					long when = System.currentTimeMillis();
					Context c = context.getApplicationContext();
					String contentTitle = message.getFrom().toString();
					String contentText = message.getContent();
					String tickerText = String.format("%s: %s", contentTitle,
							contentText);
					Intent notificationIntent = getPopupIntent(context, message);
					PendingIntent contentIntent = PendingIntent.getActivity(
							context, 0, notificationIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

					Notification notification = new Notification(icon,
							tickerText, when);
					notification.setLatestEventInfo(c, contentTitle,
							contentText, contentIntent);
					notification.flags = notification.flags
							| Notification.FLAG_AUTO_CANCEL;

					NotificationManager mNotificationManager = (NotificationManager) context
							.getSystemService(Context.NOTIFICATION_SERVICE);
					mNotificationManager.notify(SMS_NOTIFICATION_ID,
							notification);
				}
			}
		}
	}

	private Intent getPopupIntent(Context context, MessageModel message) {
		Intent i = new Intent(context, PopupActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		i.putExtra("message", message);

		return i;
	}
}
