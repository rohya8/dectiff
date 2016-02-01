package com.rns.tiffeat.mobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class AboutUsFragment extends Fragment implements AndroidConstants {

	private WebView aboutweb;
	private CustomerOrder customerOrder;
	private View rootview;

	public AboutUsFragment(CustomerOrder custOrder) {
		customerOrder = custOrder;
	}

	public AboutUsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootview = inflater.inflate(R.layout.fragment_aboutus, container, false);

		aboutweb = (WebView) rootview.findViewById(R.id.aboutus_webview);

		WebSettings webSettings = aboutweb.getSettings();
		webSettings.setJavaScriptEnabled(true);

		aboutweb.loadUrl("file:///android_asset/aboutUs.html");

		WebViewClient webViewClient = new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url != null && url.contains("tiffeat.com")) {
					nextActivity();
					return true;
				}
				return false;
			}
		};
		aboutweb.setWebViewClient(webViewClient);

		return rootview;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), FONT);
		fontChanger.replaceFonts((ViewGroup) this.getView());
	}

	private void nextActivity() {

		Fragment fragment = null;
		if (customerOrder != null)
			fragment = new FirstTimeUse(customerOrder);
		else
			fragment = new FirstTimeUse();

		CustomerUtils.nextFragment(fragment, getFragmentManager(), false);
	}

}
