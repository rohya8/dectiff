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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rns.tiffeat.mobile.adapter.AutocompleteTvAdapter;
import com.rns.tiffeat.mobile.asynctask.GetVendorsForAreaAsynctask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CoreServerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
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

	public FirstTimeUse(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public FirstTimeUse() {
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

			if (customerOrder != null && customerOrder.getMealFormat() != null)
				if (MealFormat.SCHEDULED.equals(customerOrder.getMealFormat()) && customerOrder.getId() != 0) {
					changeScheduleOrder();
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
							getVendorsForAreaAsynctask = new GetVendorsForAreaAsynctask(getActivity(), listview, text, FirstTimeUse.this, customerOrder);
							getVendorsForAreaAsynctask.execute(area);

						} else
							Toast.makeText(getActivity(), "Please Enter Area ", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
		return view;

	}

	private void changeScheduleOrder() {

		actvAreas.setText(customerOrder.getArea());
		actvAreas.setEnabled(false);
		getVendorsForAreaAsynctask = new GetVendorsForAreaAsynctask(getActivity(), listview, text, FirstTimeUse.this, customerOrder);
		getVendorsForAreaAsynctask.execute(area);

	}

	private void initialise() {

		actvAreas = (AutoCompleteTextView) view.findViewById(R.id.first_time_use_area_autoCompleteTextView);
		listview = (ListView) view.findViewById(R.id.first_time_used_listView);
		searchvendor = (Button) view.findViewById(R.id.first_time_use_search_button);

		text = (TextView) view.findViewById(R.id.first_time_area_textView);

		getAreaName();

	}

	private void getAreaName() {
		AutocompleteTvAdapter adapter = new AutocompleteTvAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, CoreServerUtils.areaNames, FONT);
		actvAreas.setThreshold(1);
		actvAreas.setAdapter(adapter);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), FONT);
		fontChanger.replaceFonts((ViewGroup) this.getView());
	}
}
