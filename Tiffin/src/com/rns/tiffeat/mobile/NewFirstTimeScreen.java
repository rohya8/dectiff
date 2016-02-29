package com.rns.tiffeat.mobile;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rns.tiffeat.mobile.adapter.PlacesAutoCompleteAdapter;
import com.rns.tiffeat.mobile.asynctask.GetVendorsForAreaAsynctask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealFormat;
import com.rns.tiffeat.web.bo.domain.MealType;
import com.rns.tiffeat.web.google.Location;
import com.rns.tiffeat.web.util.CommonUtil;

public class NewFirstTimeScreen extends Fragment implements AndroidConstants {

	private View rootview;
	private Button searchmeal;
	private CustomerOrder customerOrder;
	private AutoCompleteTextView actvAreas;
	private TextView from;
	private Spinner spday, sptiming, spformat;
	private LinearLayout layout;
	private ArrayAdapter<CharSequence> adapter, adapter1, adapter2;

	private TextView getLocation;
	private LocationTracker tracker;

	public NewFirstTimeScreen() {
	}

	public NewFirstTimeScreen(CustomerOrder customerOrder2) {
		this.customerOrder = customerOrder2;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootview = inflater.inflate(R.layout.new_fragment_firsttimeuse, container, false);

		initialise();
		spinnerData();
		searchmeal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

				String area = actvAreas.getText().toString();
				if (!TextUtils.isEmpty(area)) {
					if (customerOrder == null) {
						customerOrder = new CustomerOrder();
					}

					if (spformat.getSelectedItem().toString().equals("Try a meal"))
						customerOrder.setMealFormat(MealFormat.QUICK);
					else
						customerOrder.setMealFormat(MealFormat.SCHEDULED);

					String mealtype = sptiming.getSelectedItem().toString();
					if (mealtype.equals("Both (Lunch And Dinner)"))
						customerOrder.setMealType(MealType.BOTH);
					else if (mealtype.equals("Dinner"))
						customerOrder.setMealType(MealType.DINNER);
					else
						customerOrder.setMealType(MealType.LUNCH);

					if (spday.getVisibility() == View.VISIBLE) {
						if (spday.getSelectedItem().toString().equals("Today's")) {
							customerOrder.setDate(new Date());
						} else {
							customerOrder.setDate(CommonUtil.addDay());
						}
					}

					String locat = actvAreas.getText().toString();
					Location location = new Location();
					location.setAddress(locat);
					customerOrder.setLocation(location);

					new GetVendorsForAreaAsynctask(getActivity(), customerOrder).execute();

				} else
					CustomerUtils.alertbox(TIFFEAT, "Please Enter Area ", getActivity());
			}

		});

		getLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				tracker = new LocationTracker(getActivity());
				// check if location is available
				if (tracker.isLocationEnabled) {
					double latitude = tracker.getLatitude();
					double longitude = tracker.getLongitude();
					//	Toast.makeText(getActivity(), "Your Location is Latitude= " + latitude + " Longitude= " + longitude, Toast.LENGTH_LONG).show();
					String addres = getCompleteAddressString(latitude, longitude);
					actvAreas.setText(addres);
				} else {
					// show dialog box to user to enable location
					tracker.askToOnLocation();
				}
			}
		});

		return rootview;

	}

	private void initialise() {

		actvAreas = (AutoCompleteTextView) rootview.findViewById(R.id.new_first_time_use_area_autoCompleteTextView);
		searchmeal = (Button) rootview.findViewById(R.id.new_first_time_use_search_button);
		spday = (Spinner) rootview.findViewById(R.id.new_first_time_day_spinner);
		sptiming = (Spinner) rootview.findViewById(R.id.new_first_time_timing_spinner);
		actvAreas.setThreshold(1);
		from = (TextView) rootview.findViewById(R.id.new_first_time_from_textview);
		spformat = (Spinner) rootview.findViewById(R.id.new_first_time_format_spinner);
		layout = (LinearLayout) rootview.findViewById(R.id.new_first_time_layout);
		getLocation = (TextView) rootview.findViewById(R.id.getlocation);

		layout.setVisibility(View.VISIBLE);
		from.setVisibility(View.GONE);
		actvAreas.setDropDownWidth(getResources().getDisplayMetrics().widthPixels);
		getNearbyPlaces();
		spinnerClick();
	}

	private void spinnerData() {
		adapter = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_day, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(R.layout.blacktext_list_item);
		spday.setAdapter(adapter);

		adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_timing, android.R.layout.simple_spinner_item);
		adapter1.setDropDownViewResource(R.layout.blacktext_list_item);
		sptiming.setAdapter(adapter1);

		adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_format, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(R.layout.blacktext_list_item);
		spformat.setAdapter(adapter2);
	}

	private void spinnerClick() {
		spday.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
				if (parent.getChildAt(0) != null)
					((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		sptiming.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
				if (parent.getChildAt(0) != null)
					((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		spformat.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {

				TextView textView = (TextView) parent.getChildAt(0);
				if (textView != null) {
					if (textView.getText().equals("Try a meal")) {
						adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_timing, android.R.layout.simple_spinner_item);
						layout.setVisibility(View.VISIBLE);
						from.setVisibility(View.GONE);

					} else {
						adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_timing2, android.R.layout.simple_spinner_item);
						layout.setVisibility(View.GONE);
						from.setVisibility(View.VISIBLE);
					}

					adapter1.setDropDownViewResource(R.layout.blacktext_list_item);
					sptiming.setAdapter(adapter1);
				}
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

	private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
		String strAdd = "";
		Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
		try {
			List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
			if (addresses != null) {
				android.location.Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder("");
				//				for (int i = 1; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
				//					strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
				//				}
				if(returnedAddress.getLocality().equals("Pune")){
					strReturnedAddress.append(returnedAddress.getAddressLine(1)).append(", "+returnedAddress.getLocality()+", "+returnedAddress.getAdminArea()+", "+returnedAddress.getCountryName());

					strAdd = strReturnedAddress.toString().trim();
					Log.w(" location address", "" + strReturnedAddress.toString());
				}
				else{
					Toast.makeText(getActivity(),"Sorry we donot serve in area you requested for" , Toast.LENGTH_SHORT).show();

				}
			} else {
				Log.w(" location address", "No Address returned!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.w(" location address", "Cannot get Address!");
		}
		return strAdd;
	}
}
