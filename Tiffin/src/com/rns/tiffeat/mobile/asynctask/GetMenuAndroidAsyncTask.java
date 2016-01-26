package com.rns.tiffeat.mobile.asynctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.ShowMenuFragment;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.DailyContent;

public class GetMenuAndroidAsyncTask extends AsyncTask<String, String, DailyContent> implements AndroidConstants {

	private FragmentActivity context;
	private CustomerOrder customerOrder;
	private ProgressDialog progressDialog;

	public GetMenuAndroidAsyncTask(FragmentActivity context, CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = UserUtils.showLoadingDialog(context, "Contacting your vendor", "Getting menu....");
	}

	@Override
	protected DailyContent doInBackground(String... arg0) {

		if (!Validation.isNetworkAvailable(context)) {
			return null;
		}

		try {

			DailyContent result = new Gson().fromJson(CustomerServerUtils.getMealMenuAndroid(customerOrder), DailyContent.class);

			if (result == null) {
				result = new DailyContent();
				result.setMainItem("No Meal available");
			}
			return result;
		} catch (Exception e) {
			CustomerUtils.exceptionOccurred(e.getMessage(), getClass().getSimpleName());
		}

		return null;
	}

	@Override
	protected void onPostExecute(DailyContent dailyContent) {
		super.onPostExecute(dailyContent);
		progressDialog.dismiss();

		if (dailyContent == null) {
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, context);
			return;
		}
		Fragment fragment = null;
		customerOrder.setContent(dailyContent);
		fragment = new ShowMenuFragment(customerOrder);

		CustomerUtils.nextFragment(fragment, context.getSupportFragmentManager(), false);
	}
}