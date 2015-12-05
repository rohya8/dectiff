package com.rns.tiffeat.mobile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rns.tiffeat.mobile.asynctask.ValidateQuickOrderAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealType;
import com.rns.tiffeat.web.bo.domain.PaymentType;

public class QuickOrderFragment extends Fragment implements OnClickListener,AndroidConstants{

	private RadioButton lunch,dinner,codpayment,onlinepayment; 
	private EditText lunchaddr;

	private Button proceed;
	private CustomerOrder customerOrder; 
	private TextView tiffindesc,name,emailid,phone,amount;
	private View rootView;
	Context context;
	private Date lunchdate, dinnerdate;
	private DateFormat dateFormat;
	private Map<MealType,Date> availableMealType;

	public QuickOrderFragment(CustomerOrder customerOrder, Map<MealType, Date> availableMealType) {
		this.customerOrder=customerOrder;
		this.availableMealType=availableMealType;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	private void getMealDate(Map<MealType, Date> availableMealType2) {

		dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		if(availableMealType2.get("LUNCH")!=null)
		{
			lunchaddr.setVisibility(View.VISIBLE);
			//lunchdate=availableMealType2.get("LUNCH");
			lunch.setText("Lunch for ( " + availableMealType2.get("LUNCH")  +" )");
			lunch.setVisibility(View.VISIBLE);
		}
		if(availableMealType2.get("DINNER")!=null)
		{
			lunchaddr.setVisibility(View.VISIBLE);
			//dinnerdate=availableMealType2.get("DINNER");
			dinner.setText("Dinner for ( " + availableMealType2.get("DINNER") +" )");
			dinner.setVisibility(View.VISIBLE);
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_quick_order, container, false);

		if (!Validation.isNetworkAvailable(getActivity())) {
			Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
		} 
		else{
			initialise();
			lunch.setOnClickListener(this);
			dinner.setOnClickListener(this);
			proceed.setOnClickListener(this);
			codpayment.setOnClickListener(this);
			onlinepayment.setOnClickListener(this);
		}
		return rootView;

	}
	private void initialise( ) {
		lunch=(RadioButton) rootView.findViewById(R.id.quick_order_screen_radioButton_lunch);
		dinner=(RadioButton) rootView.findViewById(R.id.quick_order_screen_radioButton_dinner);
		codpayment=(RadioButton) rootView.findViewById(R.id.quick_order_screen_radioButton_cashondelivery);
		onlinepayment=(RadioButton) rootView.findViewById(R.id.quick_order_screen_radioButton_onlinepayment);

		lunchaddr=(EditText) rootView.findViewById(R.id.quick_order_screen_editText_LunchAddress);
		tiffindesc=(EditText) rootView.findViewById(R.id.quick_order_screen_editText_TiffinName);
		name=(EditText) rootView.findViewById(R.id.quick_order_screen_editText_Name);
		emailid=(EditText) rootView.findViewById(R.id.quick_order_screen_editText_Email);
		phone=(EditText) rootView.findViewById(R.id.quick_order_screen_editText_Phoneno);
		amount=(EditText) rootView.findViewById(R.id.quick_order_screen_editText_Price);
		proceed=(Button) rootView.findViewById(R.id.quick_order_screen_proceed_button);
		customerData();
		getMealDate(availableMealType);
	}

	private void customerData() {

		tiffindesc.setText(customerOrder.getMeal().getTitle());
		name.setText(customerOrder.getCustomer().getName());
		emailid.setText(customerOrder.getCustomer().getEmail());;
		phone.setText(customerOrder.getCustomer().getPhone());
		amount.setText(customerOrder.getMeal().getPrice().toString());

	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) 
		{
		case R.id.quick_order_screen_radioButton_lunch:
			dinner.setChecked(false);

			lunchaddr.setVisibility(View.VISIBLE);
			lunchaddr.setHint("Lunch Address");

			break;

		case R.id.quick_order_screen_radioButton_dinner:
			lunch.setChecked(false);

			lunchaddr.setVisibility(View.VISIBLE);
			lunchaddr.setHint("Dinner Address");
			break;

		case R.id.quick_order_screen_proceed_button:

			if (!Validation.isNetworkAvailable(getActivity())) {
				Validation.showError(getActivity(), ERROR_NO_INTERNET_CONNECTION);
			} 
			else{
				InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(),0);

				if(lunchaddr.getText().toString().equals("") )
					Toast.makeText(getActivity(), " Do not Leave Empty Field ", Toast.LENGTH_SHORT).show();
				else if(lunchaddr.getText().toString().length() <= 8 )
					Toast.makeText(getActivity(), " Enter Valid Address ", Toast.LENGTH_SHORT).show();
				else if(codpayment.isChecked()==false && onlinepayment.isChecked()==false)
					Toast.makeText(getActivity(), " Select A Payment Method ", Toast.LENGTH_SHORT).show();
				else if(dinner.isChecked()==false && lunch.isChecked()==false)
					Toast.makeText(getActivity(), " Select Address ", Toast.LENGTH_SHORT).show();
				else {
					prepareCustomerOrder();
					new ValidateQuickOrderAsyncTask(getActivity(), customerOrder).execute();
				}
			}
			break;

		case R.id.quick_order_screen_radioButton_cashondelivery:
			onlinepayment.setChecked(false);

			customerOrder.setPaymentType(PaymentType.CASH);
			break;

		case R.id.quick_order_screen_radioButton_onlinepayment:
			codpayment.setChecked(false);

			customerOrder.setPaymentType(PaymentType.NETBANKING);
			break;

		default:
			break;
		}

	}

	private void prepareCustomerOrder() 
	{
		customerOrder.setMealType(MealType.LUNCH);
		if(dinner.isChecked()) {
			customerOrder.setMealType(MealType.DINNER);
		}

		customerOrder.setAddress(lunchaddr.getText().toString());

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), FONT);
		fontChanger.replaceFonts((ViewGroup) this.getView());
	}
}
