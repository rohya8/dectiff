package com.rns.tiffeat.mobile.asynctask;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.DrawerActivity;
import com.rns.tiffeat.mobile.QuickOrderHomeScreen;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.Customer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class GetCurrentCustomerAsyncTask extends AsyncTask<String, String, Customer> implements AndroidConstants{

	private QuickOrderHomeScreen quickOrderHome;
	private Context context;

	public GetCurrentCustomerAsyncTask(Context context, QuickOrderHomeScreen quickHome) {
		this.quickOrderHome = quickHome;
		this.context = context;
	}

	@Override
	protected Customer doInBackground(String... arg0) {
		if (!Validation.isNetworkAvailable(context)) {
			return null;
		} else {

			try {
				Customer currentCustomer = CustomerUtils.getCurrentCustomer(context);
				Customer latestCustomer = currentCustomer;
				latestCustomer = CustomerServerUtils.getCurrentCustomer(currentCustomer);
				return latestCustomer;
			} catch (Exception e) {
				
			}
		}
		return null;

	}

	@Override
	protected void onPostExecute(Customer result) {
		super.onPostExecute(result);
		if (result == null) {
			Validation.showError(context, ERROR_FETCHING_DATA);
			return;
		}
		CustomerUtils.storeCurrentCustomer(context, result);
		if (quickOrderHome == null) {
			nextActivity(result);
		} else {
			quickOrderHome.setCustomer(result);
			quickOrderHome.prepareScreen();
		}
	}

	private void nextActivity(Customer customer) {
		Intent i = new Intent(context, DrawerActivity.class);
		i.putExtra(AndroidConstants.CUSTOMER_OBJECT, new Gson().toJson(customer));
		context.startActivity(i);

	}

}
