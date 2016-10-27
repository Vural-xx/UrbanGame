package nl.hs_hague.urbangame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        ///////////////////////////////facebook Login
        loginButton= (LoginButton) findViewById(R.id.facebook_button);
        callbackManager= CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),"Not valid facebook user",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(),"Error: something went wrong",Toast.LENGTH_SHORT).show();
            }
        });
        ///////////////////////////Listener Login (Normal)
        Button button = (Button) findViewById(R.id.login_button);
        final EditText emailBox = (EditText) findViewById(R.id.editmail) ;
        final EditText passwordBox = (EditText) findViewById(R.id.editPass) ;
        mAuth = FirebaseAuth.getInstance();


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onLogin(emailBox.getText().toString(),passwordBox.getText().toString());
            }
        });
        ////////
    }//on create

    private void onLogin(String mail,String pass) {
        if(mail.equals("")||pass.equals(""))//empty fields
            Toast.makeText(this, "You must fill both fields", Toast.LENGTH_SHORT).show();
        else {
                  mAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                          if(!task.isSuccessful()){
                              Toast.makeText(Login.this, "Unsuccesful login", Toast.LENGTH_SHORT).show();
                          }
                          else{SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                              SharedPreferences.Editor editor=preferences.edit();
                              editor.putBoolean("Login",true).apply();
                              editor.putBoolean("Login_fire",true).apply();
                              goRoomActivity();}
                      }
                  });
        }
    }

    private void goRoomActivity() {
        Intent intent= new Intent(this, RoomListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data )
    {
        super.onActivityResult(requestCode,resultCode,data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }


    public void OpenRegisterDialog(View view){
        RegisterDialog newFragment = new RegisterDialog();
        newFragment.show(getSupportFragmentManager(), "dialog_register");
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Login", "signInWithCredential:onComplete:" + task.isSuccessful());
                        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putBoolean("Login",true).apply();
                        editor.putBoolean("Login_face",true).apply();
                        goRoomActivity();

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.d("Login", "signInWithCredential", task.getException());
                        }

                    }
                });
    }

}
