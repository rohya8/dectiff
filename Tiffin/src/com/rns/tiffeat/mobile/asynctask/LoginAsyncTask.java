package com.rns.tiffeat.mobile.asynctask;

import java.util.Date;
import java.util.Map;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.FirstTimeUse;
import com.rns.tiffeat.mobile.QuickOrderFragment;
import com.rns.tiffeat.mobile.ScheduledOrderFragment;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealFormat;
import com.rns.tiffeat.web.bo.domain.MealType;
import com.rns.tiffeat.web.util.Constants;

public class LoginAsyncTask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity fragmentActivity;
	private ProgressDialog progressDialog;
	private Customer customerlogin;
	private CustomerOrder customerOrder;
	private String availableMealTypeResult;
	private Map<MealType, Date> availableMealType;

	public LoginAsyncTask(FragmentActivity activity, CustomerOrder customerOrder2) {
		this.fragmentActivity = activity;
		this.customerOrder = customerOrder2;

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = UserUtils.showLoadingDialog(fragmentActivity, "Checking  Details ", "Please Wait.....");
	}

	@Override
	protected String doInBackground(String... params) {
		String resultLogin = "";
		if (!Validation.isNetworkAvailable(fragmentActivity)) {
			return null;
		}
		try {
			resultLogin = CustomerServerUtils.customerLogin(customerOrder.getCustomer());
			customerlogin = new Gson().fromJson(resultLogin, Customer.class);

			CustomerUtils.storeCurrentCustomer(fragmentActivity, customerlogin);
			if (customerOrder.getMealFormat() == null) {
				return resultLogin;
			}
			customerOrder.setCustomer(customerlogin);

			availableMealTypeResult = CustomerServerUtils.customerGetMealAvailable(customerOrder);
			Map<String, Object> customerOrderVailableMealTypesMap = CustomerUtils.convertToStringObjectMap(availableMealTypeResult);
			String customerOrderString = (String) customerOrderVailableMealTypesMap.get(Constants.MODEL_CUSTOMER_ORDER);
			availableMealType = CustomerUtils.convertToMealTypeDateMap((String) customerOrderVailableMealTypesMap.get(Constants.MODEL_MEAL_TYPE));

			customerOrder = new Gson().fromJson(customerOrderString, CustomerOrder.class);

		} catch (Exception e) {
			CustomerUtils.exceptionOccurred(e.getMessage(), getClass().getSimpleName());
		}
		return resultLogin;

	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressDialog.dismiss();
		if (result == null) {
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, fragmentActivity);
			// Validation.showError(fragmentActivity, ERROR_FETCHING_DATA);
			return;
		}

		if (customerlogin == null) {
			CustomerUtils.alertbox(TIFFEAT, "Login failed due to : " + result, fragmentActivity);
			customerOrder.setCustomer(null);
			return;
		}
		if (customerOrder == null) {
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, fragmentActivity);
			// Validation.showError(fragmentActivity, ERROR_FETCHING_DATA);
			return;
		}
		customerOrder.setCustomer(customerlogin);
		nextActivity();
	}

	private void nextActivity() {

		postLoginUtil();
	}

	private void postLoginUtil() {
		Fragment fragment = null;
		if (customerOrder.getMealFormat() == null) {
			new DrawerUpdateAsynctask(fragmentActivity, customerlogin).execute("");
		} else if (MealFormat.QUICK.equals(customerOrder.getMealFormat())) {
			fragment = new QuickOrderFragment(customerOrder, availableMealType);
			CustomerUtils.nextFragment(fragment, fragmentActivity.getSupportFragmentManager(), false);
		} else {
			fragment = new ScheduledOrderFragment(customerOrder, availableMealType);
			CustomerUtils.nextFragment(fragment, fragmentActivity.getSupportFragmentManager(), false);
		}
	}

}
