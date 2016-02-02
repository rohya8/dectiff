package com.rns.tiffeat.mobile.asynctask;

import java.util.Map;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.PaymentGatewayFragment;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.google.Location;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.PaymentType;

public class ValidateQuickOrderAsyncTask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity previousActivity;
	private ProgressDialog progressDialog;
	private CustomerOrder customerOrder;
	private String validationResult;

	public ValidateQuickOrderAsyncTask(FragmentActivity activity, CustomerOrder customerOrder) {
		this.previousActivity = activity;
		this.customerOrder = customerOrder;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = UserUtils.showLoadingDialog(previousActivity, "Validating order", "Please Wait.....");
	}

	@Override
	protected String doInBackground(String... arg0) {
		if (!Validation.isNetworkAvailable(previousActivity)) {
			return null;
		}
		try {
			if (customerOrder.getLocation() == null) {
				Location location = new Location();
				location.setAddress(customerOrder.getAddress());
				customerOrder.setLocation(location);
			}
			String result = CustomerServerUtils.validateQuickOrder(customerOrder);
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
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, previousActivity);
			return;
		}
		Map<String, Object> validateOrderMap = CustomerUtils.convertToStringObjectMap(result);
		validationResult = (String) validateOrderMap.get("result");
		String customerOrderString = (String) validateOrderMap.get("customerOrder");
		customerOrder = new Gson().fromJson(customerOrderString, CustomerOrder.class);
		if ("OK".equals(validationResult)) {
			nextActivity();
		} else {
			CustomerUtils.alertbox(TIFFEAT, result, previousActivity);
			return;
		}

	}

	private void nextActivity() {

		
		if (customerOrder.getPaymentType().equals(PaymentType.NETBANKING)) {
			Fragment fragment = new PaymentGatewayFragment(customerOrder);
			CustomerUtils.nextFragment(fragment, previousActivity.getSupportFragmentManager(), false);
		}

		else if (customerOrder.getPaymentType().equals(PaymentType.CASH)) {
			new QuickOrderAsyncTask(previousActivity, customerOrder).execute("");
		}

	}

}
