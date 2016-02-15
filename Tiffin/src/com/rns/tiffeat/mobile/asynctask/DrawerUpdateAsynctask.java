package com.rns.tiffeat.mobile.asynctask;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.DrawerActivity;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.Customer;

public class DrawerUpdateAsynctask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity activity;
	private Customer customer;
	private String action = null;

	public DrawerUpdateAsynctask(FragmentActivity fragmentActivity, Customer customerlogin, String string) {
		this.activity = fragmentActivity;
		this.customer = customerlogin;
		this.action = string;
	}

	@Override
	protected String doInBackground(String... arg) {
		if (!Validation.isNetworkAvailable(activity)) {
			return null;
		}

		return "Hello";
	}

	protected void onPostExecute(String result) {
		if (result == null) {
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, activity);
			return;
		}

		Intent i = new Intent(activity, DrawerActivity.class);
		i.putExtra(AndroidConstants.CUSTOMER_OBJECT, new Gson().toJson(customer));
		i.putExtra(ACTION, action);
		activity.startActivity(i);
		activity.finish();
	};

}
