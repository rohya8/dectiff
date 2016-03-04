package com.rns.tiffeat.mobile.asynctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.ShowMenuFragment;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.DailyContent;

public class GetMealMenuAsyncTask extends AsyncTask<String, String, DailyContent> implements AndroidConstants {

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
	protected DailyContent doInBackground(String... arg0) {
		try {
			/*Customer currentCustomer = CustomerUtils.getCurrentCustomer(context);
			Customer latestCustomer = currentCustomer;
			latestCustomer = CustomerServerUtils.getCurrentCustomer(currentCustomer);*/
			DailyContent result = new Gson().fromJson(CustomerServerUtils.getMealMenuAndroid(customerOrder), DailyContent.class);
			if (result == null) {
				result = new DailyContent();
			}
			return result;
			//return latestCustomer;
		} catch (Exception e) {
			CustomerUtils.exceptionOccurred(e.getMessage(), getClass().getSimpleName());
		}

		return null;
	}

	@Override
	protected void onPostExecute(DailyContent content) {
		super.onPostExecute(content);
		progressDialog.dismiss();

		if (content == null) {
			CustomerUtils.alertbox(TIFFEAT, ERROR_FETCHING_DATA, context);
			return;
		}
		Fragment fragment = null;

		/*if (quickOrderHome != null) {
			extractCustomerOrder(content.getQuickOrders());

		} else {
			extractCustomerOrder(content.getScheduledOrder());
		}*/
		customerOrder.setContent(content);
		fragment = new ShowMenuFragment(customerOrder);
		CustomerUtils.nextFragment(fragment, context.getSupportFragmentManager(), false);
	}

	/*private void extractCustomerOrder(List<CustomerOrder> orders) {
		if (CollectionUtils.isEmpty(orders)) {
			return;
		}

		for (CustomerOrder order : orders) {
			if (order.getMeal().getId() == customerOrder.getMeal().getId()) {
				customerOrder = order;
				break;
			}
		}
	}*/
}