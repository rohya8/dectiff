package com.rns.tiffeat.mobile.asynctask;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.rns.tiffeat.mobile.NewListOfMeals;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.Meal;

public class GetVendorsForAreaAsynctask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity activity;
	private ProgressDialog progressDialog;
	private List<Meal> meals;
	private CustomerOrder customerOrder;

	public GetVendorsForAreaAsynctask(FragmentActivity activity, CustomerOrder customerOrder) {
		this.activity = activity;
		this.customerOrder = customerOrder;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = UserUtils.showLoadingDialog(activity, "Getting your meal", "Getting nearby vendors..");
	}

	public List<Meal> getMeals() {
		return meals;
	}

	public void setMeals(List<Meal> meals) {
		this.meals = meals;
	}

	@Override
	protected String doInBackground(String... params) {

		if (!Validation.isNetworkAvailable(activity)) {
			return null;
		}
		try {
			meals = CustomerServerUtils.getMealsForOrder(customerOrder);
		} catch (Exception e) {
			CustomerUtils.exceptionOccurred(e.getMessage(), getClass().getSimpleName());
			return null;
		}
		return RESPONSE_OK;

	};

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressDialog.dismiss();
		if (result == null) {
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, activity);
			return;
		}

		if (CollectionUtils.isEmpty(meals)) {
			CustomerUtils.alertbox(TIFFEAT, NO_MEALS_CURRENTLY_AVAILABLE_IN_THIS_AREA, activity);
			return;
		} else {
			Fragment fragment = new NewListOfMeals(customerOrder, meals);
			CustomerUtils.nextFragment(fragment, activity.getSupportFragmentManager(), false);
		}

	}
}