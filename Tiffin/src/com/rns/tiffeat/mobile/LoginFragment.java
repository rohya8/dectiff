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

public class LoginFragment extends Fragment implements AndroidConstants {

	private Button submit;
	private TextView newuser;
	private View view;
	private Customer customer;
	private EditText email, password;
	private CustomerOrder customerOrder;

	public LoginFragment(CustomerOrder customerOrder2) {
		customerOrder = customerOrder2;
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
}
