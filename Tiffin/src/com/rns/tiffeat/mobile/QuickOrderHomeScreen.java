package com.rns.tiffeat.mobile;

import org.apache.commons.collections.CollectionUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rns.tiffeat.mobile.adapter.QuickOrderListAdapter;
import com.rns.tiffeat.mobile.asynctask.GetCurrentCustomerAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class QuickOrderHomeScreen extends Fragment implements AndroidConstants {
	private ListView todaylistview;
	private Customer customer;
	private Button neworder;
	private CustomerOrder customerOrder;
	private TextView welcomeText;
	private QuickOrderListAdapter quickOrdersAdapter;
	private View view;
	private RelativeLayout relativeLayout;

	public QuickOrderHomeScreen(Customer currentCustomer) {
		this.customer = currentCustomer;
	}

	public void setCustomer(Customer customer) 
	{
		this.customer = customer;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_quickorder_homescreen, container, false);
		RateUs.app_launched(getActivity());
		
		
		customerOrder = new CustomerOrder();
		initialise();
		neworder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				customerOrder.setCustomer(customer);
				newActivity(customerOrder);
			}
		});

		prepareScreen();
		return view;
	}

	private void initialise() {
		if (customer == null) {
			return;
		}

		todaylistview = (ListView) view.findViewById(R.id.quickorder_homescreen_listView);
		neworder = (Button) view.findViewById(R.id.quickorder_homescreen_button);
		relativeLayout = (RelativeLayout) view.findViewById(R.id.quickorder_homescreen_layout);
		welcomeText = (TextView) view.findViewById(R.id.quickorder_homescreen_textView);
		relativeLayout.setVisibility(View.VISIBLE);

		if (customer.getQuickOrders() != null) {
			quickOrdersAdapter = new QuickOrderListAdapter(getActivity(), R.layout.activity_quickorder_list_adapter, customer.getQuickOrders(), customer);
		}
		todaylistview.setFooterDividersEnabled(true);

	}

	public void prepareScreen() {

		initialise();
		quickOrdersAdapter.setQuickHome(this);
		if (CollectionUtils.isEmpty(customer.getQuickOrders())) {
			welcomeText.setText("You have no orders today..");
			relativeLayout.setVisibility(View.GONE);
			return;
		}

		welcomeText.setText("Todays Orders");
		quickOrdersAdapter = new QuickOrderListAdapter(getActivity(), R.layout.activity_quickorder_list_adapter, customer.getQuickOrders(), customer);
		quickOrdersAdapter.setQuickOrders(customer.getQuickOrders());
		todaylistview.setAdapter(quickOrdersAdapter);
	}

	private void newActivity(CustomerOrder customerOrder2) {
		Fragment fragment = null;
		fragment = new NewFirstTimeScreen(customerOrder);
		CustomerUtils.nextFragment(fragment, getFragmentManager(), false);
	}

	@Override
	public void onResume() {
		super.onResume();
		new GetCurrentCustomerAsyncTask(getActivity(), this).execute();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), FONT);
		fontChanger.replaceFonts((ViewGroup) this.getView());
	}

}