package com.rns.tiffeat.mobile;

import java.math.BigDecimal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class WalletFragment extends Fragment implements AndroidConstants {

	private View rootView;
	private Button addAmt, addLater;
	private CustomerOrder customerOrder;
	private EditText balanceEditText;

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

		addAmt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!Validation.isNetworkAvailable(getActivity())) {
					Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
				} else {
					if (balanceEditText.getText().toString().equals("") || balanceEditText.getText().toString().length() == 0) {
						CustomerUtils.alertbox(TIFFEAT, "Invalid amount!", getActivity());
						return;
					}
					customerOrder.getCustomer().setBalance(new BigDecimal(balanceEditText.getText().toString()));

					Fragment fragment = new PaymentGatewayFragment(customerOrder);
					CustomerUtils.nextFragment(fragment, getFragmentManager(), false);

				}
			}
		});

		// TODO: addlater
		// if (customerOrder.getCustomer().getBalance() != null
		// ||
		// customerOrder.getCustomer().getBalance().compareTo(customerOrder.getMeal().getPrice())
		// < 0))
		//

		return rootView;
	}

}
