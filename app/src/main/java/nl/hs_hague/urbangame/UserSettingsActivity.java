package nl.hs_hague.urbangame;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserSettingsActivity extends AppCompatActivity {

    FirebaseAuth mAuthListener;
    FirebaseUser fbUser;
    Button confirmButton;



    EditText etUsername, etEmail, etPass, etPass2;

    String newUsername, newEmail, newPass, newPass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        confirmButton = (Button)findViewById(R.id.btnConfirm);



        confirmButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        if (fbUser != null) {



                            etUsername = (EditText) findViewById(R.id.etUsername);
                            etEmail = (EditText) findViewById(R.id.etEmail);
                            etPass = (EditText) findViewById(R.id.etPass);
                            etPass2 = (EditText) findViewById(R.id.etPass2);

                            newUsername = etUsername.getText().toString();
                            newEmail = etEmail.getText().toString();
                            newPass = etPass.getText().toString();
                            newPass2 = etPass2.getText().toString();



                            if(!newUsername.isEmpty()){

                                UserProfileChangeRequest fbUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(newUsername)
                                        .build();

                                fbUser.updateProfile(fbUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("profileUpdateUsername", "User profile updated.");
                                                    //Toast.makeText(getApplicationContext(), fbUser.getDisplayName(), Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                                

                            }

                            if(!newEmail.isEmpty() && newEmail.contains("@")){



                                fbUser.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("email update", "User email address updated.");
                                        }
                                    }
                                });
                            }

                            if(!newPass.equals("") && newPass.equals(newPass2)){
                                fbUser.updatePassword(newPass);

                            }
                            Toast.makeText(getApplicationContext(), R.string.profile_updated, Toast.LENGTH_SHORT).show();



                            // User is signed in
                        } else {
                            // No user is signed in
                        }

                    }
                });



    }


    public void UpdateEmail(String _email){
        fbUser.updateEmail(_email);

    }
}
