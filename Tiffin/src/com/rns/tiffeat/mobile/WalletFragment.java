package com.rns.tiffeat.mobile;

import java.math.BigDecimal;

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
import android.widget.TextView;

import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class WalletFragment extends Fragment implements AndroidConstants {

	private View rootView;
	private Button addAmt, addLater;
	private CustomerOrder customerOrder;
	private EditText balanceEditText;
	private TextView currentBalance;

	public WalletFragment(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_addtowallet, container, false);

		addAmt = (Button) rootView.findViewById(R.id.add_amount_button);
		addLater = (Button) rootView.findViewById(R.id.add_later_button);
		balanceEditText = (EditText) rootView.findViewById(R.id.add_amount_edittext);
		currentBalance = (TextView) rootView.findViewById(R.id.current_balance_textview);
		currentBalance.setText("Balance : Rs. 0");
		
		if (customerOrder.getCustomer().getBalance() != null) {
			if (BigDecimal.TEN.compareTo(customerOrder.getCustomer().getBalance()) < 0) {
				addLater.setVisibility(View.VISIBLE);
			}
			currentBalance.setText("Balance : Rs. "  + customerOrder.getCustomer().getBalance().toString());
		}

		addAmt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!Validation.isNetworkAvailable(getActivity())) {
					Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
				} else {
					InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

					String balance = balanceEditText.getText().toString();

					if (balance.equals("") || balance.length() == 0 || !balance.matches("[0-9]+")) {
						CustomerUtils.alertbox(TIFFEAT, "Invalid amount!", getActivity());
						return;
					}
					customerOrder.getCustomer().setBalance(new BigDecimal(balance));
					nextActivity();

				}
			}

		});

		addLater.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ScheduledOrderHomeScreen fragment = new ScheduledOrderHomeScreen(customerOrder.getCustomer());
				fragment.setAddLater(true);
				CustomerUtils.nextFragment(fragment, getFragmentManager(), false);
			}
		});

		return rootView;
	}

	private void nextActivity() {
		Fragment fragment = new PaymentGatewayFragment(customerOrder);
		CustomerUtils.nextFragment(fragment, getFragmentManager(), false);
	}

}
