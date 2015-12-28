package com.rns.tiffeat.mobile;

import java.math.BigDecimal;

import org.apache.commons.collections.CollectionUtils;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.rns.tiffeat.mobile.adapter.ScheduledOrderListAdapter;
import com.rns.tiffeat.mobile.asynctask.GetCurrentCustomerAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.Meal;
import com.rns.tiffeat.web.bo.domain.MealFormat;
import com.rns.tiffeat.web.bo.domain.OrderStatus;

public class ScheduledUser extends Fragment implements AndroidConstants {

	private Customer customer;
	private View view;
	private CustomerOrder customerOrder;
	private EditText balanceEditText;
	private Dialog addToWalletDialog;
	private ScheduledOrderListAdapter scheduledOrdersAdapter;
	private ListView scheduledOrdersListView;
	
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public ScheduledUser(Customer currentCustomer, boolean showAddToWallet) {
		this.customer = currentCustomer;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_scheduled_user, container, false);

		if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
		} else {
			initialise();
			checkBalance();
		}
		return view;
	}

	private void showWalletDialogbox() {
		customerOrder = new CustomerOrder();

		addToWalletDialog = new Dialog(getActivity());
		addToWalletDialog.setContentView(R.layout.activity_add_to_wallet);
		addToWalletDialog.setTitle("Add money to Wallet");
		addToWalletDialog.setCancelable(false);
		Button dialogAddAmt = (Button) addToWalletDialog.findViewById(R.id.add_amount_button);
		Button dialogAddLater = (Button) addToWalletDialog.findViewById(R.id.add_later_button);

		dialogAddAmt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!Validation.isNetworkAvailable(getActivity())) {
					Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
				} else {
					addToWalletDialog.dismiss();
					customerOrder = new CustomerOrder();
					customerOrder.setMealFormat(MealFormat.SCHEDULED);
					if (balanceEditText.getText().toString().equals("") || balanceEditText.getText().toString().length() == 0) {
						CustomerUtils.alertbox(TIFFEAT, "Invalid amount!", getActivity());
						return;
					}
					customer.setBalance(new BigDecimal(balanceEditText.getText().toString()));
					customerOrder.setCustomer(customer);
					customerOrder.setMeal(new Meal());
					Fragment fobj = new PaymentGatewayFragment(customerOrder);
					getFragmentManager().beginTransaction().replace(R.id.container_body, new PaymentGatewayFragment(customerOrder), "" + fobj).commit();
				}
			}
		});

		dialogAddLater.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				addToWalletDialog.dismiss();
			}
		});

		balanceEditText = (EditText) addToWalletDialog.findViewById(R.id.add_amount_edittext);
		addToWalletDialog.show();

	}

	private void initialise() {

		scheduledOrdersListView = (ListView) view.findViewById(R.id.scheduled_user_scheduled_orders_list);
		
		scheduledOrdersAdapter = new ScheduledOrderListAdapter(getActivity(), R.layout.activity_scheduled_orders_adapter, customer.getScheduledOrder(), customer);
		
		if(CollectionUtils.isEmpty(customer.getScheduledOrder()))
			scheduledOrdersAdapter.setScheduledOrders(customer.getScheduledOrder());
		
		scheduledOrdersAdapter.setScheduledUserHome(this);
		scheduledOrdersListView.setAdapter(scheduledOrdersAdapter);
		
	}

	private void checkBalance() {
		if (isOrderPayable()) {
			showWalletDialogbox();
		} else if (customer.getBalance() != null && BigDecimal.TEN.compareTo(customer.getBalance()) > 0) {
			showWalletDialogbox();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		new GetCurrentCustomerAsyncTask(getActivity(), this).execute();
	}


	private boolean isOrderPayable() {
		if (CollectionUtils.isEmpty(customer.getScheduledOrder())) {
			return false;
		}
		for (CustomerOrder order : customer.getScheduledOrder()) {
			if (OrderStatus.PAYABLE.equals(order.getStatus())) {
				return true;
			}
		}
		return false;
	}

	public void prepareScreen() {
		initialise();
	}

}