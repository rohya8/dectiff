package com.rns.tiffeat.mobile.asynctask;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.rns.tiffeat.mobile.ShowMenuFragment;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class GetMealMenuAsyncTask extends AsyncTask<String, String, Customer> implements AndroidConstants {

	private String quickOrderHome, scheduledUser;
	private FragmentActivity context;
	private Customer customer;
	private CustomerOrder customerOrder;
	private ProgressDialog progressDialog;

	public GetMealMenuAsyncTask(FragmentActivity context, String quickHome, String scheduledUser, CustomerOrder customerOrder2) {
		this.quickOrderHome = quickHome;
		customer = new Customer();
		this.scheduledUser = scheduledUser;
		this.customerOrder = customerOrder2;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = UserUtils.showLoadingDialog(context, "Contacting your vendor", "Getting today's menu....");
	}

	@Override
	protected Customer doInBackground(String... arg0) {

		if (!Validation.isNetworkAvailable(context)) {
			return null;
		}

		try {
			Customer currentCustomer = CustomerUtils.getCurrentCustomer(context);
			Customer latestCustomer = currentCustomer;
			latestCustomer = CustomerServerUtils.getCurrentCustomer(currentCustomer);
			return latestCustomer;
		} catch (Exception e) {
		}

		return null;

	}

	@Override
	protected void onPostExecute(Customer result) {
		super.onPostExecute(result);
		progressDialog.dismiss();

		if (result == null) {
			Validation.showError(context, ERROR_FETCHING_DATA);
			return;
		}
		List<CustomerOrder> Orders = new ArrayList<CustomerOrder>();
		Fragment fragment = null;

		if (quickOrderHome != null) {

			Orders.addAll(result.getQuickOrders());
			for (int i = 0; i < Orders.size(); i++) {
				if (Orders.get(i).getId() == customerOrder.getId()) {
					customer = customerOrder.getCustomer();
					customerOrder = Orders.get(i);
					customerOrder.setCustomer(customer);
					break;
				}
			}

		} else if (scheduledUser != null) {
			Orders.addAll(result.getScheduledOrder());

			for (int i = 0; i < Orders.size(); i++) {
				if (Orders.get(i).getId() == customerOrder.getId()) {
					customer = customerOrder.getCustomer();
					customerOrder = Orders.get(i);
					customerOrder.setCustomer(customer);
					break;
				}
			}
		}

		fragment = new ShowMenuFragment(customerOrder);
		CustomerUtils.nextFragment(fragment, context.getSupportFragmentManager(), false);
	}
}