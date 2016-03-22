package com.rns.tiffeat.mobile.asynctask;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;

public class MealRatingAsyncTask extends AsyncTask<String, String, String> implements AndroidConstants 
{
	Context rating;
	private ProgressDialog progressDialog;
	private Customer mealrateus;
	private CustomerOrder customerOrders;

	public MealRatingAsyncTask(Context context, CustomerOrder customerOrder) 
	{
		this.customerOrders = customerOrder;
		rating = context;
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		progressDialog = UserUtils.showLoadingDialog(rating, "Checking  Details", "Please Wait...");
	}

	@Override
	protected String doInBackground(String... params) 
	{
		String result = null;
		try 
		{
			result = CustomerServerUtils.getRatingForMeals(customerOrders);
			result = new Gson().fromJson(result, String.class);
		}
		catch (Exception e) {
			CustomerUtils.exceptionOccurred(e.getMessage(), getClass().getSimpleName());
		}
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressDialog.dismiss();
		if (result == null) {
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, rating);
			return;
		}
		if ("OK".equals(result)) {
			CustomerUtils.alertbox(TIFFEAT, "Thankyou for rating this meal !!", rating);

		} else {
			CustomerUtils.alertbox(TIFFEAT, "Order Failed due to : " + result, rating);
		}
	}

}

