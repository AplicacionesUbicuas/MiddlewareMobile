package org.unicauca.middlewaremobile.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

import org.unicauca.middlewaremobile.R;

public class ProgressDialogFragment extends DialogFragment {

	public static ProgressDialogFragment newInstance() {
		ProgressDialogFragment frag = new ProgressDialogFragment ();
		return frag;
	}
	
	public ProgressDialogFragment(){
		
	}
	
 
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
 
		final ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage(getString(R.string.loading_text));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		this.setCancelable(false);
		return dialog;
	}

}
