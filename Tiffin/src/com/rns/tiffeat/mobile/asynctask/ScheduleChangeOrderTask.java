package com.rns.tiffeat.mobile.asynctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.FirstTimeUse;
import com.rns.tiffeat.mobile.ScheduledUser;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.Meal;

public class ScheduleChangeOrderTask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity mchangeorder;
	private ProgressDialog progressDialog;
	private CustomerOrder customerOrder;
	private String result1;
	private Meal objmMeal, scheduleMeal;

	public Meal getScheduleMeal() {
		return scheduleMeal;
	}

	public void setScheduleMeal(Meal scheduleMeal) {
		this.scheduleMeal = scheduleMeal;
	}

	public ScheduleChangeOrderTask(FragmentActivity contxt, CustomerOrder customerOrder, Meal objmeal) {
		mchangeorder = contxt;
		this.customerOrder = customerOrder;
		this.objmMeal = objmeal;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = UserUtils.showLoadingDialog(mchangeorder, "Download Data", "Please Wait.....");
	}

	@Override
	protected String doInBackground(String... args) {

		if (!Validation.isNetworkAvailable(mchangeorder)) {
			return null;
		}
		try {
			this.setScheduleMeal(customerOrder.getMeal());
			customerOrder.setMeal(objmMeal);
			result1 = CustomerServerUtils.changeOrder(customerOrder);

			return result1;

		} catch (Exception e) {
		}
		return null;

	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressDialog.dismiss();

		if (result == null) {
			Validation.showError(mchangeorder, ERROR_FETCHING_DATA);
			return;
		}

		String result1 = new Gson().fromJson(result, String.class);

		if (!result1.equals("OK")) {
			Toast.makeText(mchangeorder, "You can't order this meal!!", Toast.LENGTH_LONG).show();
			homeActivity();
		} else if (result1.equals("OK")) {
			Toast.makeText(mchangeorder, "Change Order Successful !! ", Toast.LENGTH_LONG).show();
			nextActivity();
		}

	}

	private void homeActivity() {

		Fragment fragment = null;
		fragment = new FirstTimeUse(customerOrder);

		CustomerUtils.nextFragment(fragment, mchangeorder.getSupportFragmentManager(), false);
	}

	private void nextActivity() {

		Customer customer = CustomerUtils.getCurrentCustomer(mchangeorder);
		Fragment fragment = null;
		fragment = new ScheduledUser(customer, true);

		CustomerUtils.nextFragment(fragment, mchangeorder.getSupportFragmentManager(), false);

	}

}