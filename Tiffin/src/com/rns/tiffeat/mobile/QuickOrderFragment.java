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
import android.widget.TextView;

import com.rns.tiffeat.mobile.asynctask.ValidateQuickOrderAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealType;
import com.rns.tiffeat.web.bo.domain.PaymentType;

public class QuickOrderFragment extends Fragment implements OnClickListener, AndroidConstants {

	private RadioButton lunch, dinner, codpayment, onlinepayment;
	private EditText address, phone;

	private Button proceed;
	private CustomerOrder customerOrder;
	private TextView tiffindesc, name, emailid, amount;
	private View rootView;
	Context context;
	private Map<MealType, Date> availableMealType;

	public QuickOrderFragment(CustomerOrder customerOrder, Map<MealType, Date> availableMealType) {
		this.customerOrder = customerOrder;
		this.availableMealType = availableMealType;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	private void getMealDate(Map<MealType, Date> availableMealType) {

		if (availableMealType.get(MealType.LUNCH) != null) {
			address.setVisibility(View.VISIBLE);
			lunch.setText("Lunch for ( " + CustomerUtils.convertDate(availableMealType.get(MealType.LUNCH)) + " )");
			lunch.setVisibility(View.VISIBLE);
		}
		if (availableMealType.get(MealType.DINNER) != null) {
			address.setVisibility(View.VISIBLE);
			dinner.setText("Dinner for ( " + CustomerUtils.convertDate(availableMealType.get(MealType.DINNER)) + " )");
			dinner.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_quick_order, container, false);

		if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
		} else {
			initialise();
			lunch.setOnClickListener(this);
			dinner.setOnClickListener(this);
			proceed.setOnClickListener(this);
			codpayment.setOnClickListener(this);
			onlinepayment.setOnClickListener(this);
		}
		return rootView;

	}

	private void initialise() {
		lunch = (RadioButton) rootView.findViewById(R.id.quick_order_screen_radioButton_lunch);
		dinner = (RadioButton) rootView.findViewById(R.id.quick_order_screen_radioButton_dinner);
		codpayment = (RadioButton) rootView.findViewById(R.id.quick_order_screen_radioButton_cashondelivery);
		onlinepayment = (RadioButton) rootView.findViewById(R.id.quick_order_screen_radioButton_onlinepayment);

		address = (EditText) rootView.findViewById(R.id.quick_order_screen_editText_LunchAddress);
		tiffindesc = (EditText) rootView.findViewById(R.id.quick_order_screen_editText_TiffinName);
		name = (EditText) rootView.findViewById(R.id.quick_order_screen_editText_Name);
		emailid = (EditText) rootView.findViewById(R.id.quick_order_screen_editText_Email);
		phone = (EditText) rootView.findViewById(R.id.quick_order_screen_editText_Phoneno);
		amount = (EditText) rootView.findViewById(R.id.quick_order_screen_editText_Price);
		proceed = (Button) rootView.findViewById(R.id.quick_order_screen_proceed_button);
		customerData();
		getMealDate(availableMealType);
	}

	private void customerData() {

		tiffindesc.setText(customerOrder.getMeal().getTitle());
		name.setText(customerOrder.getCustomer().getName());
		emailid.setText(customerOrder.getCustomer().getEmail());

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
		case R.id.quick_order_screen_radioButton_lunch:
			dinner.setChecked(false);
			address.setVisibility(View.VISIBLE);
			address.setHint("Lunch Address");
			break;

		case R.id.quick_order_screen_radioButton_dinner:
			lunch.setChecked(false);
			address.setVisibility(View.VISIBLE);
			address.setHint("Dinner Address");
			break;

		case R.id.quick_order_screen_proceed_button:

			if (!Validation.isNetworkAvailable(getActivity())) {
				Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
			} else {

				InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

				if (TextUtils.isEmpty(address.getText())) {
					CustomerUtils.alertbox(TIFFEAT, " Do not Leave Empty Field ", getActivity());
				} else if (!dinner.isChecked() && !lunch.isChecked()) {
					CustomerUtils.alertbox(TIFFEAT, " Select Lunch Or Dinner ", getActivity());
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

		case R.id.quick_order_screen_radioButton_cashondelivery:
			onlinepayment.setChecked(false);
			customerOrder.setPaymentType(PaymentType.CASH);
			break;

		case R.id.quick_order_screen_radioButton_onlinepayment:
			codpayment.setChecked(false);
			customerOrder.setPaymentType(PaymentType.NETBANKING);
			break;

		default:
			break;
		}

	}

	private void prepareCustomerOrder() {
		customerOrder.setMealType(MealType.LUNCH);
		if (dinner.isChecked()) {
			customerOrder.setMealType(MealType.DINNER);
		}
		customerOrder.getCustomer().setPhone(phone.getText().toString());
		customerOrder.setDate(availableMealType.get(customerOrder.getMealType()));
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
