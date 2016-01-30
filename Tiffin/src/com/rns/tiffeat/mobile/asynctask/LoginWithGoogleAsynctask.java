package com.rns.tiffeat.mobile.asynctask;

import java.util.Date;
import java.util.Map;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

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
import com.rns.tiffeat.web.bo.domain.MealType;
import com.rns.tiffeat.web.util.Constants;

public class LoginWithGoogleAsynctask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity mGoogleLogin;
	private String action;
	private Customer customerlogin;
	private CustomerOrder customerOrder;
	private String availableMealTypeResult;
	private ProgressDialog progressDialog;
	private Map<MealType, Date> availableMealType;

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
			customerOrder.setCustomer(customerlogin);
			CustomerUtils.storeCurrentCustomer(mGoogleLogin, customerlogin);
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
		if (customerOrder == null) {
			Validation.showError(mGoogleLogin, ERROR_FETCHING_DATA);
			return;
		}
		customerOrder.setCustomer(customerlogin);
		nextActivity();

	};

	private void nextActivity() {

		Fragment fragment = null;

		if (customerOrder.getMealFormat() == null) {
			new DrawerUpdateAsynctask(mGoogleLogin, customerlogin).execute("");
		} else if (MealFormat.QUICK.equals(customerOrder.getMealFormat())) {
			fragment = new QuickOrderFragment(customerOrder, availableMealType);
			CustomerUtils.nextFragment(fragment, mGoogleLogin.getSupportFragmentManager(), false);
		} else {
			fragment = new ScheduledOrderFragment(customerOrder, availableMealType);
			CustomerUtils.nextFragment(fragment, mGoogleLogin.getSupportFragmentManager(), false);
		}

	}

}
