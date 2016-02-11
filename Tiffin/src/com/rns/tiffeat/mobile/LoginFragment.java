package com.rns.tiffeat.mobile;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
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

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.rns.tiffeat.mobile.asynctask.LoginAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class LoginFragment extends Fragment implements AndroidConstants, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	private Button submit;
	private TextView newuser;
	private View rootview;
	private Customer customer;
	private EditText email, password;
	private CustomerOrder customerOrder;

	private static GoogleApiClient mGoogleApiClient;
	private int RESULT_OK = 1;
	private static final int RC_SIGN_IN = 0;
	private boolean mIsResolving = false;
	private boolean msignedInClicked = false;
	private SignInButton signinButton;

	public LoginFragment(CustomerOrder customerOrder2) {
		this.customerOrder = customerOrder2;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootview = inflater.inflate(R.layout.fragment_login, container, false);

		if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
		} else {

			initialise();
			signinButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//msignedIn = true;
					googlePlusSignIn();
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
							new LoginAsyncTask(getActivity(), customerOrder,"LOGINFRAGMENT").execute();
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
		return rootview;
	}

	private void initialise() {
		customer = new Customer();
		submit = (Button) rootview.findViewById(R.id.login_submit_button);
		newuser = (TextView) rootview.findViewById(R.id.login_newuser_button);
		email = (EditText) rootview.findViewById(R.id.login_editText_email);
		password = (EditText) rootview.findViewById(R.id.login_editText_Password);
		signinButton = (SignInButton) rootview.findViewById(R.id.signin);

		mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) LoginFragment.this)
				.addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) LoginFragment.this)
				.addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_PROFILE).build();

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
		mGoogleApiClient.connect();
	}

	public void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!mIsResolving && msignedInClicked) {
			if (result.hasResolution()) {
				try {
					result.startResolutionForResult(getActivity(), RC_SIGN_IN);
					mIsResolving = true;
				} catch (IntentSender.SendIntentException e) {
					mIsResolving = false;
					mGoogleApiClient.connect();
				}
			} else {
				CustomerUtils.alertbox(TIFFEAT, "Connection with Google failed!!", getActivity());
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RC_SIGN_IN) {
			if (resultCode != RESULT_OK) {
				msignedInClicked = false;
			}
			mIsResolving = false;
			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}

		} else
			Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onConnected(Bundle bundle) {
		msignedInClicked = false;

		Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();
		new GoogleAccessToken().execute();
	}


	private void googlePlusSignIn() {
		msignedInClicked = true;
		mGoogleApiClient.connect();

	}

	@Override
	public void onConnectionSuspended(int arg0) {
	}

	public class GoogleAccessToken extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			String response = "";
			try {
				String scope = "oauth2:" + Scopes.PLUS_LOGIN + " https://www.googleapis.com/auth/plus.profile.emails.read";
				response = GoogleAuthUtil.getToken(getActivity(), Plus.AccountApi.getAccountName(mGoogleApiClient), scope);
			} catch (UserRecoverableAuthException e) {
				startActivityForResult(e.getIntent(), RC_SIGN_IN);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			getProfileInformation(result);
		}

	}

	private void getProfileInformation(String strAccessToken) {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
				String mPersonName = currentPerson.getDisplayName();
				String mPersonGooglePlusProfile = currentPerson.getUrl();
				String mEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
				String mPersonID = currentPerson.getId();

				customer.setEmail(mEmail);
				customer.setName(mPersonName);

				if (customerOrder == null) {
					customerOrder = new CustomerOrder();
				}
				customerOrder.setCustomer(customer);
				new LoginAsyncTask(getActivity(), customerOrder, "LOGINGOOGLE").execute();
			} else
				return;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mGoogleApiClient.isConnected()) {
				Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
				mGoogleApiClient.disconnect();
			}
		}
	}
}
