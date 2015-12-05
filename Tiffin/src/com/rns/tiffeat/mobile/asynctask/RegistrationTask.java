package com.rns.tiffeat.mobile.asynctask;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class RegistrationTask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity mregistration;
	private ProgressDialog progressDialog;
	private CustomerOrder customerOrder;
	private String result1;
	private Customer customer;
	private Map<MealType, Date> availableMealType;

	public RegistrationTask(FragmentActivity contxt, CustomerOrder customerOrder) {
		mregistration = contxt;
		this.customerOrder = customerOrder;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		progressDialog = UserUtils.showLoadingDialog(mregistration, "Download Data", "Please Wait....");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String doInBackground(String... args) {

		if (!Validation.isNetworkAvailable(mregistration)) {
			return null;
		}
		try {
			String resultRegistration = CustomerServerUtils.customerRegistration(customerOrder.getCustomer());
			try {
				customer = new Gson().fromJson(resultRegistration, Customer.class);
				CustomerUtils.storeCurrentCustomer(mregistration, customer);

			} catch (Exception e) {
				return resultRegistration;
			}

			result1 = CustomerServerUtils.customerGetMealAvailable(customerOrder); // ResourceAccessException
			Type typeMap = new TypeToken<Map<String, Object>>() {
			}.getType();
			Map<String, Object> customerorderavail = new HashMap<String, Object>();
			customerorderavail = new Gson().fromJson(result1, typeMap);
			String customerOrderString = (String) customerorderavail.get("customerOrder");
			customerOrder = new Gson().fromJson(customerOrderString, CustomerOrder.class);
			availableMealType = (Map<MealType, Date>) customerorderavail.get("mealType");

			return resultRegistration;
		} catch (Exception e) {
		}
		return null;

	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressDialog.dismiss();

		if (result == null) {
			Validation.showError(mregistration, ERROR_FETCHING_DATA);
			return;
		}
		if (customer == null) {
			Toast.makeText(mregistration, "Registration failed due to :" + result, Toast.LENGTH_LONG).show();
			return;
		}
		customerOrder.setCustomer(customer);
		nextActivity();
	}

	private void nextActivity() {

		String customerOrderobj = new Gson().toJson(customerOrder, CustomerOrder.class);

		Fragment fragment = null;
		if (customerOrder.getMealFormat().equals(MealFormat.QUICK))
			fragment = new QuickOrderFragment(customerOrder, availableMealType);
		else if (customerOrder.getMealFormat().equals(MealFormat.SCHEDULED))
			fragment = new ScheduledOrderFragment(customerOrder, availableMealType);
		// fragment = new QuickOrderFragment(customerOrder);

		Bundle bundle = new Bundle();

		bundle.putString("MyObject", customerOrderobj);

		fragment.setArguments(bundle);

		CustomerUtils.nextFragment(fragment, mregistration.getSupportFragmentManager(), true);

	}

}