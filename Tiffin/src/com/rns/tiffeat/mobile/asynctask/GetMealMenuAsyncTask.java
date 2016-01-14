
package com.rns.tiffeat.mobile.asynctask;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

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

	private String quickOrderHome;
	private FragmentActivity context;
	private CustomerOrder customerOrder;
	private ProgressDialog progressDialog;

	public GetMealMenuAsyncTask(FragmentActivity context, String quickHome, String scheduledUser, CustomerOrder customerOrder) {
		this.quickOrderHome = quickHome;
		this.customerOrder = customerOrder;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = UserUtils.showLoadingDialog(context, "Contacting your vendor", "Getting menu....");
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
			CustomerUtils.exceptionOccurred(e.getMessage(), getClass().getSimpleName());
		}

		return null;

	}

	@Override
	protected void onPostExecute(Customer customer) {
		super.onPostExecute(customer);
		progressDialog.dismiss();

		if (customer == null) {
			//Validation.showError(context, ERROR_FETCHING_DATA);
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, context);
			return;
		}
		Fragment fragment = null;

		if (quickOrderHome != null) {
			extractCustomerOrder(customer.getQuickOrders());

		} else {
			extractCustomerOrder(customer.getScheduledOrder());
		}
		customerOrder.setCustomer(customer);
		customerOrder.setTransactionId("-20");
		fragment = new ShowMenuFragment(customerOrder);
		CustomerUtils.nextFragment(fragment, context.getSupportFragmentManager(), false);
	}

	private void extractCustomerOrder(List<CustomerOrder> orders) {
		if (CollectionUtils.isEmpty(orders)) {
			return;
		}

		for (CustomerOrder order : orders) {
			if (order.getMeal().getId() == customerOrder.getMeal().getId()) {
				customerOrder = order;
				break;
			}
		}
	}
}