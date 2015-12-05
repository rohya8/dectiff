package com.rns.tiffeat.mobile.asynctask;

import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

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

	private List<CustomerOrder> scheduledOrders;
	private ProgressDialog progressDialog;
	private FragmentActivity previousActivity;
	private Customer currentCustomer;

	public ScheduledOrderAsyncTask(List<CustomerOrder> customerOrders, FragmentActivity context) {
		this.scheduledOrders = customerOrders;
		this.previousActivity = context;
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
		try {
			if (scheduledOrders == null || scheduledOrders.size() == 0) {
				return null;
			}
			String result = new Gson().fromJson(CustomerServerUtils.scheduledOrder(scheduledOrders), String.class);
			if ("OK".equals(result)) {
				currentCustomer = CustomerServerUtils.getCurrentCustomer(scheduledOrders.get(0).getCustomer());
				CustomerUtils.storeCurrentCustomer(previousActivity, currentCustomer);
			}
			return result;

		} catch (Exception e) {
		}
		return null;

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
			nextActivity();
		}
		Toast.makeText(previousActivity, result, Toast.LENGTH_LONG).show();

	}

	private void nextActivity() {
		Fragment fragment = new ScheduledUser(currentCustomer, true);
		CustomerUtils.nextFragment(fragment, previousActivity.getSupportFragmentManager(), false);
	}

}
