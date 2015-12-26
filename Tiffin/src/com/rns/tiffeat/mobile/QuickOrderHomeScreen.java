package com.rns.tiffeat.mobile;

import org.apache.commons.collections.CollectionUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rns.tiffeat.mobile.adapter.PreviousOrderListAdapter;
import com.rns.tiffeat.mobile.adapter.QuickOrderListAdapter;
import com.rns.tiffeat.mobile.asynctask.GetCurrentCustomerAsyncTask;
import com.rns.tiffeat.mobile.asynctask.GetNewOrderAreaAsynctask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class QuickOrderHomeScreen extends Fragment implements AndroidConstants {
	private ListView todaylistview, previouslistview;
	private Customer customer;
	private Button neworder, previousorder;
	private CustomerOrder customerOrder;
	private TextView welcomeText, previousText;
	private QuickOrderListAdapter quickOrdersAdapter;
	private PreviousOrderListAdapter previousOrderAdapter;
	private View view;
	private LinearLayout linearLayout;
	private int flag = 0;

	public QuickOrderHomeScreen(Customer currentCustomer) {
		this.customer = currentCustomer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_quick_order_main_homescreen, container, false);

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

			previousorder.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!Validation.isNetworkAvailable(getActivity())) {
						Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
					}

					if (CollectionUtils.isEmpty(customer.getPreviousOrders())) {
						previousText.setText("You Don't Have Any Previous Order ");
					}
					if (flag == 0)
						repeatorderActivity(customer);
					else {
						flag = 0;
						linearLayout.setVisibility(View.GONE);
						previousorder.setText("Show Previous Orders");
						welcomeText.setVisibility(View.VISIBLE);
					}
				}
			});

			prepareScreen();
		}
		return view;
	}

	private void initialise() {
		todaylistview = (ListView) view.findViewById(R.id.quick_order_homescreen_today_order_listView);
		neworder = (Button) view.findViewById(R.id.quick_order_homescreen_neworder_button);
		welcomeText = (TextView) view.findViewById(R.id.quick_order_homescreen_textView);
		previousText = (TextView) view.findViewById(R.id.quick_order_homescreen_previousorder_textView);

		quickOrdersAdapter = new QuickOrderListAdapter(getActivity(), R.layout.activity_quickorder_list_adapter, customer.getQuickOrders(), customer);
		previousOrderAdapter = new PreviousOrderListAdapter(getActivity(), R.layout.activity_previousorder_list_adapter, customer.getPreviousOrders(), customer);

		previousorder = (Button) view.findViewById(R.id.quick_order_homescreen_previousorder_button);
		linearLayout = (LinearLayout) view.findViewById(R.id.quick_order_homescreen_linearlayout);
		previouslistview = (ListView) view.findViewById(R.id.quick_order_homescreen_previous_order_listView);

	}

	public void prepareScreen() {

		welcomeText.setText("Welcome " + customer.getName());
		quickOrdersAdapter.setQuickHome(this);
		previousOrderAdapter.setQuickHome(this);

		if(!CollectionUtils.isEmpty(previousOrderAdapter.getPreviousOrders())) {
			//previousOrderAdapter.clear();
			//previousOrderAdapter.addAll(customer.getPreviousOrders());
			previouslistview.setAdapter(previousOrderAdapter);
		}
		if(!CollectionUtils.isEmpty(quickOrdersAdapter.getQuickOrders())) {
			//quickOrdersAdapter.clear();
			//quickOrdersAdapter.addAll(customer.getQuickOrders());
			todaylistview.setAdapter(quickOrdersAdapter);
		}
	}

	private void newActivity(CustomerOrder customerOrder2) {
		new GetNewOrderAreaAsynctask(getActivity(), customerOrder2).execute();
	}

	@Override
	public void onResume() {
		super.onResume();
		new GetCurrentCustomerAsyncTask(getActivity(), this).execute();
	}

	private void repeatorderActivity(Customer customer2) {

		previousorder.setText("View Today's Order");
		linearLayout.setVisibility(View.VISIBLE);
		flag = 1;
		welcomeText.setVisibility(View.GONE);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), FONT);
		fontChanger.replaceFonts((ViewGroup) this.getView());
	}


}