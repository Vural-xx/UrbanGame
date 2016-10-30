package nl.hs_hague.urbangame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


public class MarkersFragment extends DialogFragment {
    String name;
    EditText name_text;
    EditText hint;
    public static final String ARG_ITEM = "item_id";


    public interface MakersFragmentListener {
        /**Setting the interface that we are using in the MapsActivity in order to communicate with it**/
        public void onDialogPositiveClick(DialogFragment dialog, String name, String hint, String id);
    }


    MakersFragmentListener mListener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
       final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View inflator = inflater.inflate(R.layout.layout_markers_fragment, null);



        savedInstanceState = getArguments();
        name = savedInstanceState.getString(ARG_ITEM);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflator)
                // Add action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        name_text = (EditText) inflator.findViewById(R.id.name_marker);
                        hint = (EditText) inflator.findViewById(R.id.hint_marker);
                        mListener.onDialogPositiveClick(MarkersFragment.this,name_text.getText().toString(), hint.getText().toString(),name);


                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MarkersFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (MakersFragmentListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}