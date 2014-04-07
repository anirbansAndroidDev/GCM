package com.androiddoc.gcm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.androiddoc.gcm.R;
import com.google.android.gcm.GCMRegistrar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class GCMMainActivity extends Activity {
	private GCMReceiver mGCMReceiver;
	private IntentFilter mOnRegisteredFilter;


	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Register the device to GCM
		getRegisteredIdFromGcm();
	}

	//===================================================================================================
	//Requesting to register the device to GCM
	//===================================================================================================

	private void getRegisteredIdFromGcm() 
	{
		mGCMReceiver = new GCMReceiver();
		mOnRegisteredFilter = new IntentFilter();
		mOnRegisteredFilter.addAction(GCMConstants.ACTION_ON_REGISTERED);


		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (!regId.equals("")) 
		{
			new MyAsyncTaskForGcmRegistration().execute(regId);
		} 
		else
		{
			GCMRegistrar.register(this, GCMConstants.SENDER_ID);
		}
	}

	//===================================================================================================
	//END Requesting to register the device to GCM
	//===================================================================================================

	
	//===================================================================================================
	//On Gcm response received
	//===================================================================================================
	
	private class GCMReceiver extends BroadcastReceiver 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			String regId = intent.getStringExtra(GCMConstants.FIELD_REGISTRATION_ID);
			Toast.makeText(GCMMainActivity.this, "Device registered to GCM.", Toast.LENGTH_LONG).show();

			//send registered id to our server
			new MyAsyncTaskForGcmRegistration().execute(regId);
		}
	}
	
	//===================================================================================================
	//END On Gcm response received
	//===================================================================================================


	//===================================================================================================================================
	//Sending userName, Password to server and checking login successful or not
	//===================================================================================================================================
	private class MyAsyncTaskForGcmRegistration extends AsyncTask<String, Integer, Double>{

		String responseBody;
		int responseCode;
		@Override
		protected Double doInBackground(String... params) {
			postData(params[0]);
			return null;
		}

		protected void onPostExecute(Double result)
		{
			//The HTTP status messages in the 200 series reflect that the request was successful.
			if(responseCode == 200)
			{
				Log.d("Log",responseBody);
				processLoginResponce(responseBody);
			}
			//Not getting proper response
			else
			{
				Toast.makeText(GCMMainActivity.this, "Sorry network problem", Toast.LENGTH_LONG).show();
			}

		}

		protected void onProgressUpdate(Integer... progress){

		}

		public void postData(String mRegId) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(GCMConstants.SERVER_URL);

			try {
				// Data that I am sending
				List nameValuePairs = new ArrayList();
				nameValuePairs.add(new BasicNameValuePair("regId", mRegId));
				nameValuePairs.add(new BasicNameValuePair("name", "Anirban"));
				nameValuePairs.add(new BasicNameValuePair("email", "anirban.jana@gmail.com"));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);

				responseCode = response.getStatusLine().getStatusCode();
				responseBody = EntityUtils.toString(response.getEntity());
			} 
			catch (Throwable t ) {
				Log.d("Error Time of Login",t+"");
			} 
		}
	}
	//===================================================================================================================================
	//END Sending userName, Password to server and checking login successful or not
	//===================================================================================================================================




	//===================================================================================================================================
	//processing the XML got from server 
	//===================================================================================================================================
	private void processLoginResponce(String responceFromServer) 
	{
		Log.w("Log",responceFromServer);
		if(responceFromServer.equalsIgnoreCase("Y"))
		{
			Toast.makeText(GCMMainActivity.this, "Device registered to server.", Toast.LENGTH_LONG).show();
		}
		else if(responceFromServer.equalsIgnoreCase("exist"))
		{
			Toast.makeText(GCMMainActivity.this, "Device already registered to server.", Toast.LENGTH_LONG).show();
		}
		else if(responceFromServer.equalsIgnoreCase("N"))
		{
			Toast.makeText(GCMMainActivity.this, "Device registration faild.", Toast.LENGTH_LONG).show();
		}
	}
	//===================================================================================================================================
	//processing the XML 
	//===================================================================================================================================


	@Override
	public void onResume() 
	{
		super.onResume();
		registerReceiver(mGCMReceiver, mOnRegisteredFilter);
	}

	@Override
	public void onPause() 
	{
		super.onPause();
		unregisterReceiver(mGCMReceiver);
	}
}
