package com.rns.tiffeat.mobile.asynctask;

import java.util.Date;
import java.util.Map;

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
import com.rns.tiffeat.web.bo.domain.MealType;
import com.rns.tiffeat.web.util.Constants;

public class RegistrationTask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity mregistration;
	private ProgressDialog progressDialog;
	private CustomerOrder customerOrder;
	private String availableMealTypeResult;
	private Customer customer;
	private Map<MealType, Date> availableMealType;

	public RegistrationTask(FragmentActivity contxt, CustomerOrder customerOrder) {
		this.mregistration = contxt;
		this.customerOrder = customerOrder;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = UserUtils.showLoadingDialog(mregistration, "Registering User", "Please Wait....");
	}

	@Override
	protected String doInBackground(String... args) {
		String resultRegistration = "";
		if (!Validation.isNetworkAvailable(mregistration)) {
			return null;
		}
		try {
			resultRegistration = CustomerServerUtils.customerRegistration(customerOrder.getCustomer());
			customer = new Gson().fromJson(resultRegistration, Customer.class);

			CustomerUtils.storeCurrentCustomer(mregistration, customer);

			if (customerOrder.getMealFormat() == null) {
				return resultRegistration;
			}
			availableMealTypeResult = CustomerServerUtils.customerGetMealAvailable(customerOrder);
			Map<String, Object> customerorderavail = CustomerUtils.convertToStringObjectMap(availableMealTypeResult);

			String customerOrderString = (String) customerorderavail.get(Constants.MODEL_CUSTOMER_ORDER);
			customerOrder = new Gson().fromJson(customerOrderString, CustomerOrder.class);
			availableMealType = CustomerUtils.convertToMealTypeDateMap((String) customerorderavail.get(Constants.MODEL_MEAL_TYPE));

		} catch (Exception e) {
			CustomerUtils.exceptionOccurred(e.getMessage(), getClass().getSimpleName());
		}
		return resultRegistration;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressDialog.dismiss();

		if (result == null) {
			// Validation.showError(mregistration, ERROR_FETCHING_DATA);
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, mregistration);
			return;
		}
		if (customer == null) {
			CustomerUtils.alertbox(TIFFEAT, "Registration failed due to : " + result, mregistration);
			return;
		}
		if (customerOrder == null) {
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, mregistration);
			// Validation.showError(mregistration, ERROR_FETCHING_DATA);
			return;
		}

		customerOrder.setCustomer(customer);
		nextActivity();
	}

	private void nextActivity() {
		postLoginUtil();

	}

	private void postLoginUtil() {
		Fragment fragment = null;
		if (customerOrder.getMealFormat() == null) {
			new DrawerUpdateAsynctask(mregistration, customer).execute("");
		} else if (MealFormat.QUICK.equals(customerOrder.getMealFormat())) {
			fragment = new QuickOrderFragment(customerOrder, availableMealType);
			CustomerUtils.nextFragment(fragment, mregistration.getSupportFragmentManager(), false);
		} else {
			fragment = new ScheduledOrderFragment(customerOrder, availableMealType);
			CustomerUtils.nextFragment(fragment, mregistration.getSupportFragmentManager(), false);
		}
	}

}