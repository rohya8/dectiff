package com.rns.tiffeat.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;
import com.rns.tiffeat.mobile.R;
import com.rns.tiffeat.mobile.asynctask.LoginAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class LoginActivity extends ActionBarActivity implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener, AndroidConstants {


	private static final int RC_SIGN_IN = 0;

	private GoogleApiClient mGoogleApiClient;
	private EditText email, password;
	private Button login;
	private TextView newuser;
	private Customer customer;
	private boolean mIntentInProgress;
	private boolean signedInUser;
	private ConnectionResult mConnectionResult;
	private SignInButton signinButton;
	private LinearLayout signinFrame;
	private CustomerOrder customerOrder;
	private Activity currentActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initialise();

		if (getIntent().getExtras() != null) {
			String customerOrderJson = getIntent().getExtras().getString(CUSTOMER_ORDER_OBJECT);
			customerOrder = new Gson().fromJson(customerOrderJson, CustomerOrder.class);
		}

		signinButton.setOnClickListener(this);

		mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
				.addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();

		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
				}

				if (!Validation.isNetworkAvailable(LoginActivity.this)) {
					Validation.showError(LoginActivity.this, ERROR_NO_INTERNET_CONNECTION);
				} else {
					if (validateInfo()) {
						customer.setEmail(email.getText().toString());
						customer.setPassword(password.getText().toString());
						customerOrder.setCustomer(customer);
						new LoginAsyncTask(LoginActivity.this, customerOrder, LOGIN_FRAGMENT).execute();
					} else
						CustomerUtils.alertbox(TIFFEAT, " Enter Valid Credentials ", LoginActivity.this);
				}
			}
		});
		currentActivity = this;
		newuser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CustomerUtils.startDrawerActivity(currentActivity, customerOrder, customer, ACTION_REGISTRATION);
			}
		});

	}

	private void initialise() {

		customer = new Customer();
		signinButton = (SignInButton) findViewById(R.id.signin);
		login = (Button) findViewById(R.id.login_submit_button);
		newuser = (TextView) findViewById(R.id.login_newuser_button);
		email = (EditText) findViewById(R.id.login_editText_email);
		password = (EditText) findViewById(R.id.login_editText_Password);
		signinFrame = (LinearLayout) findViewById(R.id.signinFrame);
	}

	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
			return;
		}

		if (!mIntentInProgress) {
			mConnectionResult = result;

			if (signedInUser) {
				resolveSignInError();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		switch (requestCode) {
		case RC_SIGN_IN:
			if (responseCode == RESULT_OK) {
				signedInUser = false;

			}
			mIntentInProgress = false;
			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
			break;
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		signedInUser = false;
		Toast.makeText(this, "Login successful !!!", Toast.LENGTH_LONG).show();
		getProfileInformation();
	}

	private void updateProfile(boolean isSignedIn) {
		if (isSignedIn) {
			signinFrame.setVisibility(View.GONE);
		} else {
			signinFrame.setVisibility(View.VISIBLE);
		}
	}

	private void getProfileInformation() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
				String personName = currentPerson.getDisplayName();
				String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

				Customer customer = new Customer();
				customer.setEmail(email);
				customer.setName(personName);

				if (customerOrder == null) {
					customerOrder = new CustomerOrder();
				}
				customerOrder.setCustomer(customer);
				googlePlusLogout();
				new LoginAsyncTask(this, customerOrder, LOGIN_W_GOOGLE).execute();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		mGoogleApiClient.connect();
		updateProfile(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.signin:
			googlePlusLogin();
			break;
		}
	}

	public void signIn(View v) {
		googlePlusLogin();
	}

	public void logout(View v) {
		googlePlusLogout();
	}

	private void googlePlusLogin() {
		if (!mGoogleApiClient.isConnecting()) {
			signedInUser = true;
			resolveSignInError();
		}
	}

	private void googlePlusLogout() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();
			updateProfile(false);
		}
	}

	private boolean validateInfo() {
		if (TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(password.getText())) {
			return false;
		}
		if (!Validation.isEmailAddress(email, true))
			return false;

		return true;
	}

}
