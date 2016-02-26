package com.rns.tiffeat.mobile;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.rns.tiffeat.mobile.asynctask.LoginAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class UserRegistration extends Fragment implements AndroidConstants {

	private Button submit;
	private EditText emailid, name, password, confirmpass;
	private String registerpersonName, registerpassword, registeremailid;
	private Customer customer;
	private CustomerOrder customerOrder;
	private View rootview;
	private Context context;

	public UserRegistration(CustomerOrder customerOrder2) {
		this.customerOrder = customerOrder2;
	}

	public UserRegistration(CustomerOrder customerOrder2, Context applicationContext) {
		this.customerOrder = customerOrder2;
		this.context = applicationContext;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootview = inflater.inflate(R.layout.fragment_userregistration, container, false);

		initialise();

		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
				InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
				if (checkValidation()) {
					getDetails();
					customer.setEmail(registeremailid);
					customer.setName(registerpersonName);
					customer.setPassword(registerpassword);
					customerOrder.setCustomer(customer);

					if (!confirmpass.getText().toString().equals(password.getText().toString())) {
						confirmpass.setError("Password Do Not Match");
						CustomerUtils.alertbox(TIFFEAT, "Password do not match", getActivity());
						return;
					}
					new LoginAsyncTask(getActivity(), customerOrder, REGISTRATION_FRAGMENT).execute();
				}
			}
		});
		return rootview;
	}

	private void initialise() {

		emailid = (EditText) rootview.findViewById(R.id.registration_emailid_editText);

		name = (EditText) rootview.findViewById(R.id.registration_name_editText);
		confirmpass = (EditText) rootview.findViewById(R.id.registration_confirmpassword_editText);
		password = (EditText) rootview.findViewById(R.id.registration_password_editText);

		submit = (Button) rootview.findViewById(R.id.registration_Loginbutton);

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
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), FONT);
		fontChanger.replaceFonts((ViewGroup) this.getView());
	}
}