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
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class AddToWalletAsyncTask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity activity;
	
	private ProgressDialog progressDialog;
	private CustomerOrder customerOrder;

	public AddToWalletAsyncTask(FragmentActivity activity, CustomerOrder customerOrder) {
		this.activity = activity;
		this.customerOrder = customerOrder;
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
			return CustomerServerUtils.addToWallet(customerOrder.getCustomer());
		} catch (Exception e) {
			CustomerUtils.exceptionOccurred(e.getMessage(), getClass().getSimpleName());
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
		} else
			nextActivity();
	}

	private void nextActivity() {

		CustomerUtils.clearFragmentStack(activity.getSupportFragmentManager());
		if (customerOrder != null) {
			new ScheduledOrderAsyncTask(activity, customerOrder).execute();
		} else {
			Fragment scheduledHomeFragment = new ScheduledUser(customerOrder.getCustomer());
			CustomerUtils.nextFragment(scheduledHomeFragment, activity.getSupportFragmentManager(), false);
		}
	}
}
