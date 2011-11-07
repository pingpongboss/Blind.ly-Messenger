package edu.berkeley.cs169;



import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import edu.berkeley.cs169.utils.BMSendingSMS;
import edu.berkeley.cs169.utils.BMUtils;

public class ComposeActivity extends Activity {
	// {{ Copy and edit these fields and methods for each Activity
	
		Button sendButton; // Send Button

		// Edit for each Activity
		public static final String TAG = "ComposeActivity";
		// Same for all Activities
		public boolean upPressed = false;
		public boolean downPressed = false;

		// Same for all Activities
		@Override
		protected void onResume() {
			super.onResume();

			vibrateShortCode();
		}

		// Edit for each Activity
		protected void vibrateShortCode() {
			String alert = getResources().getString(R.string.compose_shortcode);

			BMUtils.textToVibration(alert, this);
		}

		// Edit for each Activity
		protected void vibrateHelp() {
			String alert = getResources().getString(R.string.compose_help);

			BMUtils.textToVibration(alert, this);
		}

		// Edit for each Activity
		protected void startUpAction() {
			Log.d(TAG, "Clicked UP");
			
			BMUtils.textToVibration("e", this);
		}

		// Edit for each Activity
		protected void startDownAction() {
			Log.d(TAG, "Clicked DOWN");
			startActivity(new Intent(this, MessageInputActivity.class));
		}

		// Same for all Activities
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_UP:
				upPressed = true;
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				downPressed = true;
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}

		// Same for all Activities
		@Override
		public boolean onKeyUp(int keyCode, KeyEvent event) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_UP:
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				if (upPressed && downPressed) {
					vibrateHelp();
				} else if (upPressed) {
					startUpAction();
				} else if (downPressed) {
					startDownAction();
				}
				upPressed = false;
				downPressed = false;
				return true;
			}
			return super.onKeyUp(keyCode, event);
		}

		// }}
		
//		private MessageModel mMessage;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.compose);
			
			sendButton = (Button) findViewById(R.id.sendButton);

		
		
		//Sending SMS
		 sendButton.setOnClickListener(new View.OnClickListener() 
	        {
	            public void onClick(View v) 
	            {     
	            	//Needs to take phone number and a message.
	            	//Need to get this off Edmond and make changes
	            	/*
	                String phoneNo = txtPhoneNo.getText().toString();
	                String message = txtMessage.getText().toString();  
	                */
	            	
	            	String phoneNo = "5554";  
	            	String message = "Hi 169 team";
	            	sendSMS(phoneNo,message);
	            }
	        });
		}
		 private void sendSMS(String phoneNumber, String message)
		    {        
		        PendingIntent pi = PendingIntent.getActivity(this, 0,
		            new Intent(this, ComposeActivity.class), 0);                
		       
		       // BMSendingSMS.sendSMSHelper(phoneNumber,message, pi);        
		        SmsManager sms = SmsManager.getDefault();
		        sms.sendTextMessage(phoneNumber, null, message, pi, null);
		    }   
		
		
}
