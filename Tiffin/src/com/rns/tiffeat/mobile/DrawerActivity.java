package com.rns.tiffeat.mobile;

import org.springframework.util.CollectionUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.asynctask.GetAreaAsynctask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class DrawerActivity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener, AndroidConstants {

	private Toolbar mToolbar;
	private FragmentDrawer drawerFragment;
	private Customer customer;

	@Override
	public void onBackPressed() {

		FragmentManager fragmentManager = getSupportFragmentManager();
		int backcount = fragmentManager.getBackStackEntryCount();
		if (backcount > 1) {
			super.onBackPressed();
		} else
			finish();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawer);

		mToolbar = (Toolbar) findViewById(R.id.tool_bar);
		if (getIntent().getExtras() != null) {
			String customerJson = (String) getIntent().getExtras().get(AndroidConstants.CUSTOMER_OBJECT);
			customer = new Gson().fromJson(customerJson, Customer.class);
		}
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
		drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
		drawerFragment.setDrawerListener(this);
		displayView(0);
	}

	public void onDrawerItemSelected(View view, int position) {
		displayView(position);

	}

	private void displayView(int position) {

		hideSoftKeyboard();

		customer = CustomerUtils.getCurrentCustomer(DrawerActivity.this);
		if (customer.getName() == null) {
			newUserDrawer(position);
		} else if (!CollectionUtils.isEmpty(customer.getQuickOrders())) {
			quickAndScheduleOrderDrawer(position);
			if ((!CollectionUtils.isEmpty(customer.getScheduledOrder()))) {
				bothOrderDrawer(position);
			}
		} else if ((!CollectionUtils.isEmpty(customer.getScheduledOrder()))) {
			quickAndScheduleOrderDrawer(position);
		}
	}

	private void quickAndScheduleOrderDrawer(int position) {
		Fragment fragment = null;
		String title = getString(R.string.app_name);

		switch (position) {

		case 0:
			
			fragment = new FirstTimeUse(customer);
			break;

		case 1:

			if (customer != null) {
				if (!CollectionUtils.isEmpty(customer.getQuickOrders())) {
					CustomerUtils.clearFragmentStack(getSupportFragmentManager());
					fragment = new QuickOrderHomeScreen(customer);
				} else if (!CollectionUtils.isEmpty(customer.getScheduledOrder())) {
					CustomerUtils.clearFragmentStack(getSupportFragmentManager());
					fragment = new ScheduledUser(customer);
				}
			} else {
				CustomerUtils.alertbox(TIFFEAT, "Sorry You dont have order ", DrawerActivity.this);
				fragment = new FirstTimeUse();
			}
			break;

		case 2:
			fragment = new TermsFragment();
			title = getString(R.string.nav_item_terms);
			break;

		case 3:
			fragment = new AboutUsFragment();
			title = getString(R.string.nav_item_terms);
			break;

		case 4:

			fragment = new ContactusFragment();
			title = getString(R.string.nav_item_contactus);
			break;

		case 5:
			if (customer != null) {
				CustomerUtils.logout(this);
				new GetAreaAsynctask(this).execute();
			} else {
				CustomerUtils.alertbox(TIFFEAT, " You Are not Logged In  ", DrawerActivity.this);
				fragment = new FirstTimeUse();
			}
			break;

		default:
			break;
		}

		if (fragment != null) {
			title = "TiffEat";
			getSupportActionBar().setTitle(title);
			if (isFragmentToBeAddedToBackStack(fragment)) {
				CustomerUtils.nextFragment(fragment, getSupportFragmentManager(), false);
			} else {
				CustomerUtils.nextFragment(fragment, getSupportFragmentManager(), true);
			}
		}

	}

	private void newUserDrawer(int position) {

		Fragment fragment = null;
		String title = getString(R.string.app_name);

		switch (position) {

		case 0:
			if (customer == null || TextUtils.isEmpty(customer.getEmail())) {
				fragment = new FirstTimeUse();
			}
			break;

		case 1:
			CustomerOrder customerOrder=new CustomerOrder();
			customerOrder.setId(-10);
			fragment=new LoginFragment(customerOrder);
			break;

		case 2:
			fragment = new TermsFragment();
			title = getString(R.string.nav_item_terms);

			break;

		case 3:
			fragment = new AboutUsFragment();
			title = getString(R.string.nav_item_terms);

			break;

		case 4:

			fragment = new ContactusFragment();
			title = getString(R.string.nav_item_contactus);
			break;

		default:
			break;
		}

		if (fragment != null) {
			title = "TiffEat";
			getSupportActionBar().setTitle(title);
			if (isFragmentToBeAddedToBackStack(fragment)) {
				CustomerUtils.nextFragment(fragment, getSupportFragmentManager(), false);
			} else {
				CustomerUtils.nextFragment(fragment, getSupportFragmentManager(), true);
			}
		}

	}

	private void bothOrderDrawer(int position) {

		Fragment fragment = null;
		String title = getString(R.string.app_name);

		switch (position) {

		case 0:
			fragment = new FirstTimeUse(customer);
			break;

		case 1:

			if (customer != null) {
				if (!CollectionUtils.isEmpty(customer.getQuickOrders())) {
					CustomerUtils.clearFragmentStack(getSupportFragmentManager());
					fragment = new QuickOrderHomeScreen(customer);
				}
			} else {
				CustomerUtils.alertbox(TIFFEAT, "Sorry You dont have order ", DrawerActivity.this);
				fragment = new FirstTimeUse();
			}
			break;

		case 2:

			if (customer != null) {
				if (!CollectionUtils.isEmpty(customer.getScheduledOrder())) {
					CustomerUtils.clearFragmentStack(getSupportFragmentManager());
					fragment = new ScheduledUser(customer);
				}
			} else {
				CustomerUtils.alertbox(TIFFEAT, "Sorry You dont have order ", DrawerActivity.this);
				fragment = new FirstTimeUse();
			}
			break;

		case 3:
			fragment = new TermsFragment();
			title = getString(R.string.nav_item_terms);
			break;

		case 4:
			fragment = new AboutUsFragment();
			title = getString(R.string.nav_item_terms);
			break;

		case 5:

			fragment = new ContactusFragment();
			title = getString(R.string.nav_item_contactus);
			break;

		case 6:
			if (customer != null) {
				CustomerUtils.logout(this);
				new GetAreaAsynctask(this).execute();
			} else {
				CustomerUtils.alertbox(TIFFEAT, " You Are not Logged In  ", DrawerActivity.this);
				fragment = new FirstTimeUse();
				title = "TiffEat";
			}
			break;

		default:
			break;
		}

		if (fragment != null) {
			title = "TiffEat";
			getSupportActionBar().setTitle(title);
			if (isFragmentToBeAddedToBackStack(fragment)) {
				CustomerUtils.nextFragment(fragment, getSupportFragmentManager(), false);
			} else {
				CustomerUtils.nextFragment(fragment, getSupportFragmentManager(), true);
			}
		}

	}

	private boolean isFragmentToBeAddedToBackStack(Fragment fragment) {
		return fragment instanceof TermsFragment || fragment instanceof ContactusFragment || fragment instanceof AboutUsFragment
				|| fragment instanceof QuickOrderHomeScreen || fragment instanceof ScheduledUser;
	}

	public void setContentView(View view) {
		super.setContentView(view);
		FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), FONT);
		fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
	}

	public void hideSoftKeyboard() {
		if (getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

	// private void newUserDrawer(int position) {
	//
	// Fragment fragment = null;
	// String title = getString(R.string.app_name);
	//
	// switch (position) {
	//
	// case 0:
	//
	// if (customer == null || TextUtils.isEmpty(customer.getEmail())) {
	// fragment = new FirstTimeUse();
	// title = "TiffEat";
	// } else if (!CollectionUtils.isEmpty(customer.getScheduledOrder())) {
	// CustomerUtils.clearFragmentStack(getSupportFragmentManager());
	// fragment = new ScheduledUser(customer, false);
	// title = "TiffEat";
	// } else {
	// CustomerUtils.clearFragmentStack(getSupportFragmentManager());
	// fragment = new QuickOrderHomeScreen(customer);
	// title = "TiffEat";
	// }
	// break;
	//
	// case 1:
	//
	// if (customer != null) {
	// if (!CollectionUtils.isEmpty(customer.getQuickOrders())) {
	// CustomerUtils.clearFragmentStack(getSupportFragmentManager());
	// fragment = new QuickOrderHomeScreen(customer);
	// title = "My Orders";
	// }
	// } else {
	// CustomerUtils.alertbox(TIFFEAT, "Sorry You dont have order ",
	// DrawerActivity.this);
	// fragment = new FirstTimeUse();
	// title = "TiffEat";
	// }
	// break;
	//
	// case 2:
	// fragment = new TermsFragment();
	// title = getString(R.string.nav_item_terms);
	// break;
	//
	// case 3:
	// fragment = new AboutUsFragment();
	// title = getString(R.string.nav_item_terms);
	// break;
	//
	// case 4:
	//
	// fragment = new ContactusFragment();
	// title = getString(R.string.nav_item_contactus);
	// break;
	//
	// case 5:
	// if (customer != null) {
	// CustomerUtils.logout(this);
	// new GetAreaAsynctask(this).execute();
	// } else {
	// CustomerUtils.alertbox(TIFFEAT, " You Are not Logged In  ",
	// DrawerActivity.this);
	// fragment = new FirstTimeUse();
	// title = "TiffEat";
	// }
	// break;
	//
	// default:
	// break;
	// }
	//
	// if (fragment != null) {
	// title = "TiffEat";
	// getSupportActionBar().setTitle(title);
	// if (isFragmentToBeAddedToBackStack(fragment)) {
	// CustomerUtils.nextFragment(fragment, getSupportFragmentManager(), false);
	// } else {
	// CustomerUtils.nextFragment(fragment, getSupportFragmentManager(), true);
	// }
	// }
	//
	//
	// }

}