package com.rns.tiffeat.mobile;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.rns.tiffeat.mobile.adapter.ScheduledOrderListAdapter;
import com.rns.tiffeat.mobile.asynctask.GetCurrentCustomerAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealFormat;
import com.rns.tiffeat.web.bo.domain.MealType;

public class ScheduledOrderHomeScreen extends Fragment implements AndroidConstants {

	private Customer customer;
	private View rootview;
	private TextView wallet;
	private boolean addLater;
	private ScheduledOrderListAdapter scheduledOrdersAdapter;
	private ListView scheduledOrdersListView;
	private TextView header;
	private Button addLunch;
	private Button addDinner;

	public void setAddLater(boolean addLater) {
		this.addLater = addLater;
	}

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

		if (lowOnCash() && !addLater) {
			CustomerUtils.alertbox(TIFFEAT, "Your balance is low. Please add Money to your wallet .. ", getActivity());
			CustomerOrder customerOrder = new CustomerOrder();
			customerOrder.setMealFormat(MealFormat.SCHEDULED);
			customerOrder.setCustomer(customer);
			Fragment fobj = new WalletFragment(customerOrder);
			CustomerUtils.nextFragment(fobj, getFragmentManager(), false);
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
		header = (TextView) rootview.findViewById(R.id.scheduled_homescreen_textview_header);
		addLunch = (Button) rootview.findViewById(R.id.scheduled_homescreen_button_add_lunch);
		addDinner = (Button) rootview.findViewById(R.id.scheduled_homescreen_button_add_dinner);

		addLunch.setVisibility(View.GONE);
		addDinner.setVisibility(View.GONE);

		if (customer.getScheduledOrder().size() < 1) {
			header.setText("You don't have any Daily Tiffins yet ..");
			addLunch.setVisibility(View.VISIBLE);
			addDinner.setVisibility(View.VISIBLE);
		} else {
			header.setText("Your Daily Tiffins");
		}

		if (customer.getBalance() != null)
			wallet.setText(customer.getBalance().toString());

		addLunch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Fragment fragment = null;
				fragment = new NewScheduleLunchOrDinnerFragment(prepareCustomerOrder(MealType.LUNCH));
				CustomerUtils.nextFragment(fragment, getFragmentManager(), true);
			}
		});

		addDinner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Fragment fragment = null;
				fragment = new NewScheduleLunchOrDinnerFragment(prepareCustomerOrder(MealType.DINNER));
				CustomerUtils.nextFragment(fragment, getFragmentManager(), true);
			}
		});

		scheduledOrdersAdapter = new ScheduledOrderListAdapter(getActivity(), R.layout.activity_scheduled_orders_adapter, customer.getScheduledOrder(), customer);
		if (!CollectionUtils.isEmpty(customer.getScheduledOrder())) {
			scheduledOrdersAdapter.setScheduledOrders(customer.getScheduledOrder());
		}
		scheduledOrdersAdapter.setScheduledUserHome(this);
		scheduledOrdersListView.setAdapter(scheduledOrdersAdapter);

	}

	public CustomerOrder prepareCustomerOrder(MealType mealType) {
		CustomerOrder order = new CustomerOrder();
		order.setMealFormat(MealFormat.SCHEDULED);
		order.setDate(new Date());
		order.setCustomer(customer);
		order.setMealType(mealType);
		return order;
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