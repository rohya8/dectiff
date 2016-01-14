package com.rns.tiffeat.mobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rns.tiffeat.mobile.asynctask.ScheduleChangeOrderTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class ChangeOrderFragment extends Fragment implements AndroidConstants {

	private View rootView;
	private TextView meal, price, location, timing;
	private EditText address;
	private CustomerOrder customerOrder;
	private Button submit;

	public ChangeOrderFragment(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.activity_changeaddress, container, false);

		initialise();

		prepareScreen();

		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!Validation.isNetworkAvailable(getActivity())) {
					Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
				}
				else{
					if(!TextUtils.isEmpty(address.getText()) || address.getText().length()>8)
						new ScheduleChangeOrderTask(getActivity(), customerOrder).execute();
					else
						CustomerUtils.alertbox(TIFFEAT, "Enter Valid Address", getActivity());
				}
			}
		});

		return rootView;

	}

	private void prepareScreen() {
		if (customerOrder != null) {
			if (customerOrder.getMeal().getTitle() != null)
				meal.setText(customerOrder.getMeal().getTitle());

			if (customerOrder.getMeal().getPrice() != null)
				price.setText(customerOrder.getMeal().getPrice().toString());

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

	private void initialise() {
		meal = (TextView) rootView.findViewById(R.id.changeorder_meal);
		price = (TextView) rootView.findViewById(R.id.changeorder_price);
		location = (TextView) rootView.findViewById(R.id.changeorder_location);
		timing = (TextView) rootView.findViewById(R.id.changeorder_timing);
		address = (EditText) rootView.findViewById(R.id.changeorder_address);
		submit = (Button) rootView.findViewById(R.id.changeorder_button);
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), FONT);
		fontChanger.replaceFonts((ViewGroup) this.getView());
	}

}
