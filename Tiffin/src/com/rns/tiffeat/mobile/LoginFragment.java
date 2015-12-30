package com.rns.tiffeat.mobile;

import android.content.Context;
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

import com.rns.tiffeat.mobile.asynctask.LoginAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

//public class LoginFragment extends Fragment implements OnClickListener, AndroidConstants, ConnectionCallbacks, OnConnectionFailedListener {
public class LoginFragment extends Fragment implements AndroidConstants {
	private Button submit;
	private TextView newuser;
	private View view;
	private Customer customer;
	private EditText email, password;
	private CustomerOrder customerOrder;
	
	// private int RESULT_OK = -1;
	// private static final int RC_SIGN_IN = 0;
	//
	// private GoogleApiClient mGoogleApiClient;
	//
	// private boolean mIntentInProgress;
	// private boolean signedInUser;
	// private ConnectionResult mConnectionResult;
	// private SignInButton signinButton;

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
			// signinButton.setOnClickListener(LoginFragment.this);
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
							customerOrder.setCustomer(customer);
							new LoginAsyncTask(getActivity(), customerOrder).execute();
						} else
							Toast.makeText(getActivity(), " Enter Valid Credentials ", Toast.LENGTH_SHORT).show();
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
		// signinButton = (SignInButton) view.findViewById(R.id.signin);
		// mGoogleApiClient = new
		// GoogleApiClient.Builder(getActivity()).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
		// .addApi(Plus.API,
		// Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();
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
		return true;
	}
	//
	// public void onStart() {
	// super.onStart();
	// mGoogleApiClient.connect();
	// }
	//
	// public void onStop() {
	// super.onStop();
	// if (mGoogleApiClient.isConnected()) {
	// mGoogleApiClient.disconnect();
	// }
	// }

	// private void resolveSignInError() {
	// if (mConnectionResult.hasResolution()) {
	// try {
	// mIntentInProgress = true;
	// mConnectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
	// } catch (SendIntentException e) {
	// mIntentInProgress = false;
	// mGoogleApiClient.connect();
	// }
	// }
	// }

	// @Override
	// public void onConnectionFailed(ConnectionResult result) {
	// if (!result.hasResolution()) {
	// // GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
	// // this, 0).show();
	// Toast.makeText(getActivity(), "Connection failed!!",
	// Toast.LENGTH_SHORT).show();
	// return;
	// }
	//
	// if (!mIntentInProgress) {
	// // store mConnectionResult
	// mConnectionResult = result;
	//
	// if (signedInUser) {
	// resolveSignInError();
	// }
	// }
	// }
	//
	// @Override
	// public void onActivityResult(int requestCode, int responseCode, Intent
	// intent) {
	// switch (requestCode) {
	// case RC_SIGN_IN:
	//
	// if (responseCode == RESULT_OK) {
	// signedInUser = false;
	//
	// }
	// mIntentInProgress = false;
	// if (!mGoogleApiClient.isConnecting()) {
	// mGoogleApiClient.connect();
	//
	// }
	// break;
	// }
	// }
	//
	// @Override
	// public void onConnected(Bundle arg0) {
	// signedInUser = false;
	// Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
	// getProfileInformation();
	//
	// }
	//
	// private void getProfileInformation() {
	// try {
	// if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
	// Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
	// String personName = currentPerson.getDisplayName();
	// String emailid = Plus.AccountApi.getAccountName(mGoogleApiClient);
	//
	// Toast.makeText(getActivity(), personName, Toast.LENGTH_SHORT).show();
	// Toast.makeText(getActivity(), emailid, Toast.LENGTH_SHORT).show();
	//
	// // email.setText(emailid);
	// // password.setText(personName);
	//
	// customer.setEmail(emailid);
	// customer.setName(personName);
	// customerOrder.setCustomer(customer);
	// new LoginWithGoogleAsynctask(getActivity(), customerOrder).execute();
	//
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	// public void onClick(View v) {
	// switch (v.getId()) {
	// case R.id.signin:
	// googlePlusLogin();
	// break;
	// }
	// }
	//
	// public void signIn(View v) {
	// googlePlusLogin();
	// }
	//
	// private void googlePlusLogin() {
	// if (!mGoogleApiClient.isConnecting()) {
	// signedInUser = true;
	//
	// resolveSignInError();
	// }
	// }
	//
	// @Override
	// public void onConnectionSuspended(int arg0) {
	// mGoogleApiClient.connect();
	// }
}
