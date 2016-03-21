package com.rns.tiffeat.mobile;

import com.rns.tiffeat.mobile.asynctask.LoginAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Verification extends Fragment implements AndroidConstants {

	private View rootview;
	private Button register;
	private TextView email;
	private String verificatiocode;
	private CustomerOrder customerOrders;
	private EditText secretcode;
	
	
	
	public Verification(CustomerOrder customerOrder,String code) 
	{   
		super();
		customerOrders = customerOrder; 
		verificatiocode = code;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		 rootview =  inflater.inflate(R.layout.activity_verification, container, false);
		 register = (Button)rootview.findViewById(R.id.verification_register_button);
		 email = (TextView)rootview.findViewById(R.id.customer_email_textView1);
		 secretcode = (EditText)rootview.findViewById(R.id.verificationcode_editText);
		 email.setText(customerOrders.getCustomer().getEmail());
		 register.setOnClickListener(new OnClickListener() 
		 {
			@Override
			public void onClick(View v) 
			{
				if(!secretcode.getText().equals(verificatiocode) || TextUtils.isEmpty(secretcode.getText()))
				{
					CustomerUtils.alertbox(TIFFEAT, "Please enter correct verification code.", getActivity());
					return;
				}
					new LoginAsyncTask(getActivity(),customerOrders, REGISTRATION_FRAGMENT).execute();
			}
		});
		 return rootview;
	}
}


