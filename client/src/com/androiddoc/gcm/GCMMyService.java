package com.androiddoc.gcm;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class GCMMyService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onStart(Intent intent, int startId) {
		Bundle extras = intent.getExtras(); 
		String message;

		if (extras != null) {
			message = extras.getString("message");
			Toast.makeText(this,"From Service: " +  message, Toast.LENGTH_LONG).show();
		}
		stopSelf();
	}
}
