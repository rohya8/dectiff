package com.rns.tiffeat.mobile;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealFormat;

public class ShowMenuFragment extends Fragment implements AndroidConstants {

	private Button alertbtn;
	private TextView roti, sabji, rice, salad, extra, price;
	private View rootView;
	private CustomerOrder customerOrder;

	public ShowMenuFragment(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.show_menu_fragment, container, false);

		if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
		} else {
			initialise();
			
			if (customerOrder == null || customerOrder.getContent() == null) {
				sabji.setText("Menu not available yet..");
			}

			if (customerOrder.getMeal() != null && customerOrder.getMeal().getPrice() != null) {
				price.setText(customerOrder.getMeal().getPrice().toString());
			}

			if (customerOrder.getContent() != null) {
				//TODO: Show menu date
				//TODO: Add title e.g Non Veg Special Menu for Date
				sabji.setText(customerOrder.getContent().getMainItem());
				roti.setText(customerOrder.getContent().getSubItem1());
				rice.setText(customerOrder.getContent().getSubItem2());
				salad.setText(customerOrder.getContent().getSubItem3());
				extra.setText(customerOrder.getContent().getSubItem4());
			} 
			alertbtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					if (!Validation.isNetworkAvailable(getActivity())) {
						Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
					} else {
						Fragment fragment = null;
						if (customerOrder.getMealFormat().equals(MealFormat.QUICK))
							fragment = new QuickOrderHomeScreen(customerOrder.getCustomer());
						else
							fragment = new ScheduledUser(customerOrder.getCustomer(), true);

						CustomerUtils.nextFragment(fragment, getFragmentManager(), false);
					}
				}
			});
		}
		return rootView;
	}

	public static String getToday(String format) {
		Date date = new Date();
		return new SimpleDateFormat(format).format(date);
	}

	private void initialise() {
		roti = (TextView) rootView.findViewById(R.id.roti_status_tv);
		sabji = (TextView) rootView.findViewById(R.id.sabji_status_tv);
		rice = (TextView) rootView.findViewById(R.id.rice_status_tv);
		salad = (TextView) rootView.findViewById(R.id.salad_status_tv);
		extra = (TextView) rootView.findViewById(R.id.extra_status_tv);
		price = (TextView) rootView.findViewById(R.id.price_status_tv);
		alertbtn = (Button) rootView.findViewById(R.id.menu_done_button);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		CustomerUtils.changeFont(getActivity().getAssets(), this);
	}

}
