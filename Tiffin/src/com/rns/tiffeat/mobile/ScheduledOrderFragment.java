package com.rns.tiffeat.mobile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rns.tiffeat.mobile.asynctask.ScheduledOrderAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealType;

public class ScheduledOrderFragment extends Fragment implements OnClickListener, AndroidConstants {

	private RadioButton lunch, dinner, both;
	private EditText lunchaddr;
	private CustomerOrder customerOrder;
	private TextView tiffindesc, name, emailid, phone, amount, wallet;
	private View rootView;
	private Button proceed;
	private Map<MealType, Date> availableMealType;
	private Date lunchdate, dinnerdate;
	private DateFormat dateFormat;

	public ScheduledOrderFragment(CustomerOrder customerOrder, Map<MealType, Date> availableMealType) {
		this.customerOrder = customerOrder;
		this.availableMealType = availableMealType;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	private void getMealDate(Map<MealType, Date> availableMealType2) {

		dateFormat = new SimpleDateFormat("MM-dd-yyyy");

		if (availableMealType2.get("LUNCH") != null) {
			lunchaddr.setVisibility(View.VISIBLE);
			lunch.setText("Lunch ");
			lunch.setVisibility(View.VISIBLE);
		}
		if (availableMealType2.get("DINNER") != null) {
			lunchaddr.setVisibility(View.VISIBLE);
			dinner.setText("Dinner ");
			dinner.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_scheduled_order, container, false);

		if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
		} else {

			initialise();

			lunch.setOnClickListener(this);
			dinner.setOnClickListener(this);
			both.setOnClickListener(this);
			proceed.setOnClickListener(this);
		}
		return rootView;
	}

	private void initialise() {

		lunch = (RadioButton) rootView.findViewById(R.id.scheduled_order_radioButton_lunch);
		dinner = (RadioButton) rootView.findViewById(R.id.scheduled_order_radioButton_dinner);
		both = (RadioButton) rootView.findViewById(R.id.scheduled_order_radioButton_both);

		lunchaddr = (EditText) rootView.findViewById(R.id.scheduled_order_editText_LunchAddress);
		dateFormat = new SimpleDateFormat("MM-dd-yyyy");

		tiffindesc = (TextView) rootView.findViewById(R.id.scheduled_order_editText_TiffinName);
		name = (TextView) rootView.findViewById(R.id.scheduled_order_editText_Name);
		emailid = (TextView) rootView.findViewById(R.id.scheduled_order_editText_Email);
		phone = (TextView) rootView.findViewById(R.id.scheduled_order_editText_Phoneno);
		amount = (TextView) rootView.findViewById(R.id.scheduled_order_editText_Price);
		proceed = (Button) rootView.findViewById(R.id.scheduled_order_proceed_button);
		wallet = (TextView) rootView.findViewById(R.id.scheduled_order_textview_Wallet);

		customerData();
		getMealDate(availableMealType);
	}

	private void customerData() {

		tiffindesc.setText(customerOrder.getMeal().getTitle());
		name.setText(customerOrder.getCustomer().getName());
		emailid.setText(customerOrder.getCustomer().getEmail());
		phone.setText(customerOrder.getCustomer().getPhone());

		if (customerOrder.getCustomer().getBalance() == null)
			wallet.setText(" 0 ");
		else
			wallet.setText(customerOrder.getCustomer().getBalance().toString());

		// if(MealType.BOTH.equals(customerOrder.getMealType()))
		// {
		// both.setVisibility(View.VISIBLE);
		// lunch.setVisibility(View.VISIBLE);
		// dinner.setVisibility(View.VISIBLE);
		//
		// if(lunchdate!=null && dinnerdate!=null)
		// {
		// lunch.setText("Lunch for ( " + dateFormat.format(lunchdate) +" )");
		// dinner.setText("Dinner for ( " + dateFormat.format( dinnerdate)
		// +" )");
		// }
		// }
		// else if(MealType.LUNCH.equals(customerOrder.getMealType()))
		// {
		// lunchaddr.setVisibility(View.VISIBLE);
		// if(lunchdate!=null )
		// lunch.setText("Lunch for ( " + dateFormat.format(lunchdate) +" )");
		// lunch.setVisibility(View.VISIBLE);
		// }
		// else if(MealType.DINNER.equals(customerOrder.getMealType()))
		// {
		// lunchaddr.setVisibility(View.VISIBLE);
		// dinner.setVisibility(View.VISIBLE);
		// if( dinnerdate!=null)
		// dinner.setText("Dinner for ( " + dateFormat.format( dinnerdate)
		// +" )");
		// lunchaddr.setHint("Dinner Address");
		// }

	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.scheduled_order_radioButton_lunch:
			dinner.setChecked(false);
			both.setChecked(false);
			lunchaddr.setVisibility(View.VISIBLE);
			break;

		case R.id.scheduled_order_radioButton_dinner:
			lunch.setChecked(false);
			both.setChecked(false);
			lunchaddr.setVisibility(View.VISIBLE);
			lunchaddr.setHint("Dinner Address");

			break;

		case R.id.scheduled_order_radioButton_both:
			dinner.setChecked(false);
			lunch.setChecked(false);

			lunchaddr.setVisibility(View.VISIBLE);
			lunchaddr.setHint("Enter Address");
			break;

		case R.id.scheduled_order_proceed_button:

			if (!Validation.isNetworkAvailable(getActivity())) {
				Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
			} else {
				if (lunchaddr.getText().toString().equals(""))
					Toast.makeText(getActivity(), " Donot Leave Empty Field ", Toast.LENGTH_SHORT).show();
				else if (lunchaddr.getText().toString().length() <= 8)
					Toast.makeText(getActivity(), " Enter Valid Address ", Toast.LENGTH_SHORT).show();
				else {
					new ScheduledOrderAsyncTask(prepareCustomerOrders(), getActivity()).execute();
				}
			}
			break;

		default:
			break;
		}

	}

	private List<CustomerOrder> prepareCustomerOrders() {
		List<CustomerOrder> scheduledOrders = new ArrayList<CustomerOrder>();
		customerOrder.setAddress(lunchaddr.getText().toString());
		if (lunch != null && lunch.isChecked()) {
			customerOrder.setMealType(MealType.LUNCH);
		} else if (dinner != null && dinner.isChecked()) {
			customerOrder.setMealType(MealType.DINNER);
		} else if (both != null && both.isChecked()) {
			CustomerOrder scheduledOrder = new CustomerOrder();
			scheduledOrder.setAddress(customerOrder.getAddress());
			scheduledOrder.setArea(customerOrder.getArea());
			customerOrder.setMealType(MealType.LUNCH);
			scheduledOrder.setMealType(MealType.DINNER);
			scheduledOrder.setMeal(customerOrder.getMeal());
			scheduledOrder.setCustomer(customerOrder.getCustomer());
			scheduledOrder.setDate(customerOrder.getDate());
			scheduledOrders.add(scheduledOrder);
		}
		scheduledOrders.add(customerOrder);
		return scheduledOrders;
	}

}
