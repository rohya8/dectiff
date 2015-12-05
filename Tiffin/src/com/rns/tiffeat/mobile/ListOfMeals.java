package com.rns.tiffeat.mobile;

import java.util.Date;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.rns.tiffeat.mobile.adapter.ListOfMealAdapter;
import com.rns.tiffeat.mobile.asynctask.ScheduleChangeOrderTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.Meal;
import com.rns.tiffeat.web.bo.domain.MealFormat;
import com.rns.tiffeat.web.bo.domain.MealType;
import com.rns.tiffeat.web.bo.domain.Vendor;

public class ListOfMeals extends Fragment implements AndroidConstants {

	private ListView listview;
	private Vendor vendor;
	private TextView vendorName;
	private CustomerOrder customerOrder;
	private Map<MealType, Date> availableMealType;
	private View view;

	public ListOfMeals(Vendor vendorobj, CustomerOrder customerOrder) {
		this.vendor = vendorobj;
		this.customerOrder = customerOrder;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_list_of_meals, container, false);

		if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
		} else {
			initialise();

			vendorName.setText(vendor.getName() + "  offers : ");

			ListOfMealAdapter Adapter = new ListOfMealAdapter(getActivity(), R.layout.activity_first_time_used_adapter, vendor.getMeals(), customerOrder);
			listview.setAdapter(Adapter);

			listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

					if (!Validation.isNetworkAvailable(getActivity())) {
						Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
					} else {
						if (CollectionUtils.isEmpty(vendor.getMeals())) {
							return;
						}
						Meal meal = vendor.getMeals().get(position);
						meal.setVendor(vendor);

						Fragment fragment = null;
						if (customerOrder != null && customerOrder.getMealFormat() != null) {
							if (MealFormat.SCHEDULED.equals(customerOrder.getMealFormat())) {

								if (customerOrder.getId() == 0) {
									
									availableMealType.put(customerOrder.getMealType(), customerOrder.getDate());
									fragment = new ScheduledOrderFragment(customerOrder, availableMealType);
									CustomerUtils.nextFragment(fragment, getFragmentManager(), true);
								} else
									new ScheduleChangeOrderTask(getActivity(), customerOrder, meal).execute();
							}
						} else {
							fragment = new SelectType(meal, customerOrder);
							CustomerUtils.nextFragment(fragment, getFragmentManager(), true);
						}

					}
				}
			});
		}
		return view;

	}

	private void initialise() {
		vendorName = (TextView) view.findViewById(R.id.list_of_meals_vendor_name_textView);
		listview = (ListView) view.findViewById(R.id.list_of_meals_listView);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		CustomerUtils.changeFont(getActivity().getAssets(), this);
//		FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), FONT);
//		fontChanger.replaceFonts((ViewGroup) this.getView());
	}

}
