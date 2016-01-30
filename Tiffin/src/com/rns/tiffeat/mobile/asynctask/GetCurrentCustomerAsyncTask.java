package com.rns.tiffeat.mobile.asynctask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.DrawerActivity;
import com.rns.tiffeat.mobile.QuickOrderHomeScreen;
import com.rns.tiffeat.mobile.ScheduledOrderHomeScreen;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.Customer;

public class GetCurrentCustomerAsyncTask extends AsyncTask<String, String, Customer> implements AndroidConstants {

	private QuickOrderHomeScreen quickOrderHome;
	private Context context;
	private ScheduledOrderHomeScreen scheduledHome;

	public GetCurrentCustomerAsyncTask(Context context, QuickOrderHomeScreen quickHome) {
		this.quickOrderHome = quickHome;
		this.context = context;
	}

	public GetCurrentCustomerAsyncTask(Context activity, ScheduledOrderHomeScreen scheduledUser) {
		this.scheduledHome = scheduledUser;
		this.context = activity;
	}

	public GetCurrentCustomerAsyncTask(Context activity) {
		this.context = activity;
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
				CustomerUtils.exceptionOccurred(e.getMessage(), getClass().getSimpleName());
			}
		}
		return null;

	}

	@Override
	protected void onPostExecute(Customer result) {
		super.onPostExecute(result);
		if (result == null) {
			//Validation.showError(context, ERROR_FETCHING_DATA);
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, context);
			return;
		}
		CustomerUtils.storeCurrentCustomer(context, result);
		if (scheduledHome != null) {
			scheduledHome.setCustomer(result);
			scheduledHome.prepareScreen();
		} else if (quickOrderHome == null) {
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
		((Activity) context).finish();

	}

}
