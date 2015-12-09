package com.rns.tiffeat.mobile;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.rns.tiffeat.mobile.asynctask.GetAreaAsynctask;
import com.rns.tiffeat.mobile.asynctask.GetCurrentCustomerAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.Customer;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SplashScreen extends AppCompatActivity implements AndroidConstants{

	final Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		if (!Validation.isNetworkAvailable(context)) {
			Validation.showError(context,ERROR_NO_INTERNET_CONNECTION );
		} else {

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							AsyncTaskCall();
						}
					}, 1000);
				}

			});
		}

	}

	public void AsyncTaskCall() {
		Customer customer = CustomerUtils.getCurrentCustomer(getApplicationContext());
		if (TextUtils.isEmpty(customer.getEmail())) {
			new GetAreaAsynctask(this).execute();
		} else {
			new GetCurrentCustomerAsyncTask(this, null).execute();
		}
	}

}
