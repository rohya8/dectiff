package com.rns.tiffeat.mobile;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rns.tiffeat.mobile.asynctask.RegistrationTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class UserRegistration extends Fragment implements AndroidConstants {

	// //////////// Deviceid code remaining

	private Button submit;
	private EditText emailid, phone, name, password, confirmpass;
	private	String registerpersonName, registerpassword, registeremailid, registerphone, registerconfirmpass, registerdeviceid = "1234";
	private Customer customer;
	private CustomerOrder customerOrder;
	private View view;

	public UserRegistration(CustomerOrder customerOrder2) {
		customerOrder = customerOrder2;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_registration, container, false);

		if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
		} else {

			initialise();

			submit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (!Validation.isNetworkAvailable(getActivity())) {
						Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
					} else {
						try {
							InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
							inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

							if (checkValidation()) {
								getDetails();

								customer.setDeviceId(registerdeviceid);
								customer.setEmail(registeremailid);
								customer.setName(registerpersonName);
								customer.setPassword(registerpassword);
								customer.setPhone(registerphone);
								customerOrder.setCustomer(customer);

								if (!confirmpass.getText().toString().equals(password.getText().toString())) {
									confirmpass.setError("Password Do Not Match");
									Toast.makeText(getActivity(), "Password do not match", Toast.LENGTH_SHORT).show();
								} else
									new RegistrationTask(getActivity(), customerOrder).execute();
							}
						} catch (Exception e) {
							Toast.makeText(getActivity(), "Enter valid credentials", Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
		}
		return view;
	}

	private void initialise() {

		emailid = (EditText) view.findViewById(R.id.registration_emailid_editText);
		phone = (EditText) view.findViewById(R.id.registration_verification_phno_editText);
		name = (EditText) view.findViewById(R.id.registration_name_editText);
		confirmpass = (EditText) view.findViewById(R.id.registration_confirmpassword_editText);
		password = (EditText) view.findViewById(R.id.registration_password_editText);

		submit = (Button) view.findViewById(R.id.registration_Loginbutton);
		submit.setBackgroundColor(Color.parseColor("#8bc3fa"));

		customer = new Customer();
	}

	private boolean checkValidation() {

		boolean ret = true;

		if (!Validation.isName(name, true))
			ret = false;
		if (!Validation.isEmailAddress(emailid, true))
			ret = false;
		if (!Validation.isPhoneNumber(phone, true))
			ret = false;
		if (!Validation.hasText(emailid))
			ret = false;
		if (!Validation.hasText(name))
			ret = false;
		if (!Validation.hasText(password))
			ret = false;
		if (!Validation.hasText(confirmpass))
			ret = false;
		if (!Validation.hasText(phone))
			ret = false;

		return ret;
	}

	// private boolean checkVerification()
	// {
	// boolean ret = true;
	// if (!Validation.isVerificationCode(verification,true)) ret=false;
	// return ret;
	// }

	// private void sendSMS(String phoneNumber, String message)
	// {
	// SmsManager sms = SmsManager.getDefault();
	// sms.sendTextMessage(phoneNumber, null, message, null, null);
	// }

	// public String verification()
	// {
	//
	// int randomPIN = (int)(Math.random()*9000)+1000;
	//
	// //Store integer in a string
	// String PINString = String.valueOf(randomPIN);
	// return(PINString);
	//
	// }

	private void getDetails() {
		registeremailid = emailid.getText().toString();
		registerphone = phone.getText().toString();
		registerpersonName = name.getText().toString();
		registerconfirmpass = confirmpass.getText().toString();
		registerpassword = password.getText().toString();
		registerdeviceid = "1234";
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), FONT);
		fontChanger.replaceFonts((ViewGroup) this.getView());
	}
}