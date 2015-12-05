package com.rns.tiffeat.mobile.asynctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.rns.tiffeat.mobile.ScheduledUser;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.Customer;

public class AddToWalletAsyncTask extends AsyncTask<String, String, String> implements AndroidConstants {

	FragmentActivity activity;
	Customer currentCustomer;
	private ProgressDialog progressDialog;

	public AddToWalletAsyncTask(FragmentActivity activity, Customer customer) {
		this.activity = activity;
		this.currentCustomer = customer;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = UserUtils.showLoadingDialog(activity, "Scheduled order", "Preparing.....");
	}

	@Override
	protected String doInBackground(String... arg0) {

		if (!Validation.isNetworkAvailable(activity)) {
			return null;
		}
		try {
			return CustomerServerUtils.addToWallet(currentCustomer);
		} catch (Exception e) {
		}
		return null;

	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressDialog.dismiss();

		if (result == null) {
			Validation.showError(activity, ERROR_FETCHING_DATA);
			return;
		}
		else
			nextActivity();
	}

	private void nextActivity() {

		Fragment fobj = new ScheduledUser(currentCustomer, false);
		CustomerUtils.nextFragment(fobj, activity.getSupportFragmentManager(), false);
	}

}
