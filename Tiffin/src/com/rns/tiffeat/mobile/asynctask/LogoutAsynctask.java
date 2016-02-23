package com.rns.tiffeat.mobile.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.rns.tiffeat.mobile.DrawerActivity;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;

public class LogoutAsynctask extends AsyncTask<String, String, String> implements AndroidConstants {

	Activity mSplashScreen;
	String action;
	ProgressDialog pd;

	public LogoutAsynctask(Activity splashScreen) {
		mSplashScreen = splashScreen;
	}

	@Override
	protected String doInBackground(String... arg) {
		return "Hello";
	}

	protected void onPostExecute(String result) {
		if (result == null) {
			// Validation.showError(mSplashScreen, ERROR_FETCHING_DATA);
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, mSplashScreen);
			return;
		}
		Intent i = new Intent(mSplashScreen, DrawerActivity.class);
		mSplashScreen.startActivity(i);
		mSplashScreen.finish();
		CustomerUtils.alertbox(TIFFEAT, "Logged Out!", mSplashScreen);
	};

}
