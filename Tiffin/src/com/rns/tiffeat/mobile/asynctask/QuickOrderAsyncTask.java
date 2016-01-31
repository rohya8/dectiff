package com.rns.tiffeat.mobile.asynctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class QuickOrderAsyncTask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity proceedtopay;
	private ProgressDialog progressdialog;
	private CustomerOrder customerOrder;
	private Customer currentCustomer;

	public QuickOrderAsyncTask(FragmentActivity contxt, CustomerOrder customerOrder) {
		proceedtopay = contxt;
		this.customerOrder = customerOrder;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressdialog = UserUtils.showLoadingDialog(proceedtopay, "Quick Order ", "Please Wait...");
	}

	@Override
	protected String doInBackground(String... args) {

		if (!Validation.isNetworkAvailable(proceedtopay)) {
			return null;
		}
		try {
			String result = CustomerServerUtils.quickOrder(customerOrder);
			result = new Gson().fromJson(result, String.class);
			if ("OK".equals(result)) {
				currentCustomer = CustomerServerUtils.getCurrentCustomer(customerOrder.getCustomer());
				CustomerUtils.storeCurrentCustomer(proceedtopay, currentCustomer);
			}
			return result;
		} catch (Exception e) {
			CustomerUtils.exceptionOccurred(e.getMessage(), getClass().getSimpleName());
		}
		return null;

	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressdialog.dismiss();
		if (result == null) {
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, proceedtopay);
			return;
		}
		if ("OK".equals(result)) {
			CustomerUtils.alertbox(TIFFEAT, "Order Successfull! ", proceedtopay);
			if (currentCustomer != null) {
				nextActivity();
			}
		} else {
			CustomerUtils.alertbox(TIFFEAT, "Order Failed due to : " + result, proceedtopay);
		}
	}

	private void nextActivity() {
		new DrawerUpdateAsynctask(proceedtopay, currentCustomer).execute("");

	}

}