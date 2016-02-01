package com.rns.tiffeat.mobile;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rns.tiffeat.mobile.adapter.NavigationDrawerAdapter;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.web.bo.domain.Customer;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FragmentDrawer extends Fragment {

	private RecyclerView recyclerView;
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private NavigationDrawerAdapter adapter;
	private View containerView;
	private static String[] titles = null;
	private FragmentDrawerListener drawerListener;
	private TextView name;

	public FragmentDrawer() {

	}

	public void setDrawerListener(FragmentDrawerListener listener) {
		this.drawerListener = listener;
	}

	public static List<NavDrawerItem> getData() {
		List<NavDrawerItem> data = new ArrayList<NavDrawerItem>();
		for (int i = 0; i < titles.length; i++) {
			NavDrawerItem navItem = new NavDrawerItem();
			navItem.setTitle(titles[i]);
			data.add(navItem);
		}
		return data;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Customer currentCustomer = CustomerUtils.getCurrentCustomer(getActivity());
		if (currentCustomer.getName() == null) {
			titles = getActivity().getResources().getStringArray(R.array.nav_drawer_label_new_user);
		} else if (!CollectionUtils.isEmpty(currentCustomer.getQuickOrders()) || !CollectionUtils.isEmpty(currentCustomer.getPreviousOrders())
				|| !CollectionUtils.isEmpty(currentCustomer.getScheduledOrder())) {
			titles = getActivity().getResources().getStringArray(R.array.nav_drawer_label_loggedin_user);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
		recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
		name = (TextView) layout.findViewById(R.id.fragment_drawer_textView);

		Customer currentCustomer = CustomerUtils.getCurrentCustomer(getActivity());
		if (currentCustomer.getName() != null) {
			name.setVisibility(View.VISIBLE);
			name.setText(currentCustomer.getName());
		}

		adapter = new NavigationDrawerAdapter(getActivity(), getData());
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
			@Override
			public void onClick(View view, int position) {
				drawerListener.onDrawerItemSelected(view, position);
				mDrawerLayout.closeDrawer(containerView);
			}

			@Override
			public void onLongClick(View view, int position) {

			}
		}));

		return layout;
	}

	public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
		containerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;
		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActivity().invalidateOptionsMenu();
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				getActivity().invalidateOptionsMenu();
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				super.onDrawerSlide(drawerView, slideOffset);
				toolbar.setAlpha(1 - slideOffset / 2);
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

	}

	public static interface ClickListener {
		public void onClick(View view, int position);

		public void onLongClick(View view, int position);
	}

	static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

		private GestureDetector gestureDetector;
		private ClickListener clickListener;

		public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
			this.clickListener = clickListener;
			gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					return true;
				}

				@Override
				public void onLongPress(MotionEvent e) {
					View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
					if (child != null && clickListener != null) {
						clickListener.onLongClick(child, recyclerView.getChildPosition(child));
					}
				}
			});
		}

		@Override
		public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

			View child = rv.findChildViewUnder(e.getX(), e.getY());
			if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
				clickListener.onClick(child, rv.getChildPosition(child));
			}
			return false;
		}

		@Override
		public void onTouchEvent(RecyclerView rv, MotionEvent e) {
		}

		@Override
		public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

		}

	}

	public interface FragmentDrawerListener {
		public void onDrawerItemSelected(View view, int position);
	}
}
