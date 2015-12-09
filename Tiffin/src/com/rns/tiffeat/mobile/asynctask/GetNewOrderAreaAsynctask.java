package com.rns.tiffeat.mobile.asynctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.rns.tiffeat.mobile.FirstTimeUse;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CoreServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class GetNewOrderAreaAsynctask extends AsyncTask<String, String, String> implements AndroidConstants {

	FragmentActivity mneworder;
	CustomerOrder customerOrder;
	ProgressDialog progressDialog;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = UserUtils.showLoadingDialog(mneworder, "Download Vendors ", "Loading.....");
	}

	public GetNewOrderAreaAsynctask(FragmentActivity splashScreen, CustomerOrder customerOrder2) {

		mneworder = splashScreen;
		customerOrder = customerOrder2;

	}

	@Override
	protected String doInBackground(String... arg) {

		if (!Validation.isNetworkAvailable(mneworder)) {
			return null;
		}
		try {
			CoreServerUtils.retrieveVendorAreaNames();
			return "Hello";

		} catch (Exception e) {
		}
		return null;
	}

	protected void onPostExecute(String result) {

		progressDialog.dismiss();

		if (result == null) {
			Validation.showError(mneworder, ERROR_FETCHING_DATA);
			return;
		}

		Toast.makeText(mneworder, result, Toast.LENGTH_LONG).show();
		Fragment fragment = null;
		fragment = new FirstTimeUse(customerOrder);

		CustomerUtils.nextFragment(fragment, mneworder.getSupportFragmentManager(), true);

	};

}
