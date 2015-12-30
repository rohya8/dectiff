package com.rns.tiffeat.mobile;

import java.util.List;

import org.springframework.util.CollectionUtils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.Toast;

import com.rns.tiffeat.mobile.adapter.PlacesAutoCompleteAdapter;
import com.rns.tiffeat.mobile.asynctask.GetVendorsForAreaAsynctask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealFormat;

public class FirstTimeUse extends Fragment implements AndroidConstants {

	private ListView listview;
	private View view;
	private Button searchvendor;
	private GetVendorsForAreaAsynctask getVendorsForAreaAsynctask;
	private String area;
	private CustomerOrder customerOrder;
	private AutoCompleteTextView actvAreas;
	private TextView text;
	private Customer customer;

	public FirstTimeUse(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public FirstTimeUse() {
	}

	public FirstTimeUse(Customer customer) {
		this.customer = customer;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.activity_first_time_use, container, false);

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

						// String addr = actvAreas.getText().toString();
						// getLocationFromAddress(addr);

						String area = actvAreas.getText().toString();
						if (!TextUtils.isEmpty(area)) {
							text.setVisibility(View.VISIBLE);
							if (customer != null) {
								customerOrder = new CustomerOrder();
								if (!CollectionUtils.isEmpty(customer.getQuickOrders())) {
									customerOrder = customer.getQuickOrders().get(0);
								} else if ((!CollectionUtils.isEmpty(customer.getScheduledOrder()))) {
									customerOrder = customer.getScheduledOrder().get(0);
								}

								customerOrder.setCustomer(customer);
							}
							getVendorsForAreaAsynctask = new GetVendorsForAreaAsynctask(getActivity(), listview, text, FirstTimeUse.this, customerOrder);
							getVendorsForAreaAsynctask.execute(area);

						} else
							CustomerUtils.alertbox(TIFFEAT, "Please Enter Area ", getActivity());
					}
				}

			});
		}
		return view;

	}

	private void changeScheduleOrder() {

		actvAreas.setText(customerOrder.getLocation().getAddress());
		getVendorsForAreaAsynctask = new GetVendorsForAreaAsynctask(getActivity(), listview, text, FirstTimeUse.this, customerOrder);
		getVendorsForAreaAsynctask.execute(area);
	}

	private void initialise() {

		actvAreas = (AutoCompleteTextView) view.findViewById(R.id.first_time_use_area_autoCompleteTextView);
		listview = (ListView) view.findViewById(R.id.first_time_used_listView);
		searchvendor = (Button) view.findViewById(R.id.first_time_use_search_button);
		actvAreas.setThreshold(2);
		text = (TextView) view.findViewById(R.id.first_time_area_textView);
		getAreaName();

	}

	private void getAreaName() {
		// AutocompleteTvAdapter adapter = new
		// AutocompleteTvAdapter(getActivity(),
		// android.R.layout.simple_dropdown_item_1line,
		// CoreServerUtils.areaNames, FONT);
		// actvAreas.setThreshold(1);
		// actvAreas.setAdapter(adapter);

		getNearbyPlaces();
	}

	private void getNearbyPlaces() {
		actvAreas.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.list_item));

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

	public void getLocationFromAddress(String strAddress) {

		Geocoder coder = new Geocoder(getActivity());
		List<Address> address;

		try {
			address = coder.getFromLocationName(strAddress, 5);
			if (address == null) {
			}
			Address location = address.get(0);
			double lati = location.getLatitude();
			double longi = location.getLongitude();
			Toast.makeText(getActivity(), " latitude " + lati + " longitude " + longi, Toast.LENGTH_LONG).show();
		} catch (Exception e) {
		}
	}

}
