package com.rns.tiffeat.mobile.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.LoginActivity;
import com.rns.tiffeat.mobile.Verification;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealFormat;

public class LoginAsyncTask extends AsyncTask<String, String, String> implements AndroidConstants {

	private ProgressDialog progressDialog;
	private Customer customerlogin;
	private CustomerOrder customerOrder;
	private String action;
	private Activity context;
	private TelephonyManager tm;

	public LoginAsyncTask(FragmentActivity activity, CustomerOrder customerOrder2, String string) {
		// this.fragmentActivity = activity;
		this.context = activity;
		this.customerOrder = customerOrder2;
		this.action = string;
	}

	public LoginAsyncTask(LoginActivity loginActivity, CustomerOrder customerOrder2, String string) {
		// this.loginActivity = loginActivity;
		this.context = loginActivity;
		this.customerOrder = customerOrder2;
		this.action = string;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		progressDialog = UserUtils.showLoadingDialog(context, "Checking  Details", "Please Wait...");
	}

	@Override
	protected String doInBackground(String... params) {
		String result = null;
		customerOrder.getCustomer().setDeviceId(CustomerUtils.imeino(tm));
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
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, context);
			return;
		}

		if (action.equals("LOGINFRAGMENT")) {
			if (customerlogin == null) {
				CustomerUtils.alertbox(TIFFEAT, "Login failed due to invalid username/password", context);
				return;
			}
		} else if (action.equals("LOGINGOOGLE")) {
			if (customerlogin == null) {
				CustomerUtils.alertbox(TIFFEAT, "Please Try Again Later", context);
				return;
			}
		} else if (action.equals("REGISTRATIONFRAGMENT")) {
			if (customerlogin == null) {
				CustomerUtils.alertbox(TIFFEAT, "Registration failed due to : " + result, context);
				return;
			}
		}

		CustomerUtils.storeCurrentCustomer(context, customerlogin);
		if (customerOrder != null) {
			customerOrder.setCustomer(customerlogin);
		}
		postLogin();
	}

	private void postLogin() {
		// Fragment fragment = null;
		if (customerOrder == null || customerOrder.getMealFormat() == null) {
			action = ACTION_SCHEDULED_HOME;
		} else if (MealFormat.QUICK.equals(customerOrder.getMealFormat())) {
			// fragment = new QuickOrderFragment(customerOrder);
			// CustomerUtils.nextFragment(fragment,
			// context.getSupportFragmentManager(), false);
			action = ACTION_QUICK_ORDER;
		} else {
			// fragment = new ScheduledOrderFragment(customerOrder);
			// CustomerUtils.nextFragment(fragment,
			// context.getSupportFragmentManager(), false);
			action = ACTION_SCHEDULED_ORDER;
		}

		CustomerUtils.startDrawerActivity(context, customerOrder, customerlogin, action);
	}

}