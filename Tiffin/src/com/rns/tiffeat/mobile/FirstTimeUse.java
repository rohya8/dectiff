package com.rns.tiffeat.mobile;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.rns.tiffeat.mobile.adapter.PlacesAutoCompleteAdapter;
import com.rns.tiffeat.mobile.asynctask.GetVendorsForAreaAsynctask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealFormat;

public class FirstTimeUse extends Fragment implements AndroidConstants {

	private ListView listview;
	private View rootview;
	private Button searchvendor;
	private GetVendorsForAreaAsynctask getVendorsForAreaAsynctask;
	private CustomerOrder customerOrder;
	private AutoCompleteTextView actvAreas;
	private TextView text;

	public FirstTimeUse(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public FirstTimeUse() {
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootview = inflater.inflate(R.layout.fragment_firsttimeuse, container, false);

		if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);

		} else {

			initialise();

			if (customerOrder != null && customerOrder.getMealFormat() != null) {
				if (MealFormat.SCHEDULED.equals(customerOrder.getMealFormat()) && customerOrder.getId() != 0) {
					changeScheduleOrder();
				}
			}
			searchvendor.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if (!Validation.isNetworkAvailable(getActivity())) {
						Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
					} else {
						InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
						inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

						String area = actvAreas.getText().toString();
						if (!TextUtils.isEmpty(area)) {
							text.setVisibility(View.VISIBLE);
//							getVendorsForAreaAsynctask = new GetVendorsForAreaAsynctask(getActivity(), listview, text, customerOrder);
//							getVendorsForAreaAsynctask.execute(area);

						} else
							CustomerUtils.alertbox(TIFFEAT, "Please Enter Area ", getActivity());
					}
				}

			});
		}
		return rootview;

	}

	private void changeScheduleOrder() {

		if (customerOrder.getLocation() == null) {
			return;
		}
		actvAreas.setText(customerOrder.getLocation().getAddress());
//		getVendorsForAreaAsynctask = new GetVendorsForAreaAsynctask(getActivity(), listview, text, customerOrder);
//		getVendorsForAreaAsynctask.execute(customerOrder.getLocation().getAddress());
	}

	private void initialise() {

		actvAreas = (AutoCompleteTextView) rootview.findViewById(R.id.first_time_use_area_autoCompleteTextView);
		listview = (ListView) rootview.findViewById(R.id.first_time_used_listView);
		searchvendor = (Button) rootview.findViewById(R.id.first_time_use_search_button);
		actvAreas.setThreshold(1);
		text = (TextView) rootview.findViewById(R.id.first_time_area_textView);
		getNearbyPlaces();
	}

	private void getNearbyPlaces() {
		actvAreas.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.whitetext_list_item));
		actvAreas.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				actvAreas.setSelection(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), FONT);
		fontChanger.replaceFonts((ViewGroup) this.getView());
	}

}
