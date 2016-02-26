package com.rns.tiffeat.mobile;

import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.internal.nu;
import com.rns.tiffeat.mobile.adapter.PlacesAutoCompleteAdapter;
import com.rns.tiffeat.mobile.asynctask.GetVendorsForAreaAsynctask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.google.Location;

public class NewChangeOrderFragment extends Fragment implements AndroidConstants {

	private View rootview;
	private EditText address;
	private CustomerOrder customerOrder;
	private Button submit;
	private AutoCompleteTextView googleLocation;
	private TextView meal;
	private TextView vendorname;
	private TextView timing;

	public NewChangeOrderFragment(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootview = inflater.inflate(R.layout.new_fragment_changeaddress, container, false);

		initialise();

		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(address.getText())) {
					CustomerUtils.alertbox(TIFFEAT, "Enter Valid Address", getActivity());
					return;
				} else if (TextUtils.isEmpty(googleLocation.getText().toString())) {
					CustomerUtils.alertbox(TIFFEAT, "Enter Valid Location", getActivity());
					return;
				}
				prepareCustomerOrder();
				new GetVendorsForAreaAsynctask(getActivity(), customerOrder).execute();
			}

		});

		return rootview;

	}

	private void prepareCustomerOrder() {
		customerOrder.setAddress(address.getText().toString());
		Location location = new Location();
		location.setAddress(this.googleLocation.getText().toString());
		customerOrder.setLocation(location);
		if (customerOrder.getContent()==null) {
			customerOrder.setDate(new Date());	
		}
		else{
			customerOrder.setDate(customerOrder.getContent().getDate());
		}
	}

	private void initialise() {
		meal = (TextView) rootview.findViewById(R.id.new_changeorder_meal_textview);
		vendorname = (TextView) rootview.findViewById(R.id.new_changeorder_vendorname_textview);
		googleLocation = (AutoCompleteTextView) rootview.findViewById(R.id.new_changeorder_location_autoCompleteTextView);
		timing = (TextView) rootview.findViewById(R.id.new_changeorder_mealtype_textview);
		address = (EditText) rootview.findViewById(R.id.new_changeorder_address_edittext);
		submit = (Button) rootview.findViewById(R.id.new_changeorder_button);
		googleLocation.setThreshold(1);
		googleLocation.setDropDownWidth(getResources().getDisplayMetrics().widthPixels);
		prepareScreen();
		getNearbyPlaces();
	}

	private void prepareScreen() {
		if (customerOrder == null || customerOrder.getMeal() == null || customerOrder.getMeal().getVendor() == null || customerOrder.getMealType() == null
				|| customerOrder.getLocation() == null) {
			return;
		}
		meal.setText(customerOrder.getMeal().getTitle());
		vendorname.setText(customerOrder.getMeal().getVendor().getName());
		timing.setText(customerOrder.getMealType().getDescription());
		address.setText(customerOrder.getAddress());
		googleLocation.setText(customerOrder.getLocation().getAddress());
	}

	private void getNearbyPlaces() {
		googleLocation.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.blacktext_list_item));
		googleLocation.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				googleLocation.setSelection(position);
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