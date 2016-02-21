package com.rns.tiffeat.mobile.asynctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class ScheduledOrderAsyncTask extends AsyncTask<String, String, String> implements AndroidConstants {

	private CustomerOrder scheduledOrder;
	private ProgressDialog progressDialog;
	private FragmentActivity previousActivity;
	private Customer currentCustomer;

	public ScheduledOrderAsyncTask(FragmentActivity scheduleOrderFragmentActivity, CustomerOrder customerOrder) {
		this.previousActivity = scheduleOrderFragmentActivity;
		this.currentCustomer = customerOrder.getCustomer();
		this.scheduledOrder = customerOrder;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		progressDialog = UserUtils.showLoadingDialog(previousActivity, "Daily Tiffin", "Preparing.....");
	}

	@Override
	protected String doInBackground(String... params) {

		if (!Validation.isNetworkAvailable(previousActivity)) {
			return null;
		}
		String result = "";
		try {
			result = CustomerServerUtils.scheduledOrder(scheduledOrder);
		} catch (Exception e) {
			CustomerUtils.exceptionOccurred(e.getMessage(), getClass().getSimpleName());
		}
		return result;

	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressDialog.dismiss();
		if (result == null) {
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, previousActivity);
			return;
		}

		if ("OK".equals(result)) {
			CustomerUtils.alertbox(TIFFEAT, "Order Successfull !!", previousActivity);
			nextActivity();
		} else {
			CustomerUtils.alertbox(TIFFEAT, result, previousActivity);
			return;
		}
	}

	private void nextActivity() {
		CustomerUtils.startDrawerActivity(previousActivity, scheduledOrder, currentCustomer, ACTION_SCHEDULED_HOME);
	}

}
