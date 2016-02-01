package com.rns.tiffeat.mobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.rns.tiffeat.mobile.adapter.ListOfMealAdapter;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.Vendor;

public class ListOfMeals extends Fragment implements AndroidConstants {

	private ListView listview;
	private Vendor vendor;
	private TextView vendorName;
	private CustomerOrder customerOrder;
	private View rootview;

	public ListOfMeals(Vendor vendorobj, CustomerOrder customerOrder) {
		this.vendor = vendorobj;
		this.customerOrder = customerOrder;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootview = inflater.inflate(R.layout.fragment_listofmeals, container, false);

		if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
		} else {
			initialise();
			vendorName.setText(vendor.getName() + "  offers : ");
			ListOfMealAdapter Adapter = new ListOfMealAdapter(getActivity(), R.layout.activity_list_of_meals_adapter, vendor.getMeals(), customerOrder);
			listview.setAdapter(Adapter);
		}
		return rootview;
	}

	private void initialise() {
		vendorName = (TextView) rootview.findViewById(R.id.list_of_meals_vendor_name_textView);
		listview = (ListView) rootview.findViewById(R.id.list_of_meals_listView);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		CustomerUtils.changeFont(getActivity().getAssets(), this);
	}

}
