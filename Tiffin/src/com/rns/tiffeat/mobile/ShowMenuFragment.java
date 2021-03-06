package com.rns.tiffeat.mobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class ShowMenuFragment extends Fragment implements AndroidConstants {

	private Button alertbtn;
	private TextView roti, sabji, rice, salad, extra, menu, date, availability, vendorname;
	private View rootView;
	private LinearLayout menulayout;
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

		rootView = inflater.inflate(R.layout.fragment_showmenu, container, false);
		initialise();

		if (customerOrder == null || customerOrder.getContent() == null || TextUtils.isEmpty(customerOrder.getContent().getMainItem())
				|| customerOrder.getMealType() == null) {
			CustomerUtils.alertbox(TIFFEAT, "Menu not available for " + customerOrder.getMealType().getDescription(), getActivity());
			availability.setVisibility(View.VISIBLE);
			availability.setText("Menu not available yet..");
			menulayout.setVisibility(View.GONE);
			nextActivity();
			return rootView;
		}

		if (customerOrder.getContent() != null) {
			if (customerOrder.getMealType() != null)
				menu.setText(customerOrder.getMealType().getDescription() + " Menu of " + customerOrder.getMeal().getTitle());
			if (customerOrder.getContent().getDate() != null)
				date.setText(" For : " + CustomerUtils.convertDate(customerOrder.getContent().getDate()));
			if (customerOrder.getContent().getMainItem() != null)
				sabji.setText(customerOrder.getContent().getMainItem());
			if (customerOrder.getContent().getSubItem1() != null)
				roti.setText(customerOrder.getContent().getSubItem1());
			if (customerOrder.getContent().getSubItem2() != null)
				rice.setText(customerOrder.getContent().getSubItem2());
			if (customerOrder.getContent().getSubItem3() != null)
				salad.setText(customerOrder.getContent().getSubItem3());
			if (customerOrder.getContent().getSubItem4() != null)
				extra.setText(customerOrder.getContent().getSubItem4());
			if (customerOrder.getMeal().getVendor() != null)
				vendorname.setText("Vendor name : " + customerOrder.getMeal().getVendor().getName());

		}

		alertbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				nextActivity();
			}

		});
		return rootView;
	}

	private void initialise() {
		roti = (TextView) rootView.findViewById(R.id.roti_status_tv);
		sabji = (TextView) rootView.findViewById(R.id.sabji_status_tv);
		rice = (TextView) rootView.findViewById(R.id.rice_status_tv);
		salad = (TextView) rootView.findViewById(R.id.salad_status_tv);
		extra = (TextView) rootView.findViewById(R.id.extra_status_tv);
		alertbtn = (Button) rootView.findViewById(R.id.menu_done_button);
		menu = (TextView) rootView.findViewById(R.id.showmenu_menu_status_tv);
		date = (TextView) rootView.findViewById(R.id.date_status_tv);
		availability = (TextView) rootView.findViewById(R.id.meal_availability_textView);
		menulayout = (LinearLayout) rootView.findViewById(R.id.menu_layout);
		vendorname = (TextView) rootView.findViewById(R.id.showmenu_vendorname_textView);

		menulayout.setVisibility(View.VISIBLE);
		availability.setVisibility(View.GONE);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		CustomerUtils.changeFont(getActivity().getAssets(), this);
	}

	private void nextActivity() {
		CustomerUtils.removeFragment(getFragmentManager(), this);
	}
}