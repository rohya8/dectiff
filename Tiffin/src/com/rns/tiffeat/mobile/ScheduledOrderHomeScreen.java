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
import com.rns.tiffeat.web.bo.domain.Meal;

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

		if (customer.getBalance() == null || customer.getBalance().compareTo(BigDecimal.TEN) < 0) {
			CustomerUtils.alertbox(TIFFEAT, "Your balance is low", getActivity());
			// if (!CollectionUtils.isEmpty(customer.getScheduledOrder()))
			// {
			// CustomerOrder customerOrder=new CustomerOrder();
			// customerOrder= customer.getScheduledOrder().get(0);
			// customerOrder.setMeal(new Meal());
			// customerOrder.setCustomer(customer);
			// Fragment fobj = new WalletFragment(customerOrder);
			// CustomerUtils.nextFragment(fobj,getFragmentManager() , false);
			// }
		}

		if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
		} else {
			initialise();
		}
		return rootview;
	}

	private void initialise() {

		scheduledOrdersListView = (ListView) rootview.findViewById(R.id.scheduled_homescreen_scheduledorderlist);
		wallet = (TextView) rootview.findViewById(R.id.scheduled_homescreen_textview_Wallet);

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