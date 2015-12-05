package com.rns.tiffeat.mobile.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.rns.tiffeat.mobile.DrawerActivity;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CoreServerUtils;

public class GetAreaAsynctask extends AsyncTask<String, String, String> implements AndroidConstants {

	Activity mSplashScreen;
	String action;
	ProgressDialog pd;

	public GetAreaAsynctask(Activity splashScreen) {
		mSplashScreen = splashScreen;
	}

	@Override
	protected String doInBackground(String... arg) {
		if (!Validation.isNetworkAvailable(mSplashScreen)) {
			return null;
		} else {
			try {
				CoreServerUtils.retrieveVendorAreaNames();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "Hello";
		}
	}

	protected void onPostExecute(String result) {
		if (result == null) {
			Validation.showError(mSplashScreen, ERROR_FETCHING_DATA);
			return;
		}
		Intent i = new Intent(mSplashScreen, DrawerActivity.class);
		mSplashScreen.startActivity(i);
		mSplashScreen.finish();
	};

}
