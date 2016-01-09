package com.rns.tiffeat.mobile;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.rns.tiffeat.mobile.asynctask.RegistrationTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class UserRegistration extends Fragment implements AndroidConstants {

	private Button submit;
	private EditText emailid, name, password, confirmpass;
	private String registerpersonName, registerpassword, registeremailid, registerdeviceid;
	private Customer customer;
	private CustomerOrder customerOrder;
	private View view;
	private TelephonyManager tm;

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

							tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

							InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
							inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

							if (checkValidation()) {
								getDetails();

								customer.setDeviceId(registerdeviceid);
								customer.setEmail(registeremailid);
								customer.setName(registerpersonName);
								customer.setPassword(registerpassword);

								customerOrder.setCustomer(customer);

								if (!confirmpass.getText().toString().equals(password.getText().toString())) {
									confirmpass.setError("Password Do Not Match");
									CustomerUtils.alertbox(TIFFEAT, "Password do not match", getActivity());
								} else
									new RegistrationTask(getActivity(), customerOrder).execute();
							}
						} catch (Exception e) {
							CustomerUtils.alertbox(TIFFEAT, "Enter valid credentials", getActivity());
							CustomerUtils.exceptionOccurred(e.getMessage(), getClass().getSimpleName());
						}
					}
				}
			});
		}
		return view;
	}

	private void initialise() {

		emailid = (EditText) view.findViewById(R.id.registration_emailid_editText);

		name = (EditText) view.findViewById(R.id.registration_name_editText);
		confirmpass = (EditText) view.findViewById(R.id.registration_confirmpassword_editText);
		password = (EditText) view.findViewById(R.id.registration_password_editText);

		submit = (Button) view.findViewById(R.id.registration_Loginbutton);
		submit.setBackgroundColor(Color.parseColor("#8bc3fa"));

		customer = new Customer();
	}

	private boolean checkValidation() {

		if (!Validation.isName(name, true))
			return false;
		if (!Validation.isEmailAddress(emailid, true))
			return false;
		if (!Validation.hasText(emailid))
			return false;
		if (!Validation.hasText(name))
			return false;
		if (!Validation.hasText(password))
			return false;
		if (!Validation.hasText(confirmpass))
			return false;

		return true;
	}

	private void getDetails() {
		registeremailid = emailid.getText().toString();
		registerpersonName = name.getText().toString();
		registerpassword = password.getText().toString();
		registerdeviceid = imeino();
	}

	private String imeino() {

		String IMEI = tm.getDeviceId();
		if (IMEI != null)
			return IMEI;
		else
			return "No Device ID Found";
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), FONT);
		fontChanger.replaceFonts((ViewGroup) this.getView());
	}
}