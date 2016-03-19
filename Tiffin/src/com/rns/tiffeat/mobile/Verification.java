package com.rns.tiffeat.mobile;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Verification extends Fragment {

	private View rootview;
	Button register;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		 rootview =  inflater.inflate(R.layout.activity_verification, container, false);
		 register = (Button)rootview.findViewById(R.id.verification_register_button);
		 
		 return rootview;
	}
}


