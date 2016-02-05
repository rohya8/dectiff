package com.rns.tiffeat.mobile;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.rns.tiffeat.mobile.adapter.PlacesAutoCompleteAdapter;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class NewFirstTimeScreen extends Fragment implements AndroidConstants {

	private ListView listview;
	private View rootview;
	private Button searchvendor;
	// private GetVendorsForAreaAsynctask getVendorsForAreaAsynctask;
	private CustomerOrder customerOrder;
	private AutoCompleteTextView actvAreas;
	private TextView text;
	private Spinner spday, sptiming;

	//	public NewFirstTimeScreen(CustomerOrder customerOrder) {
	//		this.customerOrder = customerOrder;
	//	}

	public NewFirstTimeScreen() {
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootview = inflater.inflate(R.layout.new_fragment_firsttimeuse, container, false);

		if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);

		} else {

			initialise();

			// if (customerOrder != null && customerOrder.getMealFormat() !=
			// null) {
			// if (MealFormat.SCHEDULED.equals(customerOrder.getMealFormat()) &&
			// customerOrder.getId() != 0) {
			// changeScheduleOrder();
			// }
			// }
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
							// spday.getSelectedItemPosition();
							// sptiming.getSelectedItem();
							// getVendorsForAreaAsynctask = new
							// GetVendorsForAreaAsynctask(getActivity(),
							// listview, text, customerOrder);
							// getVendorsForAreaAsynctask.execute(area);
							//getActivity().getActionBar().setTitle(spday.getSelectedItem()+" "+sptiming.getSelectedItem());			
							Fragment fragment=new NewListOfMeals();
							CustomerUtils.nextFragment(fragment, getFragmentManager(), false);
						} else
							CustomerUtils.alertbox(TIFFEAT, "Please Enter Area ", getActivity());
					}
				}

			});
		}
		return rootview;

	}

	/*
	 * private void changeScheduleOrder() {
	 * 
	 * if (customerOrder.getLocation() == null) { return; }
	 * actvAreas.setText(customerOrder.getLocation().getAddress());
	 * getVendorsForAreaAsynctask = new
	 * GetVendorsForAreaAsynctask(getActivity(), listview, text, customerOrder);
	 * getVendorsForAreaAsynctask
	 * .execute(customerOrder.getLocation().getAddress()); }
	 */
	private void initialise() {

		actvAreas = (AutoCompleteTextView) rootview.findViewById(R.id.new_first_time_use_area_autoCompleteTextView);
		listview = (ListView) rootview.findViewById(R.id.new_first_time_used_listView);
		searchvendor = (Button) rootview.findViewById(R.id.new_first_time_use_search_button);
		spday = (Spinner) rootview.findViewById(R.id.new_first_time_day_spinner);
		sptiming = (Spinner) rootview.findViewById(R.id.new_first_time_timing_spinner);
		actvAreas.setThreshold(1);
		text = (TextView) rootview.findViewById(R.id.new_first_time_area_textView);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_day, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(R.layout.blacktext_list_item);
		spday.setAdapter(adapter);

		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_timing, android.R.layout.simple_spinner_item);
		adapter1.setDropDownViewResource(R.layout.blacktext_list_item);
		sptiming.setAdapter(adapter1);

		actvAreas.setDropDownWidth(getResources().getDisplayMetrics().widthPixels);
		//spday.getBackground().setColorFilter(getResources().getColor(Color.parseColor("#fff")));
		getNearbyPlaces();

		spday.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
				((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		sptiming.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
				((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

	}

	private void getNearbyPlaces() {
		actvAreas.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.blacktext_list_item));
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
		CustomerUtils.changeFont(getActivity().getAssets(), this);
		}

}
