package com.rns.tiffeat.mobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;

public class ContactusFragment extends Fragment implements AndroidConstants {

	private View rootview;
	private TextView phone1,phone2;

	public ContactusFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootview = inflater.inflate(R.layout.fragment_contactus, container, false);
		phone1 = (TextView) rootview.findViewById(R.id.contactus_phone1);
		phone2 = (TextView) rootview.findViewById(R.id.contactus_phone2);

		phone1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				connectCall(phone1.getText().toString());
			}

		});

		phone2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				connectCall(phone2.getText().toString());
			}
		});
		return rootview;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), FONT);
		fontChanger.replaceFonts((ViewGroup) this.getView());
	}

	private void call(String phone) {
		try {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + "+91" +phone));
			callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(callIntent);

		} catch (Exception e) {
			CustomerUtils.alertbox(TIFFEAT, "cannot connect at this moment ", getActivity());
		}

	}

	private void connectCall(final String string) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog.setTitle("Do you want to call?");
		alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				call(string);
			}
		});

		alertDialog.show();
	}


}
