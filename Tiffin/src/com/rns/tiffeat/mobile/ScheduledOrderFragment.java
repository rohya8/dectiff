package com.rns.tiffeat.mobile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.rns.tiffeat.mobile.asynctask.ScheduledOrderAsyncTask;
import com.rns.tiffeat.mobile.asynctask.ValidateScheduledOrderAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class ScheduledOrderFragment extends Fragment implements OnClickListener, AndroidConstants {

	private EditText lunchaddr, phone, mealtype;
	private CustomerOrder customerOrder;
	private EditText tiffindesc, name, emailid;
	private View rootView;
	private Button proceed;

	public ScheduledOrderFragment(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_scheduledorder, container, false);

		if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
		} else {

			initialise();
			proceed.setOnClickListener(this);

		}
		return rootView;
	}

	private void initialise() {

		lunchaddr = (EditText) rootView.findViewById(R.id.scheduled_order_editText_LunchAddress);
		mealtype = (EditText) rootView.findViewById(R.id.scheduled_order_mealtype_editText);
		tiffindesc = (EditText) rootView.findViewById(R.id.scheduled_order_editText_TiffinName);
		name = (EditText) rootView.findViewById(R.id.scheduled_order_editText_Name);
		emailid = (EditText) rootView.findViewById(R.id.scheduled_order_editText_Email);
		phone = (EditText) rootView.findViewById(R.id.scheduled_order_editText_Phoneno);
		proceed = (Button) rootView.findViewById(R.id.scheduled_order_proceed_button);

		customerData();
	}

	private void customerData() {

		if (customerOrder.getMeal() == null || customerOrder.getCustomer() == null) {
			return;
		}
		tiffindesc.setText(customerOrder.getMeal().getTitle());
		name.setText(customerOrder.getCustomer().getName());
		emailid.setText(customerOrder.getCustomer().getEmail());
		lunchaddr.setVisibility(View.VISIBLE);
		lunchaddr.setHint("Enter Address");

		if (customerOrder.getMealType() != null)
			mealtype.setText(customerOrder.getMealType().toString());

		if (customerOrder.getAddress() != null)
			lunchaddr.setText(customerOrder.getAddress());
		else
			lunchaddr.setHint("Enter Address");

		if (customerOrder.getCustomer().getPhone() != null)
			phone.setText(customerOrder.getCustomer().getPhone());
		else
			phone.setHint("Enter Phone Number");

	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {

		case R.id.scheduled_order_proceed_button:

			if (!Validation.isNetworkAvailable(getActivity())) {
				Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
			} else {
				if (lunchaddr.getText().toString().equals(""))
					CustomerUtils.alertbox(TIFFEAT, " Do not Leave Empty Field ", getActivity());
				else if (lunchaddr.getText().toString().length() <= 8)
					CustomerUtils.alertbox(TIFFEAT, " Enter Valid Address ", getActivity());
				else if (!Validation.isPhoneNumber(phone, true))
					CustomerUtils.alertbox(TIFFEAT, " Enter Valid Phone number ", getActivity());
				else {

					customerOrder.setAddress(lunchaddr.getText().toString());
					customerOrder.getCustomer().setPhone(phone.getText().toString());

					alertbox();
				}
			}
			break;

		default:
			break;
		}

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
					if (customerOrder.getCustomer().getBalance() == null
							|| customerOrder.getCustomer().getBalance().compareTo(customerOrder.getMeal().getPrice()) < 0)
						new ValidateScheduledOrderAsyncTask(getActivity(), customerOrder).execute();
					else
						new ScheduledOrderAsyncTask(getActivity(), customerOrder).execute();

				}
			}
		});

		builder.show();
	}
}
