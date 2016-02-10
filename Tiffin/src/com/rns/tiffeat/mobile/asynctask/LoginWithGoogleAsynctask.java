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

public class LoginWithGoogleAsynctask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity mGoogleLogin;
	private Customer customerlogin;
	private CustomerOrder customerOrder;
	private ProgressDialog progressDialog;

	public LoginWithGoogleAsynctask(FragmentActivity loginfragment, CustomerOrder customerOrder2) {
		mGoogleLogin = loginfragment;
		customerOrder = customerOrder2;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = UserUtils.showLoadingDialog(mGoogleLogin, "Checking Details ", "Loading .....");
	}

	@Override
	protected String doInBackground(String... params) {
		String resultLogin = "";
		if (!Validation.isNetworkAvailable(mGoogleLogin)) {
			return null;
		}
		try {

			resultLogin = CustomerServerUtils.customerLoginWithGoogle(customerOrder.getCustomer());
			customerlogin = new Gson().fromJson(resultLogin, Customer.class);

		} catch (Exception e) {
			CustomerUtils.exceptionOccurred(e.getMessage(), getClass().getSimpleName());
		}
		return resultLogin;

	}

	protected void onPostExecute(String result) {
		progressDialog.dismiss();
		if (result == null) {
			Validation.showError(mGoogleLogin, ERROR_FETCHING_DATA);
			return;
		}
		if (customerlogin == null) {
			Validation.showError(mGoogleLogin, "Please Try Again Later");
			return;
		}
		CustomerUtils.storeCurrentCustomer(mGoogleLogin, customerlogin);
		if (customerOrder != null) {
			customerOrder.setCustomer(customerlogin);
		}
		nextActivity();

	};

	private void nextActivity() {
		Fragment fragment = null;
		if (customerOrder == null || customerOrder.getMealFormat() == null) {
			new DrawerUpdateAsynctask(mGoogleLogin, customerlogin, null).execute("");
		} else if (MealFormat.QUICK.equals(customerOrder.getMealFormat())) {
			fragment = new QuickOrderFragment(customerOrder);
			CustomerUtils.nextFragment(fragment, mGoogleLogin.getSupportFragmentManager(), false);
		} else {
			fragment = new ScheduledOrderFragment(customerOrder);
			CustomerUtils.nextFragment(fragment, mGoogleLogin.getSupportFragmentManager(), false);
		}
	}

}
