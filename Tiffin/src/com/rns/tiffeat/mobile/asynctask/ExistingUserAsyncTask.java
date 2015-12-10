package com.rns.tiffeat.mobile.asynctask;

import java.util.Date;
import java.util.Map;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
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
import com.rns.tiffeat.web.util.Constants;

public class ExistingUserAsyncTask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity context;
	private ProgressDialog progressdialog;
	private CustomerOrder customerOrder;
	private String availableMealTypeResult;
	private Map<MealType, Date> availableMealType;

	public ExistingUserAsyncTask(FragmentActivity contxt, CustomerOrder customerOrder) {
		this.context = contxt;
		this.customerOrder = customerOrder;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressdialog = UserUtils.showLoadingDialog(context, "Download Data ", "Preparing.....");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String doInBackground(String... args) {
		if (!Validation.isNetworkAvailable(context)) {
			return null;
		}
		try {
			availableMealTypeResult = CustomerServerUtils.customerGetMealAvailable(customerOrder);
			Map<String, Object> customerOrderVailableMealTypesMap = CustomerUtils.convertToStringObjectMap(availableMealTypeResult);

			String customerOrderString = (String) customerOrderVailableMealTypesMap.get(Constants.MODEL_CUSTOMER_ORDER);
			availableMealType = CustomerUtils.convertToMealTypeDateMap((String) customerOrderVailableMealTypesMap.get(Constants.MODEL_MEAL_TYPE));;
			customerOrder = new Gson().fromJson(customerOrderString, CustomerOrder.class);

			return availableMealTypeResult;
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressdialog.dismiss();
		if (result == null) {
			Validation.showError(context, ERROR_FETCHING_DATA);
			return;
		}
		if (customerOrder != null) {
			nextActivity();
		} else {
			Toast.makeText(context, "Something Went Wrong !!!", Toast.LENGTH_SHORT).show();
		}

	}

	private void nextActivity() {

		Fragment fragment = null;

		if (MealFormat.QUICK.equals(customerOrder.getMealFormat()))
			fragment = new QuickOrderFragment(customerOrder, availableMealType);
		else if (MealFormat.SCHEDULED.equals(customerOrder.getMealFormat()))
			fragment = new ScheduledOrderFragment(customerOrder, availableMealType);


		CustomerUtils.nextFragment(fragment, context.getSupportFragmentManager(), true);

	}

}