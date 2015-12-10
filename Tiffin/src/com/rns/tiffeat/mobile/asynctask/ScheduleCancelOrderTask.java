package com.rns.tiffeat.mobile.asynctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class ScheduleCancelOrderTask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity mscheduleorder;
	private ProgressDialog progressDialog;
	private CustomerOrder customerOrder;
	private String result1;

	public ScheduleCancelOrderTask(FragmentActivity scheduledOrderFragment, CustomerOrder order) {
		this.customerOrder = order;
		this.mscheduleorder = scheduledOrderFragment;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = UserUtils.showLoadingDialog(mscheduleorder, "Download Data", "Please Wait....");
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
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressDialog.dismiss();
		if (result == null) {
			Validation.showError(mscheduleorder, ERROR_FETCHING_DATA);
			return;
		}
		String result1 = new Gson().fromJson(result, String.class);

		if (result1.equals("OK")) {
			Toast.makeText(mscheduleorder, "Cancel Order Successful !! ", Toast.LENGTH_LONG).show();
		} else
			Toast.makeText(mscheduleorder, "Cancel failed due to :" + result, Toast.LENGTH_LONG).show();
	}

}