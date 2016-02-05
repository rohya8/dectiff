package com.rns.tiffeat.mobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.rns.tiffeat.mobile.adapter.PlacesAutoCompleteAdapter;
import com.rns.tiffeat.mobile.asynctask.ScheduleChangeOrderTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class NewScheduleLunchOrDinnerFragment extends Fragment implements AndroidConstants {

	private View rootview;
	private TextView date ;
	private EditText address;
	//private CustomerOrder customerOrder;
	private Button submit;
	private AutoCompleteTextView location;

	//	public NewChangeOrderFragment(CustomerOrder customerOrder) {
	//		this.customerOrder = customerOrder;
	//	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootview = inflater.inflate(R.layout.new_fragment_schedulelunchordinner, container, false);

		initialise();

		//		prepareScreen();

		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!Validation.isNetworkAvailable(getActivity())) {
					Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
				} else {
					if (TextUtils.isEmpty(address.getText()) || address.getText().length() > 8)
						CustomerUtils.alertbox(TIFFEAT, "Enter Valid Address", getActivity());
					else if(TextUtils.isEmpty(location.getText().toString()) || location.getText().toString().length()<8)
						CustomerUtils.alertbox(TIFFEAT, "Enter Valid Location", getActivity());
					//						new ScheduleChangeOrderTask(getActivity(), customerOrder).execute();
					//else

				}
			}
		});

		return rootview;

	}

	/*private void prepareScreen() {
		if (customerOrder != null) {
			if (customerOrder.getMeal().getTitle() != null)
				meal.setText(customerOrder.getMeal().getTitle());

			if (customerOrder.getMeal().getPrice() != null)
				// price.setText(customerOrder.getMeal().getPrice().toString());

				if (customerOrder.getLocation().getAddress() != null)
					location.setText(customerOrder.getLocation().getAddress());

			if (customerOrder.getMealType() != null)
				timing.setText(customerOrder.getMealType().toString());

			if (customerOrder.getAddress() != null)
				address.setText(customerOrder.getAddress());
			else
				address.setHint("Enter Address");
		}
	}
	 */
	private void initialise() {
		date = (TextView) rootview.findViewById(R.id.new_schedulelunchordinner_from_textview);
		location = (AutoCompleteTextView) rootview.findViewById(R.id.new_schedulelunchordinner_location_autoCompleteTextView);
		address = (EditText) rootview.findViewById(R.id.new_schedulelunchordinner_address_edittext);
		submit = (Button) rootview.findViewById(R.id.new_schedulelunchordinner_button);
		location.setThreshold(1);
		location.setDropDownWidth(getResources().getDisplayMetrics().widthPixels);
		getNearbyPlaces();
	}

	private void getNearbyPlaces() {
		location.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.blacktext_list_item));
		location.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				location.setSelection(position);
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