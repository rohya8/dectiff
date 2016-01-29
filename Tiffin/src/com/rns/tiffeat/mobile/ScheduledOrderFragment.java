package com.rns.tiffeat.mobile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rns.tiffeat.mobile.asynctask.ScheduledOrderAsyncTask;
import com.rns.tiffeat.mobile.asynctask.ValidateScheduledOrderAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealType;

public class ScheduledOrderFragment extends Fragment implements OnClickListener, AndroidConstants {

	private RadioButton lunch, dinner, both;
	private EditText lunchaddr, phone;
	private CustomerOrder customerOrder;
	private TextView tiffindesc, name, emailid;
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
			CustomerUtils.alertbox(TIFFEAT, "already scheduled both lunch and dinner", getActivity());
			homeActivity();

		}

		filterMealTypes(availableMealTypeDatesMap, availableMealTypes);

		if (customerOrder.getMealType() != null && availableMealTypes.contains(customerOrder.getMealType())) {
			if (MealType.LUNCH.equals(customerOrder.getMealType())) {
				if (CustomerUtils.convertDate(availableMealType.get(MealType.LUNCH)) != null) {
					lunchaddr.setVisibility(View.VISIBLE);
					lunch.setText("Lunch for ( " + CustomerUtils.convertDate(availableMealType.get(MealType.LUNCH)) + " )");
					lunch.setVisibility(View.VISIBLE);
					customerOrder.setDate(availableMealType.get(MealType.LUNCH));
				}
			} else if (MealType.DINNER.equals(customerOrder.getMealType())) {
				if (CustomerUtils.convertDate(availableMealType.get(MealType.DINNER)) != null) {
					lunchaddr.setVisibility(View.VISIBLE);
					dinner.setText("Dinner for ( " + CustomerUtils.convertDate(availableMealType.get(MealType.DINNER)) + " )");
					dinner.setVisibility(View.VISIBLE);
					customerOrder.setDate(availableMealType.get(MealType.DINNER));
				}
			}
			if (View.VISIBLE == dinner.getVisibility() && View.VISIBLE == lunch.getVisibility()) {
				lunchaddr.setVisibility(View.VISIBLE);
				both.setText("Both ");
				both.setVisibility(View.VISIBLE);
				customerOrder.setDate(availableMealType.get(MealType.BOTH));
			}

			return;
		}

		if (availableMealTypeDatesMap.get(MealType.LUNCH) != null && availableMealTypes.contains(MealType.LUNCH)) {

			if (CustomerUtils.convertDate(availableMealType.get(MealType.LUNCH)) != null) {
				lunchaddr.setVisibility(View.VISIBLE);
				lunch.setText("Lunch for ( " + CustomerUtils.convertDate(availableMealType.get(MealType.LUNCH)) + " )");
				lunch.setVisibility(View.VISIBLE);
			}
		}
		if (availableMealTypeDatesMap.get(MealType.DINNER) != null && availableMealTypes.contains(MealType.DINNER)) {
			if (CustomerUtils.convertDate(availableMealType.get(MealType.DINNER)) != null) {
				lunchaddr.setVisibility(View.VISIBLE);
				dinner.setText("Dinner for ( " + CustomerUtils.convertDate(availableMealType.get(MealType.DINNER)) + " )");
				dinner.setVisibility(View.VISIBLE);
			}
		}
		if (View.VISIBLE == dinner.getVisibility() && View.VISIBLE == lunch.getVisibility()) {
			lunchaddr.setVisibility(View.VISIBLE);
			both.setText("Both ");
			both.setVisibility(View.VISIBLE);
		}
		customerOrder.setDate(availableMealTypeDatesMap.get(availableMealTypes.get(0)));
	}

	private void filterMealTypes(Map<MealType, Date> availableMealTypeDatesMap, List<MealType> availableMealTypes) {
		if(availableMealTypeDatesMap == null) {
			return;
		}
		List<MealType> filteredMealTypes = new ArrayList<MealType>();
		for (MealType mealType : availableMealTypes) {
			if (availableMealTypeDatesMap.get(mealType) != null) {
				filteredMealTypes.add(mealType);
			}
		}
		availableMealTypes = filteredMealTypes;
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

			if (View.GONE == dinner.getVisibility() && View.GONE == lunch.getVisibility()) {
				CustomerUtils.alertbox(TIFFEAT, " Meal you have selected is not available", getActivity());
				homeActivity();
			}
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

		tiffindesc = (TextView) rootView.findViewById(R.id.scheduled_order_editText_TiffinName);
		name = (TextView) rootView.findViewById(R.id.scheduled_order_editText_Name);
		emailid = (TextView) rootView.findViewById(R.id.scheduled_order_editText_Email);
		phone = (EditText) rootView.findViewById(R.id.scheduled_order_editText_Phoneno);
		proceed = (Button) rootView.findViewById(R.id.scheduled_order_proceed_button);

		customerData();
		getMealDate(availableMealType);
	}

	private void customerData() {

		if (customerOrder.getMeal() == null || customerOrder.getCustomer() == null) {
			return;
		}
		tiffindesc.setText(customerOrder.getMeal().getTitle());
		name.setText(customerOrder.getCustomer().getName());
		emailid.setText(customerOrder.getCustomer().getEmail());
		lunchaddr.setHint("Enter Address");

		if (customerOrder.getAddress() != null)
			lunchaddr.setText(customerOrder.getAddress());
		else
			lunchaddr.setHint("Enter Address");

		if (customerOrder.getCustomer().getPhone() != null)
			phone.setText(customerOrder.getCustomer().getPhone());
		else
			phone.setHint("Enter Phone Number");

	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.scheduled_order_radioButton_lunch:
			dinner.setChecked(false);
			both.setChecked(false);
			lunchaddr.setHint("Lunch Address");
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
					CustomerUtils.alertbox(TIFFEAT, " Do not Leave Empty Field ", getActivity());
				else if (lunchaddr.getText().toString().length() <= 8)
					CustomerUtils.alertbox(TIFFEAT, " Enter Valid Address ", getActivity());
				else if (!Validation.isPhoneNumber(phone, true))
					CustomerUtils.alertbox(TIFFEAT, " Enter Valid Phone number ", getActivity());
				else {
					if (lunch != null && lunch.isChecked()) {
						customerOrder.setMealType(MealType.LUNCH);
					} else if (dinner != null && dinner.isChecked()) {
						customerOrder.setMealType(MealType.DINNER);
					} else if (both != null && both.isChecked()) {
						customerOrder.setMealType(MealType.BOTH);
					}

					customerOrder.setAddress(lunchaddr.getText().toString());
					customerOrder.getCustomer().setPhone(phone.getText().toString());

					alertbox();
				}
			}
			break;

		default:
			break;
		}

	}

	private void alertbox() {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
		builder.setTitle("TiffEat");
		builder.setMessage("Do you want to proceed ?");

		builder.setNegativeButton("No", null);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!Validation.isNetworkAvailable(getActivity())) {
					Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
				} else {
					if (customerOrder.getCustomer().getBalance() == null || customerOrder.getCustomer().getBalance().compareTo(customerOrder.getMeal().getPrice()) < 0)
						new ValidateScheduledOrderAsyncTask(getActivity(), customerOrder).execute();
					else
						new ScheduledOrderAsyncTask(getActivity(), customerOrder).execute();

				}
			}
		});

		builder.show();
	}

	private void homeActivity() {

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Fragment fragment = null;
						fragment = new ScheduledUser(customerOrder.getCustomer());
						CustomerUtils.nextFragment(fragment, getFragmentManager(), false);

					}
				}, 2000);
			}

		});

	}

}
