package com.kesari.tkfops.network;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kesari.tkfops.R;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.nispok.snackbar.listeners.ActionSwipeListener;

public class FireToast {



	public static void makeToast(Context context, String message) {
		if (message != null)
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}


	public static void customSnackbar(final Context context,String content,String action) {
		try
		{
			SnackbarManager.show(
					Snackbar.with(context) // context
							.text(content) // text to be displayed
							.textColor(Color.RED) // change the text color
							.color(Color.WHITE) // change the background color
							.actionLabel(action) // action button label
							.actionColor(Color.LTGRAY) // action button label color
							.swipeToDismiss(true) // disable swipe-to-dismiss functionality
							.duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
					, (Activity) context); // activity where it is displayed

		} catch (Exception e) {
			Log.i("customSnackbar", e.getMessage());
		}
	}

	public static void customSnackbarDialog(final Context context, String content, String action, ViewGroup viewGroup, ActionSwipeListener actionClickListener) {
		try
		{
			viewGroup.setVisibility(View.VISIBLE);
			SnackbarManager.show(
					Snackbar.with(context) // context
							.text(content) // text to be displayed
							.textColor(Color.WHITE) // change the text color
							.color(Color.RED) // change the background color
							.actionLabel(action) // action button label
							.actionColor(Color.LTGRAY) // action button label color
							.swipeToDismiss(true) // disable swipe-to-dismiss functionality
							.swipeListener(actionClickListener)
							.duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
					, viewGroup, true); // activity where it is displayed

		} catch (Exception e) {
			Log.i("customSnackbarDialog", e.getMessage());
		}
	}

	public static void customSnackbarWithListner(final Context context,String content,String action,ActionClickListener actionClickListener) {

		try
		{
			SnackbarManager.show(
					Snackbar.with(context) // context
							.text(content) // text to be displayed
							.textColor(Color.RED) // change the text color
							.color(Color.WHITE) // change the background color
							.actionLabel(action) // action button label
							.actionColor(Color.LTGRAY) // action button label color
							.swipeToDismiss(true) // disable swipe-to-dismiss functionality
							.actionListener(actionClickListener) // action button's ActionClickListener
							.duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
					, (Activity) context); // activity where it is displayed
		} catch (Exception e) {
			Log.i("customSnackbarWithListner", e.getMessage());
		}

	}


	public static void customSnackbarHide(final Context context,String content) {
		try
		{
			SnackbarManager.show(
					Snackbar.with(context) // context
							.text(content) // text to be displayed
							.textColor(R.color.blue) // change the text color
							.color(Color.WHITE) // change the background color
							.actionColor(Color.BLUE) // action button label color
							.duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
					, (Activity) context); // activity where it is displayed

		} catch (Exception e) {
			Log.i("customSnackbarHide", e.getMessage());
		}
	}
}