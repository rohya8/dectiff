package com.rns.tiffeat.mobile.asynctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
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

public class LoginAsyncTask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity fragmentActivity;
	private ProgressDialog progressDialog;
	private Customer customerlogin;
	private CustomerOrder customerOrder;
	private String action;

	public LoginAsyncTask(FragmentActivity activity, CustomerOrder customerOrder2, String string) {
		this.fragmentActivity = activity;
		this.customerOrder = customerOrder2;
		this.action = string;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = UserUtils.showLoadingDialog(fragmentActivity, "Checking  Details ", "Please Wait.....");
	}

	@Override
	protected String doInBackground(String... params) {
		String result = "";
		if (!Validation.isNetworkAvailable(fragmentActivity)) {
			return null;
		}
		try {
			if (action.equals("LOGINFRAGMENT"))
				result = CustomerServerUtils.customerLogin(customerOrder.getCustomer());
			else if (action.equals("LOGINGOOGLE"))
				result = CustomerServerUtils.customerLoginWithGoogle(customerOrder.getCustomer());
			else if (action.equals("REGISTRATIONFRAGMENT"))
				result = CustomerServerUtils.customerRegistration(customerOrder.getCustomer());

			customerlogin = new Gson().fromJson(result, Customer.class);
			return result;
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
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, fragmentActivity);
			return;
		}

		if (action.equals("LOGINFRAGMENT")) {
			if (customerlogin == null) {
				CustomerUtils.alertbox(TIFFEAT, "Login failed due to invalid username/password", fragmentActivity);
				return;
			}
		} else if (action.equals("LOGINGOOGLE")) {
			if (customerlogin == null) {
				CustomerUtils.alertbox(TIFFEAT, "Please Try Again Later", fragmentActivity);
				return;
			}
		} else if (action.equals("REGISTRATIONFRAGMENT")) {
			if (customerlogin == null) {
				CustomerUtils.alertbox(TIFFEAT, "Registration failed due to : " + result, fragmentActivity);
				return;
			}
		}

		CustomerUtils.storeCurrentCustomer(fragmentActivity, customerlogin);
		if (customerOrder != null) {
			customerOrder.setCustomer(customerlogin);
		}
		postLogin();
	}

	private void postLogin() {
		Fragment fragment = null;
		if (customerOrder == null || customerOrder.getMealFormat() == null) {
			new DrawerUpdateAsynctask(fragmentActivity, customerlogin, null).execute("");
		} else if (MealFormat.QUICK.equals(customerOrder.getMealFormat())) {
			fragment = new QuickOrderFragment(customerOrder);
			CustomerUtils.nextFragment(fragment, fragmentActivity.getSupportFragmentManager(), false);
		} else {
			fragment = new ScheduledOrderFragment(customerOrder);
			CustomerUtils.nextFragment(fragment, fragmentActivity.getSupportFragmentManager(), false);
		}
	}
}