package com.rns.tiffeat.mobile;

import org.springframework.util.StringUtils;

import com.rns.tiffeat.mobile.asynctask.ExistingUserAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.Meal;
import com.rns.tiffeat.web.bo.domain.MealFormat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

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
		this.customerOrder = customerOrder2;
	}

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

		// Bundle b = this.getArguments();
		// if (b != null) {
		// String objmeal = b.getString("MymealObject");
		// meal = new Gson().fromJson(objmeal, Meal.class);
		// }

	}

	private void nextActivity() {

		Fragment fragment = null;

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

	private void prepareCustomerOrder(MealFormat mealFormat) {
		if (customerOrder == null) {
			customerOrder = new CustomerOrder();
		}
		if (customerOrder.getMeal() == null) {
			customerOrder.setMeal(meal);
		}
		if (customerOrder.getArea() == null) {
			customerOrder.setArea(meal.getVendor().getPinCode());
		}
		if (customerOrder.getMealFormat() == null) {
			customerOrder.setMealFormat(mealFormat);
		}
	}

}