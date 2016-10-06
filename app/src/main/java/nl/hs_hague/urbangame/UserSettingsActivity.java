package nl.hs_hague.urbangame;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserSettingsActivity extends AppCompatActivity {

    FirebaseAuth mAuthListener;
    FirebaseUser fbUser;
    Button confirmButton;


    EditText etUsername = (EditText) findViewById(R.id.etUsername);
    EditText etEmail = (EditText) findViewById(R.id.etEmail);
    EditText etPass = (EditText) findViewById(R.id.etPass);
    EditText etPass2 = (EditText) findViewById(R.id.etPass2);

    String newUsername = etUsername.getText().toString();
    String newEmail = etEmail.getText().toString();
    String newPass = etPass.getText().toString();
    String newPass2 = etPass2.getText().toString();

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

                            if(!newUsername.isEmpty()){
                                

                            }

                            if(!newEmail.isEmpty()){
                                fbUser.updateEmail(newEmail);
                            }

                            if(!newPass.equals("") && newPass.equals(newPass2)){
                                fbUser.updatePassword(newPass);
                            }


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
