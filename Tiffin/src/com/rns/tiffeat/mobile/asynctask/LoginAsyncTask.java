package com.rns.tiffeat.mobile.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.LoginActivity;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealFormat;

public class LoginAsyncTask extends AsyncTask<String, String, String> implements AndroidConstants {

	private ProgressDialog progressDialog;
	private Customer customerlogin;
	private CustomerOrder customerOrder;
	private String action;
	//private LoginActivity loginActivity;
	//private FragmentActivity fragmentActivity;
	private Activity context;

	public LoginAsyncTask(FragmentActivity activity, CustomerOrder customerOrder2, String string) {
		//this.fragmentActivity = activity;
		this.context = activity;
		this.customerOrder = customerOrder2;
		this.action = string;
	}

	public LoginAsyncTask(LoginActivity loginActivity, CustomerOrder customerOrder2, String string) {
		//this.loginActivity = loginActivity;
		this.context = loginActivity;
		this.customerOrder = customerOrder2;
		this.action = string;
	}

	/*public LoginAsyncTask(Context context2, CustomerOrder customerOrder2, String registrationFragment) {
		this.context = context2;
		this.customerOrder = customerOrder2;
		this.action = registrationFragment;
	}*/

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = UserUtils.showLoadingDialog(context, "Checking  Details ", "Please Wait.....");
	}

	@Override
	protected String doInBackground(String... params) {
		String result = "";
		if (!Validation.isNetworkAvailable(context)) {
			return null;
		}
		try {
			if (action.equals(LOGIN_FRAGMENT))
				result = CustomerServerUtils.customerLogin(customerOrder.getCustomer());
			else if (action.equals(LOGIN_W_GOOGLE))
				result = CustomerServerUtils.customerLoginWithGoogle(customerOrder.getCustomer());
			else if (action.equals(REGISTRATION_FRAGMENT))
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

		if (action.equals(LOGIN_FRAGMENT)) {
			if (customerlogin == null) {
				CustomerUtils.alertbox(TIFFEAT, "Login failed due to invalid username/password", context);
				return;
			}
		} else if (action.equals(LOGIN_W_GOOGLE)) {
			if (customerlogin == null) {
				CustomerUtils.alertbox(TIFFEAT, "Please Try Again Later", context);
				return;
			}
		} else if (action.equals(REGISTRATION_FRAGMENT)) {
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
		//Fragment fragment = null;
		if (customerOrder == null || customerOrder.getMealFormat() == null) {
			action = ACTION_SCHEDULED_HOME;
			//new DrawerUpdateAsynctask(context, customerlogin, null).execute("");
		} else if (MealFormat.QUICK.equals(customerOrder.getMealFormat())) {
			//fragment = new QuickOrderFragment(customerOrder);
			//CustomerUtils.nextFragment(fragment, context.getSupportFragmentManager(), false);
			action = ACTION_QUICK_ORDER;
		} else {
			//fragment = new ScheduledOrderFragment(customerOrder);
			//CustomerUtils.nextFragment(fragment, context.getSupportFragmentManager(), false);
			action = ACTION_SCHEDULED_ORDER;
		}
		
		CustomerUtils.startDrawerActivity(context, customerOrder, customerlogin, action);
	}
	
}