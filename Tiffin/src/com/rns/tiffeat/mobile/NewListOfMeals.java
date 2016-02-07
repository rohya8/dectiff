package com.rns.tiffeat.mobile;

import java.util.List;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.rns.tiffeat.mobile.adapter.NewListOfMealAdapter;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.Meal;

public class NewListOfMeals extends Fragment implements AndroidConstants {

	private ListView listview;
	private CustomerOrder customerOrder;
	private int lastTopValue = 1;
	private View rootview;
	private List<Meal> mealobj;
	private TextView meal;
	private NewListOfMealAdapter adapter;

	public NewListOfMeals(CustomerOrder customerOrder, List<Meal> meals) {
		this.customerOrder = customerOrder;
		this.mealobj = meals;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootview = inflater.inflate(R.layout.new_fragment_listofmeal, container, false);

		if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
		} else {
			initialise();
			adapter=new NewListOfMealAdapter(getActivity(), R.layout.new_activity_list_of_meals_adapter, mealobj, customerOrder);
			// = new NewListOfMealAdapter(getActivity(),
			// R.layout.new_activity_list_of_meals_adapter, );

			LayoutInflater inflate = getActivity().getLayoutInflater();
			ViewGroup header = (ViewGroup) inflate.inflate(R.layout.custom_header, listview, false);
			listview.addHeaderView(header, null, false);
			listview.setAdapter(adapter);
			meal = (TextView) header.findViewById(R.id.new_list_of_meals_mealtype);
			//area = (TextView) header.findViewById(R.id.new_list_of_meals_area);
			listview.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView arg0, int arg1) {

				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					Rect rect = new Rect();
					meal.getLocalVisibleRect(rect);
					if (lastTopValue != rect.top) {
						lastTopValue = rect.top;
						meal.setY((float) (rect.top / 2.0));
					}

				}
			});

		}
		return rootview;
	}

	private void initialise() {
		listview = (ListView) rootview.findViewById(R.id.new_list_of_meals_listView);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		CustomerUtils.changeFont(getActivity().getAssets(), this);
	}

}
