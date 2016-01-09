package com.rns.tiffeat.mobile;

import org.apache.commons.collections.CollectionUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rns.tiffeat.mobile.asynctask.ExistingUserAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.Meal;
import com.rns.tiffeat.web.bo.domain.MealFormat;

public class SelectType extends Fragment implements AndroidConstants {

	private ImageView scheduled, quick;
	private View view;
	private CustomerOrder customerOrder;

	public SelectType(CustomerOrder customerOrder3) {
		this.customerOrder = customerOrder3;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_select_type, container, false);

		if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
		} else {

			initialise();

			quick.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!Validation.isNetworkAvailable(getActivity())) {
						Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
					} else {
						prepareCustomerOrder(MealFormat.QUICK);
						nextActivity();
					}
				}

			});

			scheduled.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (!Validation.isNetworkAvailable(getActivity())) {
						Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
					} else {
						if (customerOrder.getCustomer() != null) {

							if (CollectionUtils.isNotEmpty(customerOrder.getCustomer().getScheduledOrder()) && customerOrder.getCustomer().getScheduledOrder().size() == 2) {
								CustomerUtils.alertbox(TIFFEAT, "You have already scheduled meal for luch and dinner", getActivity());
								Fragment fragment = null;
								fragment = new ScheduledUser(customerOrder.getCustomer());
								CustomerUtils.nextFragment(fragment, getFragmentManager(), false);

							}
						}
						prepareCustomerOrder(MealFormat.SCHEDULED);
						nextActivity();
					}
				}

			});
		}
		return view;
	}

	private void initialise() {

		scheduled = (ImageView) view.findViewById(R.id.select_type_scheduled_imageView);
		quick = (ImageView) view.findViewById(R.id.select_type_quick_imageView);

	}

	private void nextActivity() {

		if (customerOrder.getCustomer() == null) {
			Fragment fragment = null;
			fragment = new LoginFragment(customerOrder);
			CustomerUtils.nextFragment(fragment, getFragmentManager(), false);
		} else {
			if (customerOrder.getCustomer() != null) {
				new ExistingUserAsyncTask(getActivity(), customerOrder).execute();
			}
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		CustomerUtils.changeFont(getActivity().getAssets(), this);
	}

	private void prepareCustomerOrder(MealFormat mealFormat) {
		if (customerOrder == null) {
			customerOrder = new CustomerOrder();
		}
		customerOrder.setMealFormat(mealFormat);
	}

}