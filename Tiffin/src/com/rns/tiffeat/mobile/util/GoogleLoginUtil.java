package com.rns.tiffeat.mobile.util;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class GoogleLoginUtil implements ConnectionCallbacks, OnConnectionFailedListener, AndroidConstants {

	private int RESULT_OK = 1;
	private static final int RC_SIGN_IN = 0;
	private boolean mIsResolving = false;
	private boolean msignedInClicked = false;
	private GoogleApiClient mGoogleApiClient;
	private Activity activity;
	private CustomerOrder customerOrder;
	private FragmentActivity fragmentActivity;

	public GoogleLoginUtil(FragmentActivity fragmentActivity, Activity context, CustomerOrder customerOrder) {
		this.activity = context;
		this.customerOrder = customerOrder;
		this.fragmentActivity = fragmentActivity;
		mGoogleApiClient = new GoogleApiClient.Builder(fragmentActivity).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_PROFILE).build();
	}

	public GoogleApiClient getmGoogleApiClient() {
		return mGoogleApiClient;
	}

	public void signIn() {
		msignedInClicked = true;
		mGoogleApiClient.connect();
		// acitivity.startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient),
		// RC_SIGN_IN);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!mIsResolving && msignedInClicked) {
			if (result.hasResolution()) {
				try {
					result.startResolutionForResult(activity, RC_SIGN_IN);
					mIsResolving = true;
				} catch (IntentSender.SendIntentException e) {
					mIsResolving = false;
					mGoogleApiClient.connect();
				}
			} else {
				CustomerUtils.alertbox(TIFFEAT, "Connection with Google failed!!", activity);
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		msignedInClicked = false;
		Toast.makeText(activity, "Login successful", Toast.LENGTH_SHORT).show();
		new GoogleAccessToken(fragmentActivity, mGoogleApiClient, customerOrder, RC_SIGN_IN).execute();
	}

	@Override
	public void onConnectionSuspended(int arg0) {

	}

	public void onResult(int requestCode, int resultCode) {
		if (requestCode == RC_SIGN_IN) {
			if (resultCode != RESULT_OK) {
				msignedInClicked = false;
			}
			mIsResolving = false;
			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}

		} else
			Toast.makeText(activity, "Login failed", Toast.LENGTH_SHORT).show();
	}

}
