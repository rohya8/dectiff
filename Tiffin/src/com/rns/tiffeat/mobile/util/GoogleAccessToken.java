package com.rns.tiffeat.mobile.util;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.rns.tiffeat.mobile.asynctask.LoginAsyncTask;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class GoogleAccessToken extends AsyncTask<String, String, String> {

	private FragmentActivity activity;
	private GoogleApiClient mGoogleApiClient;
	private CustomerOrder customerOrder;
	private int rcSignIn;

	public GoogleAccessToken(FragmentActivity activity, GoogleApiClient googleApiClient, CustomerOrder customerOrder, int rcSignIn) {
		this.activity = activity;
		this.mGoogleApiClient = googleApiClient;
		this.customerOrder = customerOrder;
		this.rcSignIn = rcSignIn;
	}

	@Override
	protected String doInBackground(String... params) {
		String response = "";
		try {
			String scope = "oauth2:" + Scopes.PLUS_LOGIN + " https://www.googleapis.com/auth/plus.profile.emails.read";
			response = GoogleAuthUtil.getToken(activity, Plus.AccountApi.getAccountName(mGoogleApiClient), scope);
		} catch (UserRecoverableAuthException e) {
			activity.startActivityForResult(e.getIntent(), rcSignIn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	protected void onPostExecute(String result) {
		getProfileInformation(result);
	}

	private void getProfileInformation(String strAccessToken) {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
				String mPersonName = currentPerson.getDisplayName();
				// String mPersonGooglePlusProfile = currentPerson.getUrl();
				String mEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
				// String mPersonID = currentPerson.getId();
				Customer customer = new Customer();
				customer.setEmail(mEmail);
				customer.setName(mPersonName);

				if (customerOrder == null) {
					customerOrder = new CustomerOrder();
				}
				customerOrder.setCustomer(customer);
				new LoginAsyncTask(activity, customerOrder, "LOGINGOOGLE").execute();
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
