package nl.hs_hague.urbangame;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import nl.hs_hague.urbangame.database.DatabaseHandler;
import nl.hs_hague.urbangame.model.User;

import static android.content.ContentValues.TAG;

public class RegisterDialog extends DialogFragment {
    private DatabaseHandler db;
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;
    private UserProfileChangeRequest fbUPCR;
    boolean didRegisterWork;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View convertView = inflater.inflate(R.layout.dialog_register, null);

        builder.setView(convertView).setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        EditText etEmail = (EditText) getDialog().findViewById(R.id.editTextEmail);
                        EditText etPass1 = (EditText) getDialog().findViewById(R.id.editTextPass1);
                        EditText etPass2 = (EditText) getDialog().findViewById(R.id.editTextPass2);

                        String email = etEmail.getText().toString();
                        String pass1 = etPass1.getText().toString();
                        String pass2 = etPass2.getText().toString();

                        if(!email.equals("") && !pass1.equals("")) {

                            if(pass1.equals(pass2) && pass1.length()>5) {


                                fbAuth = FirebaseAuth.getInstance();


                                fbAuth.createUserWithEmailAndPassword(email, pass1)
                                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                Log.d(TAG, "IT WORKS");
                                                didRegisterWork = true;

                                                if (!task.isSuccessful()) {
                                                    didRegisterWork = false;
                                                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                                                    //Toast.makeText(getContext(), R.string.registration_failed, Toast.LENGTH_LONG).show();


                                                }

                                            }
                                        });

                                if(didRegisterWork){
                                    Toast.makeText(getContext(), R.string.registration_successful, Toast.LENGTH_LONG).show();
                                }
                                else {
                                    //Toast.makeText(getContext(), R.string.registration_failed, Toast.LENGTH_LONG).show();

                                }

                            }

                            else{
                                Toast.makeText(getContext(), R.string.passwords_unmatching, Toast.LENGTH_LONG).show();
                            }
                        }

                        else{
                            Toast.makeText(getContext(), R.string.fields_unfilled, Toast.LENGTH_LONG).show();

                        }



                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();

                    }
                });



        return builder.create();
    }

    public void makeToast(String _message){
        Toast.makeText(getContext(), _message, Toast.LENGTH_LONG).show();

    }
}
