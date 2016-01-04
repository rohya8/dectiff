package com.rns.tiffeat.mobile.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.DrawerActivity;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class DrawerUpdateAsynctask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity activity;
	private ProgressDialog pd;
	private Customer customer;

	public DrawerUpdateAsynctask(FragmentActivity fragmentActivity, Customer customerlogin) {
		this.activity = fragmentActivity;
		this.customer = customerlogin;
	}

	@Override
	protected String doInBackground(String... arg) {
		if (!Validation.isNetworkAvailable(activity)) {
			return null;
		} else {
			try {

			} catch (Exception e) {
				CustomerUtils.exceptionOccurred(e.getMessage(), getClass().getSimpleName());
			}
			return "Hello";
		}
	}

	protected void onPostExecute(String result) {
		if (result == null) {
			Validation.showError(activity, ERROR_FETCHING_DATA);
			return;
		}
		Intent i = new Intent(activity, DrawerActivity.class);
		i.putExtra(AndroidConstants.CUSTOMER_OBJECT, new Gson().toJson(customer));
		activity.startActivity(i);
		activity.finish();
	};

}
