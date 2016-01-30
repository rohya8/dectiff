package com.rns.tiffeat.mobile.asynctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.WalletFragment;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class ValidateScheduledOrderAsyncTask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity scheduleOrderFragmentActivity;
	private ProgressDialog progressDialog;
	private CustomerOrder customerOrder;

	public ValidateScheduledOrderAsyncTask(FragmentActivity activity, CustomerOrder customerOrder) {
		this.scheduleOrderFragmentActivity = activity;
		this.customerOrder = customerOrder;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = UserUtils.showLoadingDialog(scheduleOrderFragmentActivity, "Validating order", "Please Wait.....");
	}

	@Override
	protected String doInBackground(String... arg0) {
		progressDialog.dismiss();
		if (!Validation.isNetworkAvailable(scheduleOrderFragmentActivity)) {
			return null;
		}
		try {
			String result = CustomerServerUtils.validateScheduledOrder(customerOrder);
			return result;

		} catch (Exception e) {
			Log.d(MYTAG, "Log occurred in " + getClass().getName() + " Exception : " + e);
		}
		return null;

	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressDialog.dismiss();

		if (result == null) {
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, scheduleOrderFragmentActivity);
			return;
		}

		if ("OK".equals(result)) {
			nextActivity();
		} else {
			CustomerUtils.alertbox(TIFFEAT, result, scheduleOrderFragmentActivity);
			return;
		}

	}

	private void nextActivity() {
		Fragment fobj = new WalletFragment(customerOrder);
		CustomerUtils.nextFragment(fobj, scheduleOrderFragmentActivity.getSupportFragmentManager(), false);
	}

}
