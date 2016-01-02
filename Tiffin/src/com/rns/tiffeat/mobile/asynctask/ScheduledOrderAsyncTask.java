package com.rns.tiffeat.mobile.asynctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.ScheduledUser;
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

	// public ScheduledOrderAsyncTask(List<CustomerOrder> customerOrders,
	// FragmentActivity context) {
	// this.scheduledOrders = customerOrders;
	// this.previousActivity = context;
	// }

	public ScheduledOrderAsyncTask(FragmentActivity scheduleOrderFragmentActivity, CustomerOrder customerOrder) {
		this.previousActivity = scheduleOrderFragmentActivity;
		this.currentCustomer = customerOrder.getCustomer();
		this.scheduledOrder = customerOrder;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		progressDialog = UserUtils.showLoadingDialog(previousActivity, "Scheduled order", "Preparing.....");
	}

	@Override
	protected String doInBackground(String... params) {

		if (!Validation.isNetworkAvailable(previousActivity)) {
			return null;
		}
		// if (scheduledOrders == null || scheduledOrders.size() == 0) {
		// return null;
		// }
		try {

			String result = new Gson().fromJson(CustomerServerUtils.scheduledOrder(scheduledOrder), String.class);
			if ("OK".equals(result)) {
				currentCustomer = CustomerServerUtils.getCurrentCustomer(scheduledOrder.getCustomer());
				CustomerUtils.storeCurrentCustomer(previousActivity, currentCustomer);
				return result;
			}
			return result;

		} catch (Exception e) {
			CustomerUtils.exceptionOccurred(e.getMessage(), getClass().getSimpleName());
			return null;
		}

	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressDialog.dismiss();
		if (result == null) {
			Validation.showError(previousActivity, ERROR_FETCHING_DATA);
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
		CustomerUtils.clearFragmentStack(previousActivity.getSupportFragmentManager());
		Fragment fragment = new ScheduledUser(currentCustomer);
		CustomerUtils.nextFragment(fragment, previousActivity.getSupportFragmentManager(), false);
	}

}
