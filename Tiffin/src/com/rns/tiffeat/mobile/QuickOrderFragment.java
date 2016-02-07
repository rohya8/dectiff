package com.rns.tiffeat.mobile;

import java.util.Date;
import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.rns.tiffeat.mobile.asynctask.ValidateQuickOrderAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealType;
import com.rns.tiffeat.web.bo.domain.PaymentType;

public class QuickOrderFragment extends Fragment implements OnClickListener, AndroidConstants {

	private RadioButton codpayment, onlinepayment;
	private EditText address, phone;
	private static int count = 1;
	private Button proceed, plus, minus;
	private CustomerOrder customerOrder;
	private EditText tiffintitle, name, emailid, amount, quantity;
	private View rootView;
	Context context;

	public QuickOrderFragment(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_quickorder, container, false);

		if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
		} else {
			initialise();
			proceed.setOnClickListener(this);
			codpayment.setOnClickListener(this);
			onlinepayment.setOnClickListener(this);
			plus.setOnClickListener(this);
			minus.setOnClickListener(this);
		}
		return rootView;

	}

	private void initialise() {
		codpayment = (RadioButton) rootView.findViewById(R.id.quickorder_screen_cashondelivery_radiobutton);
		onlinepayment = (RadioButton) rootView.findViewById(R.id.quickorder_screen_onlinepayment_radiobutton);
		quantity = (EditText) rootView.findViewById(R.id.quickorder_screen_textview_quantity);
		address = (EditText) rootView.findViewById(R.id.quickorder_screen_lunchaddress_edittext);
		tiffintitle = (EditText) rootView.findViewById(R.id.quickorder_screen_tiffintitle_edittext);
		name = (EditText) rootView.findViewById(R.id.quickorder_screen_name_edittext);
		emailid = (EditText) rootView.findViewById(R.id.quickorder_screen_email_edittext);
		phone = (EditText) rootView.findViewById(R.id.quickorder_screen_phoneno_edittext);
		amount = (EditText) rootView.findViewById(R.id.quickorder_screen_price_edittext);
		proceed = (Button) rootView.findViewById(R.id.quickorder_screen_proceed_button);
		plus = (Button) rootView.findViewById(R.id.quickorder_screen_quantity_plus_button);
		minus = (Button) rootView.findViewById(R.id.quickorder_screen_quantity_minus_button);

		customerData();
		// getMealDate(availableMealType);
	}

	private void customerData() {

		tiffintitle.setText(customerOrder.getMeal().getTitle());
		name.setText(customerOrder.getCustomer().getName());
		emailid.setText(customerOrder.getCustomer().getEmail());
		quantity.setText(String.valueOf(count));
		//TODO: Show Meal type
		if (customerOrder.getAddress() != null)
			address.setText(customerOrder.getAddress());
		else
			address.setHint("Enter Address");

		if (customerOrder.getCustomer().getPhone() != null)
			phone.setText(customerOrder.getCustomer().getPhone());
		else
			phone.setHint("Enter Phone Number");

		amount.setText(customerOrder.getMeal().getPrice().toString());

	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {

		case R.id.quickorder_screen_proceed_button:

			if (!Validation.isNetworkAvailable(getActivity())) {
				Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
			} else {

				InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

				if (TextUtils.isEmpty(address.getText())) {
					CustomerUtils.alertbox(TIFFEAT, " Do not Leave Empty Field ", getActivity());
				} else if (TextUtils.isEmpty(address.getText()) || address.getText().toString().length() <= 8) {
					CustomerUtils.alertbox(TIFFEAT, " Enter Valid Address ", getActivity());
				} else if (codpayment.isChecked() == false && onlinepayment.isChecked() == false) {
					CustomerUtils.alertbox(TIFFEAT, " Select A Payment Method ", getActivity());
				} else if (!Validation.isPhoneNumber(phone, true)) {
					CustomerUtils.alertbox(TIFFEAT, " Enter Valid Phone number ", getActivity());
				} else {
					prepareCustomerOrder();
					alertbox();
				}
			}
			break;

		case R.id.quickorder_screen_cashondelivery_radiobutton:
			onlinepayment.setChecked(false);
			customerOrder.setPaymentType(PaymentType.CASH);
			break;

		case R.id.quickorder_screen_onlinepayment_radiobutton:
			codpayment.setChecked(false);
			customerOrder.setPaymentType(PaymentType.NETBANKING);
			break;

		case R.id.quickorder_screen_quantity_plus_button:
			if (count < 5) {
				count++;
				quantity.setText(String.valueOf(count));
				amount.setText("" + cost());
			}
			break;

		case R.id.quickorder_screen_quantity_minus_button:
			if (count > 1) {
				count--;
				quantity.setText(String.valueOf(count));
				amount.setText("" + cost());
			}
			break;
		default:
			break;
		}

	}

	private float cost() {
		float cnt = Float.parseFloat(customerOrder.getMeal().getPrice().toString());
		float quant = Float.parseFloat(quantity.getText().toString());
		return cnt * quant;
	}

	private void prepareCustomerOrder() {
		customerOrder.getCustomer().setPhone(phone.getText().toString());
		customerOrder.setAddress(address.getText().toString());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), FONT);
		fontChanger.replaceFonts((ViewGroup) this.getView());
	}

	private void alertbox() {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
		builder.setTitle("TiffEat");
		builder.setMessage("Do you want to proceed ?");

		builder.setNegativeButton("No", null);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!Validation.isNetworkAvailable(getActivity())) {
					Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
				} else {
					new ValidateQuickOrderAsyncTask(getActivity(), customerOrder).execute();
				}
			}
		});

		builder.show();
	}

}
