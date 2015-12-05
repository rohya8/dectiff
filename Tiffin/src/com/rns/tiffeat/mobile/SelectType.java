package com.rns.tiffeat.mobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.asynctask.ExistingUserAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.Meal;
import com.rns.tiffeat.web.bo.domain.MealFormat;

public class SelectType extends Fragment implements AndroidConstants {

	private ImageView scheduled, quick;
	private Meal meal;
	private View view;
	private CustomerOrder customerOrder;

	public SelectType(Meal meal) {
		this.meal = meal;
	}

	public SelectType(Meal meal, CustomerOrder customerOrder2) {
		this.meal = meal;
		customerOrder = customerOrder2;
	}

	public SelectType(CustomerOrder customerOrder2) {
		customerOrder = customerOrder2;
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
						/*
						 * try {
						 * 
						 * if (customerOrder.getCustomer().getName().toString()
						 * == null) {
						 * 
						 * } else if (customerOrder.getMeal() == null) {
						 * customerOrder.setMeal(meal);
						 * customerOrder.setArea(meal.getVendor().getPinCode());
						 * 
						 * }
						 * 
						 * } catch (Exception e) {
						 * 
						 * customerOrder = new CustomerOrder();
						 * customerOrder.setMeal(meal);
						 * customerOrder.setArea(meal.getVendor().getPinCode());
						 * 
						 * }
						 */
						prepareCustomerOrder();

						customerOrder.setMealFormat(MealFormat.QUICK);
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
						prepareCustomerOrder();

						customerOrder.setMealFormat(MealFormat.SCHEDULED);
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

		Bundle b = this.getArguments();
		if (b != null) {
			String objmeal = b.getString("MymealObject");
			meal = new Gson().fromJson(objmeal, Meal.class);
		}

	}

	private void nextActivity() {

		Fragment fragment = null;
		// try {
		//
		// if (customerOrder.getCustomer().getName().toString() != null)
		// new ExistingUserAsyncTask(getActivity(), customerOrder).execute();
		// else {
		// fragment = new FirstTimeUse(customerOrder);
		// CustomerUtils.nextFragment(fragment, getFragmentManager(), false);
		// }
		// } catch (Exception e) {
		// fragment = new LoginFragment(customerOrder);
		// CustomerUtils.nextFragment(fragment, getFragmentManager(), false);
		// }

		if (customerOrder.getCustomer() == null) {
			fragment = new LoginFragment(customerOrder);
			CustomerUtils.nextFragment(fragment, getFragmentManager(), false);
		} else {
			new ExistingUserAsyncTask(getActivity(), customerOrder).execute();
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		CustomerUtils.changeFont(getActivity().getAssets(), this);
		// FontChangeCrawler fontChanger = new
		// FontChangeCrawler(getActivity().getAssets(), FONT);
		// fontChanger.replaceFonts((ViewGroup) this.getView());
	}

	private void prepareCustomerOrder() {
		if (customerOrder == null) {
			customerOrder = new CustomerOrder();
			customerOrder.setMeal(meal);
			customerOrder.setArea(meal.getVendor().getPinCode());
		} else {
			customerOrder.setMeal(meal);
			customerOrder.setArea(meal.getVendor().getPinCode());

		}
	}

}