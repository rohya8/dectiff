package com.rns.tiffeat.mobile;

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
import com.rns.tiffeat.web.bo.domain.Customer;

public class ScheduledOrderHomeScreen extends Fragment implements AndroidConstants {

	private Customer customer;
	private View view;
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
		view = inflater.inflate(R.layout.activity_scheduled_user, container, false);

		if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
		} else {
			initialise();
		}
		return view;
	}

	private void initialise() {

		scheduledOrdersListView = (ListView) view.findViewById(R.id.scheduled_homescreen_scheduledorderlist);
		wallet = (TextView) view.findViewById(R.id.scheduled_homescreen_textview_Wallet);

		if (customer.getBalance() != null)
			wallet.setText(customer.getBalance().toString());

		if (CollectionUtils.isEmpty(customer.getScheduledOrder()))
			scheduledOrdersAdapter.setScheduledOrders(customer.getScheduledOrder());

		scheduledOrdersAdapter = new ScheduledOrderListAdapter(getActivity(), R.layout.activity_scheduled_orders_adapter, customer.getScheduledOrder(),
				customer);

		scheduledOrdersAdapter.setScheduledUserHome(this);
		scheduledOrdersListView.setAdapter(scheduledOrdersAdapter);

	}

	@Override
	public void onResume() {
		super.onResume();
		new GetCurrentCustomerAsyncTask(getActivity(), this).execute();
	}

	public void prepareScreen() {
		initialise();
	}

}