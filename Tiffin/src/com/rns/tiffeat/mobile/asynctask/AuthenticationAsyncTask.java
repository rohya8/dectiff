package com.rns.tiffeat.mobile.asynctask;

import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;

public class AuthenticationAsyncTask extends AsyncTask<String, String, String> implements AndroidConstants 
{

	private ProgressDialog progressDialog;
	private Customer customerlogin;
	private CustomerOrder customerOrder;
	private String code;
	private FragmentActivity mauthentication;


	public AuthenticationAsyncTask(FragmentActivity activity, CustomerOrder customerOrder2, String secretcode) {
		// this.fragmentActivity = activity;
		this.mauthentication = activity;
		this.customerOrder = customerOrder2;
		this.code = secretcode ;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		progressDialog = UserUtils.showLoadingDialog(mauthentication, "Sending Mail", "Please Wait...");
	}

	@Override
	protected String doInBackground(String... params) 
	{
		String result = null;
		try
		{
			

			return result; 
		}
		catch(Exception e)
		{
			CustomerUtils.exceptionOccurred(e.getMessage(), getClass().getSimpleName());
		}
		return null;
	}

	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressDialog.dismiss();
		if (result == null) {
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, mauthentication);
			return;
		}
		if ("OK".equals(result)) {
			CustomerUtils.alertbox(TIFFEAT, "Verfication code has been sent on" + customerOrder.getCustomer().getEmail(), mauthentication);
			nextActivity();
		} else {
			CustomerUtils.alertbox(TIFFEAT, "Email Failed : " + result, mauthentication);
		}
	}
	private void nextActivity() 
	{
		Customer customer = CustomerUtils.getCurrentCustomer(mauthentication);
		
	}
}
