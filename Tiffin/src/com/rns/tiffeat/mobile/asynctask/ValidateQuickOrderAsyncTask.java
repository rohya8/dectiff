package com.rns.tiffeat.mobile.asynctask;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rns.tiffeat.mobile.PaymentGatewayFragment;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
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
			String result = CustomerServerUtils.validateQuickOrder(customerOrder);
			return result;

		} catch (Exception e) {
		}
		return null;

	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);

		if (result == null) {
			Validation.showError(previousActivity, ERROR_FETCHING_DATA);
			return;
		}
		Type typeMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		Map<String, Object> validateOrderMap = new HashMap<String, Object>();
		validateOrderMap = new Gson().fromJson(result, typeMap);
		validationResult = (String) validateOrderMap.get("result");
		String customerOrderString = (String) validateOrderMap.get("customerOrder");
		customerOrder = new Gson().fromJson(customerOrderString, CustomerOrder.class);
		if ("OK".equals(validationResult)) {
			nextActivity();
		}
		progressDialog.dismiss();
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
