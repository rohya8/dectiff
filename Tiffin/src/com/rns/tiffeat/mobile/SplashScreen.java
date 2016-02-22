package com.rns.tiffeat.mobile;

import java.util.HashMap;
import java.util.Map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.rns.tiffeat.mobile.asynctask.GetCurrentCustomerAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.Customer;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SplashScreen extends AppCompatActivity implements AndroidConstants {

	final Context context = this;
	private TextView text;
	private static final int MY_PERMISSIONS_REQUEST_CODE = 0;
	private int versionName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		text = (TextView) findViewById(R.id.splashscreen_name_textView);

		String multiColorText = "<font color=0x4CAF50>T    i    f   f</font><font color=0xf44336>   E   a   t</font>";
		text.setText(Html.fromHtml(multiColorText));

		try {
			versionName = Build.VERSION.SDK_INT;
		} catch (Exception e) {
		}

		if (versionName >= 23)
			checkPermissions();


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
			GetCurrentCustomerAsyncTask getCurrentCustomerAsyncTask = new GetCurrentCustomerAsyncTask(SplashScreen.this);
			getCurrentCustomerAsyncTask.setNoLoader(true);
			getCurrentCustomerAsyncTask.execute();

		}
	}


	private void checkPermissions() {
		if (ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

				askForPermission();
			} else {
				askForPermission();
			}
		} else if (ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this, Manifest.permission.GET_ACCOUNTS)) {

				askForPermission();
			} else {
				askForPermission();
			}
		} else if (ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.USE_CREDENTIALS) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this, Manifest.permission.USE_CREDENTIALS)) {

				askForPermission();
			} else {
				askForPermission();
			}
		} else if (ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this, Manifest.permission.READ_PHONE_STATE)) {

				askForPermission();
			} else {
				askForPermission();
			}
		} else if (ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this, Manifest.permission.ACCESS_NETWORK_STATE)) {

				askForPermission();
			} else {
				askForPermission();
			}
		} else if (ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this, Manifest.permission.INTERNET)) {

				askForPermission();
			} else {
				askForPermission();
			}
		}

	}

	private void askForPermission() {
		ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.GET_ACCOUNTS,
				Manifest.permission.USE_CREDENTIALS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_NETWORK_STATE,
				Manifest.permission.INTERNET }, MY_PERMISSIONS_REQUEST_CODE);
	}

	/*
	 * Show alert dialog with message that some permissions are disbled means
	 * denied by user And redirect him to settings of app to enable permissons.
	 */
	private void showPermissionSettings() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashScreen.this);
		alertDialog.setMessage("It seems that you have disabled some permissions for this application. To use this application enable required permissions. ");
		alertDialog.setCancelable(false);

		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				Uri uri = Uri.fromParts("package", getPackageName(), null);
				intent.setData(uri);
				startActivity(intent);

				finish();
			}
		});
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				finish();
			}
		}).create().show();
	}

	@SuppressLint({ "NewApi", "Override" })
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
		case MY_PERMISSIONS_REQUEST_CODE: {

			Map<String, Integer> perms = new HashMap<String, Integer>();
			perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
			perms.put(Manifest.permission.GET_ACCOUNTS, PackageManager.PERMISSION_GRANTED);
			perms.put(Manifest.permission.USE_CREDENTIALS, PackageManager.PERMISSION_GRANTED);
			perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
			perms.put(Manifest.permission.ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);
			perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);

			for (int i = 0; i < permissions.length; i++)
				perms.put(permissions[i], grantResults[i]);

			if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
					&& perms.get(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED
					&& perms.get(Manifest.permission.USE_CREDENTIALS) == PackageManager.PERMISSION_GRANTED
					&& perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
					&& perms.get(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
					&& perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(SplashScreen.this, "All required permission granted", Toast.LENGTH_SHORT).show();
			} else {
				showPermissionSettings();
			}

			/*
			 * This commented code is same as above. Here i have checked
			 * separate permission. If you want to check comment code from 'Map'
			 * just above till this comment And un-comment below code.
			 */
			// if (grantResults.length > 0
			// && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			//
			// // permission was granted, yay! Do the
			// // contacts-related task you need to do.
			// } else {
			// // permission denied, boo! Disable the
			// // functionality that depends on this permission.
			//
			// String permission = permissions[0];
			// boolean showRationale =
			// shouldShowRequestPermissionRationale(permission);
			// if (!showRationale) {
			// // user denied flagging NEVER ASK AGAIN
			// // you can either enable some fall bac,
			// // disable features of your app
			// // or open another dialog explaining
			// // again the permission and directing to
			// // the app setting
			//
			// showPermissionSettings();
			// } else if
			// (Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission)) {
			// // user denied WITHOUT never ask again
			// // this is a good place to explain the user
			// // why you need the permission and ask if he want
			// // to accept it (the rationale)
			//
			// finish();
			// }
			//
			// }
			//
			// if (grantResults.length > 0
			// && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
			//
			// // permission was granted, yay! Do the
			// // contacts-related task you need to do.
			// } else {
			// // permission denied, boo! Disable the
			// // functionality that depends on this permission.
			//
			// String permission = permissions[1];
			// boolean showRationale =
			// shouldShowRequestPermissionRationale(permission);
			// if (!showRationale) {
			// // user denied flagging NEVER ASK AGAIN
			// // you can either enable some fall bac,
			// // disable features of your app
			// // or open another dialog explaining
			// // again the permission and directing to
			// // the app setting
			//
			// showPermissionSettings();
			// } else if
			// (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {
			// // user denied WITHOUT never ask again
			// // this is a good place to explain the user
			// // why you need the permission and ask if he want
			// // to accept it (the rationale)
			//
			// finish();
			// }
			// }
			//
			// if (grantResults.length > 0
			// && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
			//
			// // permission was granted, yay! Do the
			// // contacts-related task you need to do.
			// } else {
			// // permission denied, boo! Disable the
			// // functionality that depends on this permission.
			//
			// String permission = permissions[2];
			// boolean showRationale =
			// shouldShowRequestPermissionRationale(permission);
			// if (!showRationale) {
			// // user denied flagging NEVER ASK AGAIN
			// // you can either enable some fall bac,
			// // disable features of your app
			// // or open another dialog explaining
			// // again the permission and directing to
			// // the app setting
			//
			// showPermissionSettings();
			// } else if (Manifest.permission.CAMERA.equals(permission)) {
			// // user denied WITHOUT never ask again
			// // this is a good place to explain the user
			// // why you need the permission and ask if he want
			// // to accept it (the rationale)
			//
			// finish();
			// }
			// }
			// if (grantResults.length > 0
			// && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
			//
			// // permission was granted, yay! Do the
			// // contacts-related task you need to do.
			// } else {
			// // permission denied, boo! Disable the
			// // functionality that depends on this permission.
			//
			// String permission = permissions[3];
			// boolean showRationale =
			// shouldShowRequestPermissionRationale(permission);
			// if (!showRationale) {
			// // user denied flagging NEVER ASK AGAIN
			// // you can either enable some fall bac,
			// // disable features of your app
			// // or open another dialog explaining
			// // again the permission and directing to
			// // the app setting
			//
			// showPermissionSettings();
			// } else if
			// (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission)) {
			// // user denied WITHOUT never ask again
			// // this is a good place to explain the user
			// // why you need the permission and ask if he want
			// // to accept it (the rationale)
			//
			// finish();
			// }
			// }
			//
			// if (grantResults.length > 0
			// && grantResults[4] == PackageManager.PERMISSION_GRANTED) {
			//
			// // permission was granted, yay! Do the
			// // contacts-related task you need to do.
			// } else {
			// // permission denied, boo! Disable the
			// // functionality that depends on this permission.
			//
			// String permission = permissions[4];
			// boolean showRationale =
			// shouldShowRequestPermissionRationale(permission);
			// if (!showRationale) {
			// // user denied flagging NEVER ASK AGAIN
			// // you can either enable some fall bac,
			// // disable features of your app
			// // or open another dialog explaining
			// // again the permission and directing to
			// // the app setting
			//
			// showPermissionSettings();
			// } else if
			// (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
			// // user denied WITHOUT never ask again
			// // this is a good place to explain the user
			// // why you need the permission and ask if he want
			// // to accept it (the rationale)
			//
			// finish();
			// }
			// }

			return;
		}

		}
	}

}