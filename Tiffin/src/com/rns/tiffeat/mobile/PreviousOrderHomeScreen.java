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

import com.rns.tiffeat.mobile.adapter.PreviousOrderListAdapter;
import com.rns.tiffeat.mobile.asynctask.GetCurrentCustomerAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class PreviousOrderHomeScreen extends Fragment implements AndroidConstants {

	private View rootView;
	private Customer customer;
	private ListView previouslistview;
	private Button neworder;
	private TextView previousText;
	private CustomerOrder customerOrder;
	private PreviousOrderListAdapter previousOrderAdapter;
	private RelativeLayout relativeLayout;

	public PreviousOrderHomeScreen(Customer customer) {
		this.customer = customer;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_previous_homescreen, container, false);
		customerOrder = new CustomerOrder();

		if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
		} else {

			initialise();

			neworder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (!Validation.isNetworkAvailable(getActivity())) {
						Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
					} else {
						customerOrder.setCustomer(customer);
						newActivity(customerOrder);
					}
				}
			});
			prepareScreen();
		}
		return rootView;
	}

	private void initialise() {

		if(customer == null) {
			return;
		}
		
		previouslistview = (ListView) rootView.findViewById(R.id.previousorder_homescreen_listView);
		neworder = (Button) rootView.findViewById(R.id.previousorder_homescreen_neworder_button);
		previousText = (TextView) rootView.findViewById(R.id.previousorder_homescreen_textView);
		relativeLayout = (RelativeLayout) rootView.findViewById(R.id.previousorder_homescreen_layout);
		relativeLayout.setVisibility(View.VISIBLE);

		if (CollectionUtils.isEmpty(customer.getPreviousOrders())) {
			relativeLayout.setVisibility(View.GONE);
			previousText.setText("Sorry " + customer.getName() + " you have not order today");
			relativeLayout.setVisibility(View.GONE);

		} else if (customer.getPreviousOrders() != null) {
			previousOrderAdapter = new PreviousOrderListAdapter(getActivity(), R.layout.activity_previousorder_list_adapter, customer.getPreviousOrders(),
					customer);
		}

		previouslistview.setFooterDividersEnabled(true);
	}

	private void newActivity(CustomerOrder customerOrder2) {
		Fragment fragment = null;
		fragment = new NewFirstTimeScreen(customerOrder);
		CustomerUtils.nextFragment(fragment, getFragmentManager(), false);
	}

	public void prepareScreen() {

		previousOrderAdapter.setQuickHome(this);

		if (CollectionUtils.isEmpty(previousOrderAdapter.getPreviousOrders())) {
			previousOrderAdapter.setPreviousOrders(customer.getPreviousOrders());
			previouslistview.setAdapter(previousOrderAdapter);
		} else
			previouslistview.setAdapter(previousOrderAdapter);

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

	public void setCustomer(Customer result) {
		this.customer = result;
	}

}
