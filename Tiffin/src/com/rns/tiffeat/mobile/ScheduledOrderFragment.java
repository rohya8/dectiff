package com.rns.tiffeat.mobile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
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
	private TextView tiffindesc, name, emailid, phone, wallet;
	private View rootView;
	private Button proceed;
	private Map<MealType, Date> availableMealType;

	public ScheduledOrderFragment(CustomerOrder customerOrder, Map<MealType, Date> availableMealType) {
		this.customerOrder = customerOrder;
		this.availableMealType = availableMealType;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	private void getMealDate(Map<MealType, Date> availableMealTypeDatesMap) {
		List<MealType> availableMealTypes = filterAvailableMealTypes();

		both.setVisibility(View.GONE);
		lunch.setVisibility(View.GONE);
		dinner.setVisibility(View.GONE);

		if (CollectionUtils.isEmpty(availableMealTypes)) {

			Validation.showError(getActivity(), "already scheduled both lunch and dinner");

			return;
		}

		if (customerOrder.getMealType() != null && availableMealTypes.contains(customerOrder.getMealType())) {
			if (MealType.LUNCH.equals(customerOrder.getMealType())) {
				lunchaddr.setVisibility(View.VISIBLE);
				lunch.setText("Lunch ");
				lunch.setVisibility(View.VISIBLE);
			} else if (MealType.DINNER.equals(customerOrder.getMealType())) {
				lunchaddr.setVisibility(View.VISIBLE);
				dinner.setText("Dinner ");
				dinner.setVisibility(View.VISIBLE);
			}
			if (View.VISIBLE == dinner.getVisibility() && View.VISIBLE == lunch.getVisibility()) {
				lunchaddr.setVisibility(View.VISIBLE);
				both.setText("Both ");
				both.setVisibility(View.VISIBLE);
			}
			customerOrder.setDate(availableMealTypeDatesMap.get(availableMealTypes.get(0)));
			return;
		}

		if (availableMealTypeDatesMap.get(MealType.LUNCH) != null && availableMealTypes.contains(MealType.LUNCH)) {
			lunchaddr.setVisibility(View.VISIBLE);
			lunch.setText("Lunch ");
			lunch.setVisibility(View.VISIBLE);
		}
		if (availableMealTypeDatesMap.get(MealType.DINNER) != null && availableMealTypes.contains(MealType.DINNER)) {
			lunchaddr.setVisibility(View.VISIBLE);
			dinner.setText("Dinner ");
			dinner.setVisibility(View.VISIBLE);
		}
		if (View.VISIBLE == dinner.getVisibility() && View.VISIBLE == lunch.getVisibility()) {
			lunchaddr.setVisibility(View.VISIBLE);
			both.setText("Both ");
			both.setVisibility(View.VISIBLE);
		}
		customerOrder.setDate(availableMealTypeDatesMap.get(availableMealTypes.get(0)));
	}

	private List<MealType> filterAvailableMealTypes() {
		if (customerOrder == null || customerOrder.getCustomer() == null) {
			return null;
		}
		List<MealType> mealTypes = new ArrayList<MealType>();
		mealTypes.add(MealType.LUNCH);
		mealTypes.add(MealType.DINNER);
		if (CollectionUtils.isEmpty(customerOrder.getCustomer().getScheduledOrder())) {
			return mealTypes;
		}
		for (CustomerOrder order : customerOrder.getCustomer().getScheduledOrder()) {
			mealTypes.remove(order.getMealType());
		}

		return mealTypes;
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
		new SimpleDateFormat("MM-dd-yyyy");

		tiffindesc = (TextView) rootView.findViewById(R.id.scheduled_order_editText_TiffinName);
		name = (TextView) rootView.findViewById(R.id.scheduled_order_editText_Name);
		emailid = (TextView) rootView.findViewById(R.id.scheduled_order_editText_Email);
		phone = (TextView) rootView.findViewById(R.id.scheduled_order_editText_Phoneno);
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
					Toast.makeText(getActivity(), " Do not Leave Empty Field ", Toast.LENGTH_SHORT).show();
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
