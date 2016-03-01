package com.rns.tiffeat.mobile;

import java.math.BigDecimal;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
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
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealFormat;

public class WalletFragment extends Fragment implements AndroidConstants {

	private View rootView;
	private Button addAmt, addLater;
	private CustomerOrder customerOrder;
	private EditText balanceEditText;
	private TextView currentBalance;
	private BigDecimal balance;
	private boolean isOrderInProcess;

	public WalletFragment(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public void setOrderInProcess(boolean isOrderInProcess) {
		this.isOrderInProcess = isOrderInProcess;
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
		currentBalance.setText("Current Balance : Rs. 0");

		if (customerOrder.getCustomer().getBalance() != null) {
			if (BigDecimal.TEN.compareTo(customerOrder.getCustomer().getBalance()) < 0) {
				addLater.setVisibility(View.VISIBLE);
			}
			currentBalance.setText("Balance : Rs. " + customerOrder.getCustomer().getBalance().toString());
		}

		addAmt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

				String balance = balanceEditText.getText().toString();

				if (balance.equals("") || balance.length() == 0 || !balance.matches("[0-9]+")) {
					CustomerUtils.alertbox(TIFFEAT, "Invalid amount!", getActivity());
					return;
				}
				CustomerOrder order = prepareCustomerOrder(balance);
				if (!isOrderInProcess) {
					order.setMeal(null);
				}
				nextActivity(order);
			}

			private CustomerOrder prepareCustomerOrder(String balance) {
				CustomerOrder order = new CustomerOrder();
				BigDecimal balanceAmount = new BigDecimal(balance);
				order.setAddress(customerOrder.getAddress());
				order.setLocation(customerOrder.getLocation());
				order.setMeal(customerOrder.getMeal());
				order.setMealType(customerOrder.getMealType());
				order.setMealFormat(MealFormat.SCHEDULED);
				order.setDate(customerOrder.getDate());
				Log.d(TIFFEAT, "Date set is :" + order.getDate());
				order.setPrice(customerOrder.getPrice());
				prepareCurrentCustomer(order, balanceAmount);
				return order;
			}

			private void prepareCurrentCustomer(CustomerOrder order, BigDecimal balanceAmount) {
				Customer currentCustomer = new Customer();
				Customer customer = customerOrder.getCustomer();
				currentCustomer.setId(customer.getId());
				currentCustomer.setEmail(customer.getEmail());
				currentCustomer.setPhone(customer.getPhone());
				currentCustomer.setName(customer.getName());
				currentCustomer.setBalance(balanceAmount);
				order.setCustomer(currentCustomer);
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

	private void nextActivity(CustomerOrder order) {
		Fragment fragment = new PaymentGatewayFragment(order);
		CustomerUtils.nextFragment(fragment, getFragmentManager(), false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), FONT);
		fontChanger.replaceFonts((ViewGroup) this.getView());
	}
}
