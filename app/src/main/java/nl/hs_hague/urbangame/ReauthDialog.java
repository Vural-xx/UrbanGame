package nl.hs_hague.urbangame;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ReauthDialog extends DialogFragment
{
    EditText password;

    public interface ReauthDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String password);
    }
      ReauthDialogListener mListener;

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View convertView = inflater.inflate(R.layout.dialog_reauth, null);

        builder.setView(convertView).setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                password = (EditText) convertView.findViewById(R.id.passwordDialog);
                String p=password.getText().toString();

                if ((p != null)&& !p.equals(""))
                     {mListener.onDialogPositiveClick(ReauthDialog.this,p);}
                else
                      {Toast.makeText(getContext(), "No reauthentication, empty password", Toast.LENGTH_SHORT).show();}
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //to do cancel
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
            mListener = (ReauthDialog.ReauthDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
