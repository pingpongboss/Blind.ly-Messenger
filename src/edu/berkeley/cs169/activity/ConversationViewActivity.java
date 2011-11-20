package edu.berkeley.cs169.activity;

import edu.berkeley.cs169.BlindlyMessenger;
import edu.berkeley.cs169.util.NavigationKeyInterpreter;
import edu.berkeley.cs169.util.NavigationKeyInterpreter.NavigationKeyInterpreterResultListener;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.KeyEvent;



public class ConversationViewActivity extends ListActivity implements 
		NavigationKeyInterpreterResultListener {
	
	BlindlyMessenger app;
	private NavigationKeyInterpreter keyInterpreter;
	
	public void onCreate(Bundle savedInstanceState) {
	
	}
	
	protected void onResume() {
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyInterpreter.onKeyDown(keyCode, event)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyInterpreter.onKeyUp(keyCode, event)) {
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	public void onNavKeyInterpreterResult(ResultCode code) {
		// TODO Auto-generated method stub
		
	}

}
