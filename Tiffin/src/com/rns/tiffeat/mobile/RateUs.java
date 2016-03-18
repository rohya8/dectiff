package com.rns.tiffeat.mobile;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RateUs 
{
	// Insert your Application Title
		private final static String TITLE = "TiffEat";

		// Insert your Application Package Name
		private final static String PACKAGE_NAME = "com.rns.tiffeat.mobile";

		// Day until the Rate Us Dialog Prompt(Default 2 Days)
		private final static int DAYS_UNTIL_PROMPT = 1;

		// App launches until Rate Us Dialog Prompt(Default 5 Launches)
		private final static int LAUNCHES_UNTIL_PROMPT = 5;

		public static void app_launched(Context mContext) {
			SharedPreferences prefs = mContext.getSharedPreferences("rateus", 0);
			if (prefs.getBoolean("dontshowagain", false)) {
				return;
			}

			SharedPreferences.Editor editor = prefs.edit();

			// Increment launch counter
			long launch_count = prefs.getLong("launch_count", 0) + 1;
			editor.putLong("launch_count", launch_count);

			// Get date of first launch
			Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
			if (date_firstLaunch == 0) {
				date_firstLaunch = System.currentTimeMillis();
				editor.putLong("date_firstlaunch", date_firstLaunch);
			}

			// Wait at least n days before opening
			if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
				if (System.currentTimeMillis() >= date_firstLaunch
						+ (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
					showRateDialog(mContext, editor);
				}
			}

			editor.commit();
		}

		public static void showRateDialog(final Context mContext,
				final SharedPreferences.Editor editor) {
			final Dialog dialog = new Dialog(mContext);
			// Set Dialog Title

			dialog.setContentView(R.layout.activity_rate_us);
			dialog.setTitle(TITLE);

			TextView tv = (TextView) dialog.findViewById(R.id.rateus_textView);
			tv.setText("If you like " + TITLE
					+ ", please give us some stars and comment");

			// First Button
			Button b1 = (Button) dialog.findViewById(R.id.ratenow_button);
			b1.setText("Rate " + TITLE);
			b1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("market://details?id=" + PACKAGE_NAME)));
					dialog.dismiss();
				}
			});

			// Second Button
			Button b2 = (Button) dialog.findViewById(R.id.ratelater_button);
			b2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			// // Third Button
			// Button b3 = new Button(mContext);
			// b3.setText("Stop Bugging me");
			// b3.setOnClickListener(new OnClickListener() {
			// public void onClick(View v) {
			// if (editor != null) {
			// editor.putBoolean("dontshowagain", true);
			// editor.commit();
			// }
			// dialog.dismiss();
			// }
			// });
			// ll.addView(b3);


			// Show Dialog
			dialog.show();
		}

}
