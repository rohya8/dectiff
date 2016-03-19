package com.rns.tiffeat.mobile.asynctask;

import com.rns.tiffeat.mobile.util.AndroidConstants;
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
	private Activity context;

	
	public AuthenticationAsyncTask(FragmentActivity activity, CustomerOrder customerOrder2, String string) {
		// this.fragmentActivity = activity;
		this.context = activity;
		this.customerOrder = customerOrder2;
		this.code = string;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		progressDialog = UserUtils.showLoadingDialog(context, "Sending Mail", "Please Wait...");
	}
	
	@Override
	protected String doInBackground(String... params) 
	{
		String result = null;
		return null;
	}
 
}
