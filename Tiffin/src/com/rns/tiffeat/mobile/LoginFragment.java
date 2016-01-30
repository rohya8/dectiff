package com.rns.tiffeat.mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.rns.tiffeat.mobile.asynctask.LoginAsyncTask;
import com.rns.tiffeat.mobile.asynctask.LoginWithGoogleAsynctask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class LoginFragment extends Fragment implements AndroidConstants, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	private Button submit;
	private TextView newuser;
	private View view;
	private Customer customer;
	private EditText email, password;
	private CustomerOrder customerOrder;
	private ProgressDialog progressDialog;
	private int RESULT_OK = -1;
	private static final int RC_SIGN_IN = 0;
	private GoogleApiClient mGoogleApiClient;

	private boolean mIntentInProgress;
	private boolean signedInUser;
	private ConnectionResult mConnectionResult;
	private SignInButton signinButton;

	public LoginFragment(CustomerOrder customerOrder2) {
		this.customerOrder = customerOrder2;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_login, container, false);

		if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
		} else {

			initialise();
			signinButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					googlePlusLogin();
				}
			});
			submit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

					if (!Validation.isNetworkAvailable(getActivity())) {
						Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
					} else {
						if (validateInfo()) {
							customer.setEmail(email.getText().toString());
							customer.setPassword(password.getText().toString());
							if (customerOrder == null) {
								customerOrder = new CustomerOrder();
							}
							customerOrder.setCustomer(customer);
							new LoginAsyncTask(getActivity(), customerOrder).execute();
						} else
							CustomerUtils.alertbox(TIFFEAT, " Enter Valid Credentials ", getActivity());
					}
				}
			});

			newuser.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!Validation.isNetworkAvailable(getActivity())) {
						Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
					} else {
						Fragment fragment = null;
						if (customerOrder == null) {
							customerOrder = new CustomerOrder();
						}
						fragment = new UserRegistration(customerOrder);
						CustomerUtils.nextFragment(fragment, getFragmentManager(), false);
					}
				}
			});
		}
		return view;

	}

	private void initialise() {
		customer = new Customer();
		submit = (Button) view.findViewById(R.id.login_submit_button);
		newuser = (TextView) view.findViewById(R.id.login_newuser_button);
		email = (EditText) view.findViewById(R.id.login_editText_email);
		password = (EditText) view.findViewById(R.id.login_editText_Password);
		signinButton = (SignInButton) view.findViewById(R.id.signin);

		mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addConnectionCallbacks((ConnectionCallbacks) LoginFragment.this)
				.addOnConnectionFailedListener((OnConnectionFailedListener) LoginFragment.this).addApi(Plus.API).addScope(Plus.SCOPE_PLUS_PROFILE).build();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), FONT);
		fontChanger.replaceFonts((ViewGroup) this.getView());
	}

	private boolean validateInfo() {
		if (TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(password.getText())) {
			return false;
		}
		if (!Validation.isEmailAddress(email, true))
			return false;

		return true;
	}

	public void onStart() {
		super.onStart();
		if (mGoogleApiClient != null)
			mGoogleApiClient.connect();
	}

	public void onStop() {
		super.onStop();
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	private void resolveSignInError() {

		if (mConnectionResult != null && mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
			} catch (SendIntentException e) {
				CustomerUtils.alertbox(TIFFEAT, "Connection with Google failed!!", getActivity());
				mIntentInProgress = false;
				Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
				mGoogleApiClient.disconnect();
			}
		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			progressDialog.dismiss();
			CustomerUtils.alertbox(TIFFEAT, "Connection with Google failed!!", getActivity());
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
	public void onActivityResult(int requestCode, int responseCode, Intent intent) {
		switch (requestCode) {
		case RC_SIGN_IN:

			if (responseCode == RESULT_OK) {
				signedInUser = false;

			}
			mIntentInProgress = false;
			if (!mGoogleApiClient.isConnecting()) {
				connectgoogle();
			}

			break;
		}
	}

	private void connectgoogle() {
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnected(Bundle arg0) {
		signedInUser = false;
		if (progressDialog != null)
			progressDialog.dismiss();

		Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();

		getProfileInformation();

	}

	private void getProfileInformation() {

		if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
			Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
			customer.setEmail(Plus.AccountApi.getAccountName(mGoogleApiClient));
			customer.setName(person.getDisplayName());
			if (customerOrder == null) {
				customerOrder = new CustomerOrder();
			}
			customerOrder.setCustomer(customer);
			new LoginWithGoogleAsynctask(getActivity(), customerOrder).execute();
		} else
			return;

		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
		}

	}

	private void googlePlusLogin() {
		progressDialog = UserUtils.showLoadingDialog(getActivity(), "Signing In ", "Please Wait .....");
		if (!mGoogleApiClient.isConnecting()) {
			signedInUser = true;
			resolveSignInError();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
	}
}
