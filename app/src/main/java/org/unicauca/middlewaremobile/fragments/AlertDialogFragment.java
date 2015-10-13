package org.unicauca.middlewaremobile.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import org.unicauca.middlewaremobile.actions.UserActionListener;

public class AlertDialogFragment extends DialogFragment {

	public static final String MESSAGE_TITLE = "title";
	public static final String MESSAGE_KEY = "message";
	public static final String POSITIVE_LABEL_KEY = "positiveLabel";
	public static final String NEGATIVE_LABEL_KEY = "negativeLabel";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		String title = getArguments().getString(MESSAGE_TITLE);
		String message = getArguments().getString(MESSAGE_KEY);
		String positiveLabel = getArguments().getString(POSITIVE_LABEL_KEY);
		String negativeLabel = getArguments().getString(NEGATIVE_LABEL_KEY);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(title);
		builder.setMessage(message);

		if (positiveLabel == null) {
			positiveLabel = "OK";
		}

		// Positive button
		builder.setPositiveButton(positiveLabel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// ((UserActionListener)
						// getActivity()).onPositive(getTag());
						dismiss();
					}
				});

		if (negativeLabel != null) {
			// Negative Button
			builder.setNegativeButton(negativeLabel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							((UserActionListener) getActivity())
									.onNegative(getTag());
						}
					});
		}

		return builder.create();
	}

}
