package com.rns.tiffeat.mobile.asynctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.ScheduledOrderHomeScreen;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class ScheduleCancelOrderTask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity mscheduleorder;
	private ProgressDialog progressDialog;
	private CustomerOrder customerOrder;
	private String result1;
	private ScheduledOrderHomeScreen scheduledUser;

	public ScheduleCancelOrderTask(FragmentActivity scheduledOrderFragment, CustomerOrder order) {
		this.customerOrder = order;
		this.mscheduleorder = scheduledOrderFragment;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = UserUtils.showLoadingDialog(mscheduleorder, "Cancel Order ", "Canceling Order....");
	}

	@Override
	protected String doInBackground(String... args) {

		if (!Validation.isNetworkAvailable(mscheduleorder)) {
			return null;
		}
		try {
			result1 = CustomerServerUtils.cancelScheduleOrder(customerOrder);
			return result1;
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
			//Validation.showError(mscheduleorder, ERROR_FETCHING_DATA);
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, mscheduleorder);
			return;
		}
		String result1 = new Gson().fromJson(result, String.class);

		if (result1.equals("OK")) {
			CustomerUtils.alertbox(TIFFEAT, "Cancel Order Successful !! ", mscheduleorder);
		new GetCurrentCustomerAsyncTask(mscheduleorder, scheduledUser).execute("");
		} else
			CustomerUtils.alertbox(TIFFEAT, "Cancel failed due to : " + result, mscheduleorder);
		
	}

	public ScheduledOrderHomeScreen getScheduledUser() {
		return scheduledUser;
	}

	public void setScheduledUser(ScheduledOrderHomeScreen scheduledUser) {
		this.scheduledUser = scheduledUser;
	}

}