package com.rns.tiffeat.mobile.asynctask;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

public class LoginAsyncTask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity mlogin;
	private ProgressDialog progressDialog;
	private Customer customerlogin;
	private CustomerOrder customerOrder;
	private String result1;
	private Map<MealType, Date> availableMealType;

	public LoginAsyncTask(FragmentActivity activity, CustomerOrder customerOrder2) {
		mlogin = activity;
		customerOrder = customerOrder2;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = UserUtils.showLoadingDialog(mlogin, "Checking  Details ", "Preparing.....");
	}

	@Override
	protected String doInBackground(String... params) {
		String resultLogin = "";
		if (!Validation.isNetworkAvailable(mlogin)) {
			return null;
		}
		try {
			resultLogin = CustomerServerUtils.customerLogin(customerOrder.getCustomer());

			customerlogin = new Gson().fromJson(resultLogin, Customer.class);
			/*
			 * if
			 * (customerlogin.getEmail().toString().equals(customerOrder.getCustomer
			 * ().getEmail().toString())) { if
			 * (customerlogin.getPassword().toString
			 * ().equals(customerOrder.getCustomer().getPassword().toString()))
			 * { customerOrder.setCustomer(customerlogin);
			 * CustomerUtils.storeCurrentCustomer(mlogin, customerlogin); } else
			 * Toast.makeText(mlogin, "Please Check Your Password ",
			 * Toast.LENGTH_SHORT).show(); } else Toast.makeText(mlogin,
			 * "Please Check Your Email ", Toast.LENGTH_SHORT).show();
			 */
			customerOrder.setCustomer(customerlogin);
			CustomerUtils.storeCurrentCustomer(mlogin, customerlogin);
			result1 = CustomerServerUtils.customerGetMealAvailable(customerOrder);

			Type typeMap = new TypeToken<Map<String, Object>>() {
			}.getType();

			Map<String, Object> customerorderavail = new HashMap<String, Object>();

			customerorderavail = new Gson().fromJson(result1, typeMap);

			String customerOrderString = (String) customerorderavail.get("customerOrder");
			availableMealType = (Map<MealType, Date>) customerorderavail.get("mealType");
			customerOrder = new Gson().fromJson(customerOrderString, CustomerOrder.class);

		} catch (Exception e) {
			
		}
		return resultLogin;

	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressDialog.dismiss();
		if (result == null) {
			Validation.showError(mlogin, ERROR_FETCHING_DATA);
			return;
		}
		if (customerlogin == null) {
			Toast.makeText(mlogin, "Login failed due to :" + result, Toast.LENGTH_LONG).show();
			return;
		}
		customerOrder.setCustomer(customerlogin);
		nextActivity();
	}

	private void nextActivity() {

		Fragment fragment = null;
		if (customerOrder.getMealFormat().equals(MealFormat.QUICK))
			fragment = new QuickOrderFragment(customerOrder, availableMealType);
		else if (customerOrder.getMealFormat().equals(MealFormat.SCHEDULED))
			fragment = new ScheduledOrderFragment(customerOrder, availableMealType);

		CustomerUtils.nextFragment(fragment, mlogin.getSupportFragmentManager(), false);

	}

}
