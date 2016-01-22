package com.rns.tiffeat.mobile;

import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Validation {

	private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String PHONE_REGEX = "\\d{10}";
	private static final String Name_REGEX = "[A-Z][a-z]+( [A-Z][a-z]+)";

	private static final String REQUIRED_MSG = "Field can not be blank";
	private static final String EMAIL_MSG = "Invalid email";
	private static final String PHONE_MSG = "Contact number should be of 10 digits";
	private static final String NAME_MSG = "Invalid Name";
	private static final String CODE_MSG = "Invalid Code";

	public static boolean isEmailAddress(EditText editText, boolean required) {
		return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required);
	}

	public static boolean isPhoneNumber(EditText editText, boolean required) {
		return isValid(editText, PHONE_REGEX, PHONE_MSG, required);
	}

	public static boolean isName(EditText editText, boolean required) {
		return isValid(editText, Name_REGEX, NAME_MSG, required);
	}

	public static boolean isVerificationCode(EditText verification, boolean required) {
		return isValid(verification, PHONE_REGEX, CODE_MSG, required);

	}

	public static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {

		String text = editText.getText().toString().trim();
		editText.setError(null);

		if (required && !hasText(editText))
			return false;

		if (required && !Pattern.matches(regex, text)) {
			editText.setError(errMsg);
			return false;
		}
		;

		return true;
	}

	public static void showError(Context context, String title) {
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.networkconnection);
		dialog.setTitle(title);
		dialog.setCancelable(false);
		Button dialogButton = (Button) dialog.findViewById(R.id.check_network_button);
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
				
			}
		});

		dialog.show();

	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager obj = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo objInfo = obj.getActiveNetworkInfo();
		return objInfo != null && objInfo.isConnected();
	}

	public static boolean hasText(EditText editText) {

		String text = editText.getText().toString().trim();
		editText.setError(null);

		if (text.length() == 0) {
			editText.setError(REQUIRED_MSG);
			return false;
		}

		return true;
	}

}
