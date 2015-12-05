package com.rns.tiffeat.mobile.asynctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.ListOfMeals;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.Vendor;

public class GetMealsForVendorAsynctask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity activity;
	private Vendor vendor;
	private CustomerOrder customerOrder;
	private ProgressDialog progressDialog;

	public GetMealsForVendorAsynctask(FragmentActivity myactivity, Vendor vendor, CustomerOrder customerOrder) {
		this.activity = myactivity;
		this.vendor = vendor;
		this.customerOrder = customerOrder;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = UserUtils.showLoadingDialog(activity, "Download meals", "Getting available Tiffins..");
	}

	@Override
	protected String doInBackground(String... params) {
		if (!Validation.isNetworkAvailable(activity)) {
			return null;
		}
		try {
			String result = CustomerServerUtils.getMealsForVendor(vendor);
			return result;
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);

		if (result == null) {
			Validation.showError(activity, ERROR_FETCHING_DATA);
			return;
		}
		vendor = new Gson().fromJson(result, Vendor.class);
		progressDialog.dismiss();
		Fragment fragment = new ListOfMeals(vendor, customerOrder);
		CustomerUtils.nextFragment(fragment, activity.getSupportFragmentManager(), true);
	}
}
