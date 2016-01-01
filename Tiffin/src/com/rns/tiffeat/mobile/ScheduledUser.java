package com.rns.tiffeat.mobile;

import org.apache.commons.collections.CollectionUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.rns.tiffeat.mobile.adapter.ScheduledOrderListAdapter;
import com.rns.tiffeat.mobile.asynctask.GetCurrentCustomerAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class ScheduledUser extends Fragment implements AndroidConstants {

	private Customer customer;
	private View view;
	private CustomerOrder customerOrder;

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
			// checkBalance();
		}
		return view;
	}

	private void initialise() {

		scheduledOrdersListView = (ListView) view.findViewById(R.id.scheduled_user_scheduled_orders_list);

		scheduledOrdersAdapter = new ScheduledOrderListAdapter(getActivity(), R.layout.activity_scheduled_orders_adapter, customer.getScheduledOrder(),
				customer);

		if (CollectionUtils.isEmpty(customer.getScheduledOrder()))
			scheduledOrdersAdapter.setScheduledOrders(customer.getScheduledOrder());

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