package com.rns.tiffeat.mobile;

import java.math.BigDecimal;

import org.apache.commons.collections.CollectionUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.rns.tiffeat.mobile.adapter.ScheduledOrderListAdapter;
import com.rns.tiffeat.mobile.asynctask.GetCurrentCustomerAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class ScheduledOrderHomeScreen extends Fragment implements AndroidConstants {

	private Customer customer;
	private View rootview;
	private TextView wallet;

	private ScheduledOrderListAdapter scheduledOrdersAdapter;
	private ListView scheduledOrdersListView;

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public ScheduledOrderHomeScreen(Customer currentCustomer) {
		this.customer = currentCustomer;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootview = inflater.inflate(R.layout.fragment_scheduledorder_homescreen, container, false);

		if (lowOnCash()) {
			CustomerUtils.alertbox(TIFFEAT, "Your balance is low. Please add Money to your wallet .. " , getActivity());
			CustomerOrder customerOrder = customer.getScheduledOrder().get(0);
			customerOrder.setCustomer(customer);
			Fragment fobj = new WalletFragment(customerOrder);
			CustomerUtils.nextFragment(fobj, getFragmentManager(), false);
		} else if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
		} else {
			initialise();
		}
		return rootview;
	}

	private boolean lowOnCash() {
		if (customer == null || CollectionUtils.isEmpty(customer.getScheduledOrder())) {
			return false;
		}
		if (customer.getBalance() == null) {
			return true;
		}
		BigDecimal total = BigDecimal.ZERO;
		for (CustomerOrder order : customer.getScheduledOrder()) {
			if (order.getMeal() == null) {
				continue;
			}
			total = total.add(order.getMeal().getPrice());
		}
		return customer.getBalance().compareTo(total) < 0;
	}

	private void initialise() {

		if (customer == null) {
			return;
		}

		scheduledOrdersListView = (ListView) rootview.findViewById(R.id.scheduled_homescreen_scheduledorderlist);
		wallet = (TextView) rootview.findViewById(R.id.scheduled_homescreen_textview_Wallet);

		if (customer.getBalance() != null)
			wallet.setText(customer.getBalance().toString());

		scheduledOrdersAdapter = new ScheduledOrderListAdapter(getActivity(), R.layout.activity_scheduled_orders_adapter, customer.getScheduledOrder(), customer);
		if (!CollectionUtils.isEmpty(customer.getScheduledOrder())) {
			scheduledOrdersAdapter.setScheduledOrders(customer.getScheduledOrder());
		}
		scheduledOrdersAdapter.setScheduledUserHome(this);
		scheduledOrdersListView.setAdapter(scheduledOrdersAdapter);

	}

	@Override
	public void onResume() {
		super.onResume();
		new GetCurrentCustomerAsyncTask(getActivity(), ScheduledOrderHomeScreen.this).execute();
	}

	public void prepareScreen() {
		initialise();
	}

}