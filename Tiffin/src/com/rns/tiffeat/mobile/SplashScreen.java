package com.rns.tiffeat.mobile;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import com.rns.tiffeat.mobile.asynctask.GetCurrentCustomerAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.Customer;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SplashScreen extends AppCompatActivity implements AndroidConstants {

	final Context context = this;
	private TextView text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		text = (TextView) findViewById(R.id.splashscreen_name_textView);

		String multiColorText = "<font color=0x4CAF50>T    i    f   f</font><font color=0xf44336>   E   a   t</font>";
		text.setText(Html.fromHtml(multiColorText));

		if (!Validation.isNetworkAvailable(context)) {
			Validation.showError(context, ERROR_NO_INTERNET_CONNECTION);

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							finish();
						}
					}, 3000);
				}

			});

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
					}, 4000);
				}
			});
		}
	}

	public void AsyncTaskCall() {
		Customer customer = CustomerUtils.getCurrentCustomer(SplashScreen.this);
		if (TextUtils.isEmpty(customer.getEmail())) {
			Intent i = new Intent(SplashScreen.this, DrawerActivity.class);
			startActivity(i);
			finish();
		} else {
			new GetCurrentCustomerAsyncTask(SplashScreen.this).execute();

		}
	}
}