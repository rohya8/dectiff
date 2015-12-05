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
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealFormat;
import com.rns.tiffeat.web.bo.domain.MealType;

public class ExistingUserAsyncTask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity mexistinguser;
	private ProgressDialog progressdialog;
	private CustomerOrder customerOrder;
	private String result1;
	private Map<MealType, Date> availableMealType;

	public ExistingUserAsyncTask(FragmentActivity contxt, CustomerOrder customerOrder) {
		mexistinguser = contxt;
		this.customerOrder = customerOrder;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressdialog = UserUtils.showLoadingDialog(mexistinguser, "Download Data ", "Preparing.....");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String doInBackground(String... args) {
		if (!Validation.isNetworkAvailable(mexistinguser)) {
			return null;
		}
		try {
			result1 = CustomerServerUtils.customerGetMealAvailable(customerOrder);
			Type typeMap = new TypeToken<Map<String, Object>>() {
			}.getType();

			Map<String, Object> customerorderavail = new HashMap<String, Object>();

			customerorderavail = new Gson().fromJson(result1, typeMap);

			String customerOrderString = (String) customerorderavail.get("customerOrder");
			availableMealType = (Map<MealType, Date>) customerorderavail.get("mealType");

			customerOrder = new Gson().fromJson(customerOrderString, CustomerOrder.class);

			return result1;
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressdialog.dismiss();
		if (result == null) {
			Validation.showError(mexistinguser, ERROR_FETCHING_DATA);
			return;
		}
		if (customerOrder != null) {
			nextActivity();
		} else {
			Toast.makeText(mexistinguser, "Something Went Wrong !!!", Toast.LENGTH_SHORT).show();
		}

	}

	private void nextActivity() {

		Fragment fragment = null;

		if (customerOrder.getMealFormat().toString().equals(MealFormat.QUICK.toString()))
			fragment = new QuickOrderFragment(customerOrder, availableMealType);
		else if (customerOrder.getMealFormat().toString().equals(MealFormat.SCHEDULED.toString()))
			fragment = new ScheduledOrderFragment(customerOrder, availableMealType);

		CustomerUtils.nextFragment(fragment, mexistinguser.getSupportFragmentManager(), true);

	}

}