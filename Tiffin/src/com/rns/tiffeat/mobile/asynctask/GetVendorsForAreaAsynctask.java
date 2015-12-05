package com.rns.tiffeat.mobile.asynctask;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rns.tiffeat.mobile.FirstTimeUse;
import com.rns.tiffeat.mobile.R;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.adapter.FirstTimeUsedAdapter;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerServerUtils;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.Vendor;

public class GetVendorsForAreaAsynctask extends AsyncTask<String, String, String> implements AndroidConstants {

	private FragmentActivity myactivity;
	private ProgressDialog progressDialog;
	private ListView list;
	private TextView resultTextView;
	private List<Vendor> vendors;
	private GetMealsForVendorAsynctask getMealsForVendorAsynctask;
	private CustomerOrder customerOrder;

	public GetVendorsForAreaAsynctask(FragmentActivity activity, ListView listview, TextView text, FirstTimeUse firstTimeUse, CustomerOrder customerOrder) {
		this.myactivity = activity;
		this.list = listview;
		this.customerOrder = customerOrder;
		this.resultTextView=text;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = UserUtils.showLoadingDialog(myactivity, "Downloading data", "Getting nearby vendors..");
	}

	public List<Vendor> getVendors() {
		return vendors;
	}

	public void setVendors(List<Vendor> vendors) {
		this.vendors = vendors;
	}

	@Override
	protected String doInBackground(String... params) {
		
		if (!Validation.isNetworkAvailable(myactivity)) {
			return null;
		}
		try {
			String result = CustomerServerUtils.getVendorForArea(params[0]);
			return result;
		} catch (Exception e) {
		}
		return null;


	};

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (result == null) {
			Validation.showError(myactivity, ERROR_FETCHING_DATA);
			return;
		}
		Type typelist = new TypeToken<ArrayList<Vendor>>() {}.getType();
		vendors = new Gson().fromJson(result, typelist);
		progressDialog.dismiss();
		if(CollectionUtils.isEmpty(vendors)){
			resultTextView.setText(NO_VENDORS_CURRENTLY_AVAILABLE_IN_THIS_AREA);
			return;
		}
		prepareVendorListAdapter();
		
	}

	public void prepareVendorListAdapter() {
		FirstTimeUsedAdapter Adapter = new FirstTimeUsedAdapter(myactivity, R.layout.activity_first_time_used_adapter, getVendors());
		resultTextView.setText(" Vendors in this Area ");
		list.setVerticalFadingEdgeEnabled(true);
		list.setAdapter(Adapter);

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				try {
					if (!Validation.isNetworkAvailable(myactivity)) {
						Validation.showError(myactivity, ERROR_NO_INTERNET_CONNECTION);
					} else {
						Vendor vendor = (Vendor) parent.getAdapter().getItem(position);
						getMealsForVendorAsynctask = new GetMealsForVendorAsynctask(myactivity, vendor, customerOrder);
						getMealsForVendorAsynctask.execute();
					}
				} catch (Exception e) {
					Toast.makeText(myactivity, "Please try again later!!", Toast.LENGTH_SHORT).show();
					Fragment fragment = new FirstTimeUse();
					CustomerUtils.nextFragment(fragment, myactivity.getSupportFragmentManager(), true);
				}
			}

		});
	}

}
